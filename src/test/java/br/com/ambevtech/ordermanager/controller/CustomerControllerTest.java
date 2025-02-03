package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.CustomerRequestDTO;
import br.com.ambevtech.ordermanager.dto.CustomerResponseDTO;
import br.com.ambevtech.ordermanager.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private CustomerService customerService;

    private UUID customerId;
    private CustomerRequestDTO customerRequestDTO;
    private CustomerResponseDTO customerResponseDTO;
    private Page<CustomerResponseDTO> customerPage;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();

        customerRequestDTO = new CustomerRequestDTO(
                "João Silva",
                "joao.silva@example.com",
                "11987654321"
        );

        customerResponseDTO = new CustomerResponseDTO(
                customerId,
                "João Silva",
                "joao.silva@example.com",
                "11987654321"
        );

        customerPage = new PageImpl<>(List.of(customerResponseDTO), PageRequest.of(0, 10), 1);
    }

    @Test
    void getAllCustomers_ShouldReturnPagedCustomers() {
        when(customerService.getAllCustomers(any())).thenReturn(customerPage);

        ResponseEntity<Page<CustomerResponseDTO>> response = customerController.getAllCustomers(PageRequest.of(0, 10));

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(customerService, times(1)).getAllCustomers(any());
    }

    @Test
    void getCustomerById_ShouldReturnCustomer() {
        when(customerService.getCustomerById(customerId)).thenReturn(customerResponseDTO);

        ResponseEntity<CustomerResponseDTO> response = customerController.getCustomerById(customerId);

        assertNotNull(response.getBody());
        assertEquals(customerResponseDTO, response.getBody());
        verify(customerService, times(1)).getCustomerById(customerId);
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() {
        when(customerService.createCustomer(any(CustomerRequestDTO.class))).thenReturn(customerResponseDTO);

        ResponseEntity<CustomerResponseDTO> response = customerController.createCustomer(customerRequestDTO);

        assertNotNull(response.getBody());
        assertEquals(customerResponseDTO, response.getBody());
        verify(customerService, times(1)).createCustomer(any(CustomerRequestDTO.class));
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer() {
        when(customerService.updateCustomer(any(UUID.class), any(CustomerRequestDTO.class)))
                .thenReturn(customerResponseDTO);

        ResponseEntity<CustomerResponseDTO> response = customerController.updateCustomer(customerId, customerRequestDTO);

        assertNotNull(response.getBody());
        assertEquals(customerResponseDTO, response.getBody());
        verify(customerService, times(1)).updateCustomer(any(UUID.class), any(CustomerRequestDTO.class));
    }

    @Test
    void deleteCustomer_ShouldDeleteCustomer() {
        doNothing().when(customerService).deleteCustomer(customerId);

        ResponseEntity<Void> response = customerController.deleteCustomer(customerId);

        assertEquals(204, response.getStatusCode().value());
        verify(customerService, times(1)).deleteCustomer(customerId);
    }
}
