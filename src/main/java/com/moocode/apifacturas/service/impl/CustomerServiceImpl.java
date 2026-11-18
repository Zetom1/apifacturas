package com.moocode.apifacturas.service.impl;

import com.moocode.apifacturas.dto.request.CustomerProfileUpdateDTO;
import com.moocode.apifacturas.dto.request.CustomerRequestDTO;
import com.moocode.apifacturas.dto.response.CustomerResponseDTO;
import com.moocode.apifacturas.dto.response.PageResponseDTO;
import com.moocode.apifacturas.entity.CustomerEntity;
import com.moocode.apifacturas.entity.PersonEntity;
import com.moocode.apifacturas.exception.DuplicateResourceException;
import com.moocode.apifacturas.exception.ResourceNotFoundException;
import com.moocode.apifacturas.mapper.CustomerMapper;
import com.moocode.apifacturas.repository.CustomerRepository;
import com.moocode.apifacturas.repository.PersonRepository;
import com.moocode.apifacturas.service.CustomerService;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PersonRepository personRepository;
    private final EntityManager entityManager;

    public CustomerServiceImpl(CustomerRepository customerRepository, PersonRepository personRepository, EntityManager entityManager) {
        this.customerRepository = customerRepository;
        this.personRepository = personRepository;
        this.entityManager = entityManager;
    }

    @Override
    public CustomerResponseDTO create(CustomerRequestDTO request) {
        String email = normalizeEmail(request.email());
        if (personRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Ya existe una persona con ese correo electrónico.");
        }

        PersonEntity person = CustomerMapper.toPerson(request);
        personRepository.save(person);
        CustomerEntity customer = customerRepository.saveAndFlush(
                new CustomerEntity(UUID.randomUUID(), person));

        // El consecutivo IDENTITY lo genera PostgreSQL; refresh permite construir CLIE-000001.
        entityManager.refresh(customer);
        return CustomerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> findActive() {
        return customerRepository.findAllByActiveTrueOrderByPublicNumberAsc().stream()
                .map(CustomerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO findByCode(String customerCode) {
        return CustomerMapper.toResponse(getCustomer(customerCode));
    }

    @Override
    public CustomerResponseDTO update(String customerCode, CustomerRequestDTO request) {
        CustomerEntity customer = getCustomer(customerCode);
        String email = normalizeEmail(request.email());
        if (personRepository.existsByEmailIgnoreCaseAndPersonUuidNot(
                email, customer.getPerson().getPersonUuid())) {
            throw new DuplicateResourceException("Ya existe una persona con ese correo electrónico.");
        }
        CustomerMapper.updatePersonEntity(customer.getPerson(), request);
        return CustomerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponseDTO deactivate(String customerCode) {
        CustomerEntity customer = getCustomer(customerCode);
        customer.deactivate();
        customer.getPerson().deactivate();
        return CustomerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponseDTO activate(String customerCode) {
        CustomerEntity customer = getCustomer(customerCode);
        customer.activate();
        customer.getPerson().activate();
        return CustomerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO findOwnProfile(String email) {
        CustomerEntity customer = customerRepository.findByPerson_EmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el perfil del cliente autenticado."));
        return CustomerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponseDTO updateOwnProfile(String email, CustomerProfileUpdateDTO request) {
        CustomerEntity customer = customerRepository.findByPerson_EmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el perfil del cliente autenticado."));
        customer.getPerson().updateContact(request.firstName().trim(), request.paternalSurname().trim(),
                request.maternalSurname() == null ? null : request.maternalSurname().trim(),
                customer.getPerson().getEmail(), request.phone() == null ? null : request.phone().trim());
        return CustomerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<CustomerResponseDTO> search(String search, Boolean active, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("La página debe ser no negativa y el tamaño debe estar entre 1 y 100.");
        }
        String normalized = search == null || search.isBlank() ? null : search.trim();
        Page<CustomerEntity> result = customerRepository.search(normalized, active, PageRequest.of(page, size));
        return new PageResponseDTO<>(result.getContent().stream().map(CustomerMapper::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(),
                result.isFirst(), result.isLast());
    }

    private CustomerEntity getCustomer(String customerCode) {
        long number = parsePublicNumber(customerCode);
        return customerRepository.findByPublicNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el cliente " + customerCode + "."));
    }

    private long parsePublicNumber(String customerCode) {
        if (customerCode == null || !customerCode.matches("CLIE-[0-9]{6,}")) {
            throw new ResourceNotFoundException("El folio de cliente no tiene un formato válido.");
        }
        try {
            return Long.parseLong(customerCode.substring(5));
        } catch (NumberFormatException exception) {
            throw new ResourceNotFoundException("El folio de cliente no tiene un formato válido.");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}