package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.dto.CustomerRequestDTO;
import br.com.ambevtech.ordermanager.dto.CustomerResponseDTO;
import br.com.ambevtech.ordermanager.exception.CustomerNotFoundException;
import br.com.ambevtech.ordermanager.mapper.CustomerMapper;
import br.com.ambevtech.ordermanager.model.Customer;
import br.com.ambevtech.ordermanager.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado: " + id));
        return CustomerMapper.toResponseDTO(customer);
    }

    public CustomerResponseDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente com e-mail " + email + " não encontrado."));
        return CustomerMapper.toResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        Customer customer = CustomerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerMapper.toResponseDTO(savedCustomer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(UUID id, CustomerRequestDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado: " + id));

        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhoneNumber(dto.phoneNumber());

        Customer updatedCustomer = customerRepository.save(customer);
        return CustomerMapper.toResponseDTO(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado: " + id));

        customerRepository.delete(customer);
    }
}
