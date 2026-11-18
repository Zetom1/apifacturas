package com.moocode.apifacturas.mapper;

import com.moocode.apifacturas.dto.request.CustomerRequestDTO;
import com.moocode.apifacturas.dto.response.CustomerResponseDTO;
import com.moocode.apifacturas.entity.CustomerEntity;
import com.moocode.apifacturas.entity.PersonEntity;

import java.util.Locale;

public final class CustomerMapper {

    private CustomerMapper(){}

    public static PersonEntity toPerson(CustomerRequestDTO request) {
        return new PersonEntity(
                java.util.UUID.randomUUID(),
                request.firstName().trim(),
                request.paternalSurname().trim(),
                blankToNull(request.maternalSurname()),
                request.email().trim().toLowerCase(java.util.Locale.ROOT),
                blankToNull(request.phone())
                );
    }

    public static void updatePersonEntity(PersonEntity person, CustomerRequestDTO request) {
        person.updateContact(
                request.firstName().trim(),
                request.paternalSurname().trim(),
                blankToNull(request.maternalSurname()),
                request.email().trim().toLowerCase(Locale.ROOT),
                blankToNull(request.phone())
        );

    }


    public static CustomerResponseDTO toResponse(CustomerEntity customer) {
        PersonEntity person = customer.getPerson();
        return new CustomerResponseDTO(
                "CLIE-%06d".formatted(customer.getPublicNumber()),
                person.getFirstName(),
                person.getPaternalSurname(),
                person.getMaternalSurname(),
                person.getEmail(),
                person.getPhone(),
                customer.isActive(),
                customer.getCreatedAt());
    }

    private static String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
