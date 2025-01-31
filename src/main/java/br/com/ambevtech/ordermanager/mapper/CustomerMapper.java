package br.com.ambevtech.ordermanager.mapper;

import br.com.ambevtech.ordermanager.dto.CustomerRequestDTO;
import br.com.ambevtech.ordermanager.dto.CustomerResponseDTO;
import br.com.ambevtech.ordermanager.model.Customer;

public class CustomerMapper {

    public static CustomerResponseDTO toResponseDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoneNumber()
        );
    }

    public static Customer toEntity(CustomerRequestDTO dto) {
        return new Customer(
                null,
                dto.name(),
                dto.email(),
                dto.phoneNumber(),
                null
        );
    }
}
