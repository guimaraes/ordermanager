
package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.dto.CustomerRequestDTO;
import br.com.ambevtech.ordermanager.dto.CustomerResponseDTO;
import br.com.ambevtech.ordermanager.exception.CustomerNotFoundException;
import br.com.ambevtech.ordermanager.mapper.CustomerMapper;
import br.com.ambevtech.ordermanager.model.Customer;
import br.com.ambevtech.ordermanager.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        log.info("Buscando todos os clientes - Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return customerRepository.findAll(pageable).map(CustomerMapper::toResponseDTO);
    }

    public CustomerResponseDTO getCustomerById(UUID id) {
        log.info("Buscando cliente com ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cliente com ID {} não encontrado!", id);
                    return new CustomerNotFoundException("Cliente não encontrado: " + id);
                });

        return CustomerMapper.toResponseDTO(customer);
    }

    public CustomerResponseDTO getCustomerByEmail(String email) {
        log.info("Buscando cliente com e-mail: {}", email);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Cliente com e-mail {} não encontrado!", email);
                    return new CustomerNotFoundException("Cliente com e-mail " + email + " não encontrado.");
                });

        return CustomerMapper.toResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        log.info("Criando novo cliente: {}", dto.name());
        Customer customer = CustomerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Cliente criado com sucesso! ID: {}", savedCustomer.getId());
        return CustomerMapper.toResponseDTO(savedCustomer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(UUID id, CustomerRequestDTO dto) {
        log.info("Atualizando cliente com ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado: " + id));

        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhoneNumber(dto.phoneNumber());

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Cliente atualizado com sucesso! ID: {}", updatedCustomer.getId());
        return CustomerMapper.toResponseDTO(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        log.info("Removendo cliente com ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado: " + id));

        customerRepository.delete(customer);
        log.info("Cliente removido com sucesso! ID: {}", id);
    }
}