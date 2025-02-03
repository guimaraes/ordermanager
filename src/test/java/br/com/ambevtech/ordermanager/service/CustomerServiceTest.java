package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.dto.CustomerRequestDTO;
import br.com.ambevtech.ordermanager.dto.CustomerResponseDTO;
import br.com.ambevtech.ordermanager.exception.CustomerNotFoundException;
import br.com.ambevtech.ordermanager.mapper.CustomerMapper;
import br.com.ambevtech.ordermanager.model.Customer;
import br.com.ambevtech.ordermanager.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    private UUID customerId;
    private Customer customer;
    private CustomerRequestDTO customerRequestDTO;
    private CustomerResponseDTO customerResponseDTO;
    private Page<CustomerResponseDTO> customerPage;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();

        customer = new Customer();
        customer.setId(customerId);
        customer.setName("João Silva");
        customer.setEmail("joao.silva@example.com");
        customer.setPhoneNumber("11987654321");

        customerRequestDTO = new CustomerRequestDTO("João Silva", "joao.silva@example.com", "11987654321");
        customerResponseDTO = new CustomerResponseDTO(customerId, "João Silva", "joao.silva@example.com", "11987654321");

        customerPage = new PageImpl<>(List.of(customerResponseDTO), PageRequest.of(0, 10), 1);
    }

    @Test
    void getAllCustomers_ShouldReturnPagedCustomers() {
        when(customerRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(customer)));

        try (var mockStatic = mockStatic(CustomerMapper.class)) {
            mockStatic.when(() -> CustomerMapper.toResponseDTO(any(Customer.class)))
                    .thenReturn(customerResponseDTO);

            Page<CustomerResponseDTO> result = customerService.getAllCustomers(PageRequest.of(0, 10));

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(customerRepository, times(1)).findAll(any(PageRequest.class));
        }
    }

    @Test
    void getCustomerById_ShouldReturnCustomer() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        try (var mockStatic = mockStatic(CustomerMapper.class)) {
            mockStatic.when(() -> CustomerMapper.toResponseDTO(customer))
                    .thenReturn(customerResponseDTO);

            CustomerResponseDTO result = customerService.getCustomerById(customerId);

            assertNotNull(result);
            assertEquals(customerResponseDTO, result);
            verify(customerRepository, times(1)).findById(customerId);
        }
    }

    @Test
    void getCustomerById_ShouldThrowExceptionWhenNotFound() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(customerId));

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void getCustomerByEmail_ShouldReturnCustomer() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        try (var mockStatic = mockStatic(CustomerMapper.class)) {
            mockStatic.when(() -> CustomerMapper.toResponseDTO(customer))
                    .thenReturn(customerResponseDTO);

            CustomerResponseDTO result = customerService.getCustomerByEmail(customer.getEmail());

            assertNotNull(result);
            assertEquals(customerResponseDTO, result);
            verify(customerRepository, times(1)).findByEmail(customer.getEmail());
        }
    }

    @Test
    void getCustomerByEmail_ShouldThrowExceptionWhenNotFound() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerByEmail(customer.getEmail()));

        verify(customerRepository, times(1)).findByEmail(customer.getEmail());
    }

    @Test
    void createCustomer_ShouldCreateAndReturnCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        try (var mockStatic = mockStatic(CustomerMapper.class)) {
            mockStatic.when(() -> CustomerMapper.toEntity(customerRequestDTO))
                    .thenReturn(customer);
            mockStatic.when(() -> CustomerMapper.toResponseDTO(customer))
                    .thenReturn(customerResponseDTO);

            CustomerResponseDTO result = customerService.createCustomer(customerRequestDTO);

            assertNotNull(result);
            assertEquals(customerResponseDTO, result);
            verify(customerRepository, times(1)).save(any(Customer.class));
        }
    }

    @Test
    void updateCustomer_ShouldUpdateAndReturnCustomer() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        try (var mockStatic = mockStatic(CustomerMapper.class)) {
            mockStatic.when(() -> CustomerMapper.toResponseDTO(customer))
                    .thenReturn(customerResponseDTO);

            CustomerResponseDTO result = customerService.updateCustomer(customerId, customerRequestDTO);

            assertNotNull(result);
            assertEquals(customerResponseDTO, result);
            verify(customerRepository, times(1)).findById(customerId);
            verify(customerRepository, times(1)).save(any(Customer.class));
        }
    }

    @Test
    void updateCustomer_ShouldThrowExceptionWhenNotFound() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(customerId, customerRequestDTO));

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_ShouldDeleteCustomer() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(customer);

        customerService.deleteCustomer(customerId);

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    void deleteCustomer_ShouldThrowExceptionWhenNotFound() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(customerId));

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).delete(any(Customer.class));
    }
}
