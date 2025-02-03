package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.exception.SupplierDuplicateEmailException;
import br.com.ambevtech.ordermanager.exception.SupplierNotFoundException;
import br.com.ambevtech.ordermanager.model.Supplier;
import br.com.ambevtech.ordermanager.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Fornecedor Teste");
        supplier.setEmail("teste@email.com");
        supplier.setPhoneNumber("123456789");
    }

    @Test
    void getAllSuppliers_ShouldReturnPageOfSuppliers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Supplier> supplierPage = new PageImpl<>(List.of(supplier));

        when(supplierRepository.findAll(pageable)).thenReturn(supplierPage);

        Page<Supplier> result = supplierService.getAllSuppliers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(supplierRepository, times(1)).findAll(pageable);
    }

    @Test
    void createSupplier_ShouldSaveAndReturnSupplier() {
        when(supplierRepository.existsByEmail(supplier.getEmail())).thenReturn(false);
        when(supplierRepository.save(supplier)).thenReturn(supplier);

        Supplier result = supplierService.createSupplier(supplier);

        assertNotNull(result);
        assertEquals("Fornecedor Teste", result.getName());
        verify(supplierRepository, times(1)).save(supplier);
    }

    @Test
    void createSupplier_ShouldThrowException_WhenEmailExists() {
        when(supplierRepository.existsByEmail(supplier.getEmail())).thenReturn(true);

        assertThrows(SupplierDuplicateEmailException.class, () -> supplierService.createSupplier(supplier));

        verify(supplierRepository, never()).save(any());
    }

    @Test
    void updateSupplier_ShouldUpdateAndReturnSupplier() {
        Supplier updatedSupplier = new Supplier();
        updatedSupplier.setName("Fornecedor Atualizado");
        updatedSupplier.setEmail("atualizado@email.com");
        updatedSupplier.setPhoneNumber("987654321");

        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(updatedSupplier);

        Supplier result = supplierService.updateSupplier(supplier.getId(), updatedSupplier);

        assertNotNull(result);
        assertEquals("Fornecedor Atualizado", result.getName());
        assertEquals("atualizado@email.com", result.getEmail());
        assertEquals("987654321", result.getPhoneNumber());

        verify(supplierRepository, times(1)).save(supplier);
    }

    @Test
    void updateSupplier_ShouldThrowException_WhenSupplierNotFound() {
        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.updateSupplier(supplier.getId(), supplier));

        verify(supplierRepository, never()).save(any());
    }

    @Test
    void deleteSupplier_ShouldDeleteSupplier() {
        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));

        supplierService.deleteSupplier(supplier.getId());

        verify(supplierRepository, times(1)).delete(supplier);
    }

    @Test
    void deleteSupplier_ShouldThrowException_WhenSupplierNotFound() {
        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.deleteSupplier(supplier.getId()));

        verify(supplierRepository, never()).delete(any());
    }

    @Test
    void getSupplierById_ShouldReturnSupplier() {
        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));

        Supplier result = supplierService.getSupplierById(supplier.getId());

        assertNotNull(result);
        assertEquals(supplier.getId(), result.getId());
    }

    @Test
    void getSupplierById_ShouldThrowException_WhenSupplierNotFound() {
        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.getSupplierById(supplier.getId()));
    }
}
