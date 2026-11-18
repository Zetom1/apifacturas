package com.moocode.apifacturas.service;

import com.moocode.apifacturas.dto.request.CustomerProfileUpdateDTO;
import com.moocode.apifacturas.dto.request.CustomerRequestDTO;
import com.moocode.apifacturas.dto.response.CustomerResponseDTO;
import com.moocode.apifacturas.dto.response.PageResponseDTO;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO create(CustomerRequestDTO request);
    List<CustomerResponseDTO> findActive();
    CustomerResponseDTO findByCode(String code);
    CustomerResponseDTO update(String customerCode, CustomerRequestDTO request);
    CustomerResponseDTO deactivate(String customerCode);
    CustomerResponseDTO activate(String customerCode);
    CustomerResponseDTO findOwnProfile(String email);
    CustomerResponseDTO updateOwnProfile(String email, CustomerProfileUpdateDTO request);
    PageResponseDTO<CustomerResponseDTO> search(String search, Boolean active, int page, int size);
}