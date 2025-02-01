package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.CustomerRequestDTO;
import br.com.ambevtech.ordermanager.dto.CustomerResponseDTO;
import br.com.ambevtech.ordermanager.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<Page<CustomerResponseDTO>> getAllCustomers(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        log.info("Buscando todos os clientes - Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<CustomerResponseDTO> customers = customerService.getAllCustomers(pageable);

        log.info("Clientes encontrados: {}", customers.getTotalElements());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable UUID id) {
        log.info("Buscando cliente com ID: {}", id);
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO dto) {
        log.info("Criando um novo cliente: {}", dto.name());
        return ResponseEntity.ok(customerService.createCustomer(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable UUID id, @Valid @RequestBody CustomerRequestDTO dto) {
        log.info("Atualizando cliente com ID: {}", id);
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        log.info("Removendo cliente com ID: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}