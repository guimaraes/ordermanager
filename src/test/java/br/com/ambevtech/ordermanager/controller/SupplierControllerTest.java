package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.SupplierRequestDTO;
import br.com.ambevtech.ordermanager.dto.SupplierResponseDTO;
import br.com.ambevtech.ordermanager.mapper.SupplierMapper;
import br.com.ambevtech.ordermanager.model.Supplier;
import br.com.ambevtech.ordermanager.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierControllerTest {

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private SupplierController supplierController;

    private Supplier supplier;
    private SupplierRequestDTO supplierRequestDTO;
    private SupplierResponseDTO supplierResponseDTO;

    @BeforeEach
    void setUp() {
        supplier = new Supplier(1L, "Fornecedor Teste", "fornecedor@email.com", "11999999999", null);
        supplierRequestDTO = new SupplierRequestDTO("Fornecedor Teste", "fornecedor@email.com", "11999999999");
        supplierResponseDTO = new SupplierResponseDTO(1L, "Fornecedor Teste", "fornecedor@email.com", "11999999999");
    }

    @Test
    void getAllSuppliers_ShouldReturnPageOfSuppliers() {
        Page<Supplier> supplierPage = new PageImpl<>(List.of(supplier));
        when(supplierService.getAllSuppliers(any(Pageable.class))).thenReturn(supplierPage);

        Pageable pageable = PageRequest.of(0, 10); // Defina explicitamente a página e o tamanho
        ResponseEntity<Page<SupplierResponseDTO>> response = supplierController.getAllSuppliers(pageable);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(supplierService, times(1)).getAllSuppliers(any(Pageable.class));
    }

    @Test
    void getSupplierById_ShouldReturnSupplier() {
        when(supplierService.getSupplierById(1L)).thenReturn(supplier);

        ResponseEntity<SupplierResponseDTO> response = supplierController.getSupplierById(1L);

        assertNotNull(response.getBody());
        assertEquals(supplier.getId(), response.getBody().id());
        assertEquals(supplier.getName(), response.getBody().name());
        verify(supplierService, times(1)).getSupplierById(1L);
    }

    @Test
    void createSupplier_ShouldReturnCreatedSupplier() {
        when(supplierService.createSupplier(any(Supplier.class))).thenReturn(supplier);

        ResponseEntity<SupplierResponseDTO> response = supplierController.createSupplier(supplierRequestDTO);

        assertNotNull(response.getBody());
        assertEquals(supplier.getId(), response.getBody().id());
        assertEquals(supplier.getName(), response.getBody().name());
        assertEquals(201, response.getStatusCode().value());
        verify(supplierService, times(1)).createSupplier(any(Supplier.class));
    }

    @Test
    void updateSupplier_ShouldReturnUpdatedSupplier() {
        when(supplierService.updateSupplier(anyLong(), any(Supplier.class))).thenReturn(supplier);

        ResponseEntity<SupplierResponseDTO> response = supplierController.updateSupplier(1L, supplierRequestDTO);

        assertNotNull(response.getBody());
        assertEquals(supplier.getId(), response.getBody().id());
        assertEquals(supplier.getName(), response.getBody().name());
        verify(supplierService, times(1)).updateSupplier(anyLong(), any(Supplier.class));
    }

    @Test
    void deleteSupplier_ShouldReturnNoContent() {
        doNothing().when(supplierService).deleteSupplier(1L);

        ResponseEntity<Void> response = supplierController.deleteSupplier(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(supplierService, times(1)).deleteSupplier(1L);
    }
}
