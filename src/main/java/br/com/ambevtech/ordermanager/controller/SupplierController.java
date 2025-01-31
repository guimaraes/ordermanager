package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.SupplierRequestDTO;
import br.com.ambevtech.ordermanager.dto.SupplierResponseDTO;
import br.com.ambevtech.ordermanager.mapper.SupplierMapper;
import br.com.ambevtech.ordermanager.model.Supplier;
import br.com.ambevtech.ordermanager.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<Page<SupplierResponseDTO>> getAllSuppliers(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        log.info("Buscando todos os fornecedores - Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<SupplierResponseDTO> suppliers = supplierService.getAllSuppliers(pageable)
                .map(SupplierMapper::toResponseDTO);

        log.info("Fornecedores encontrados: {}", suppliers.getTotalElements());
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getSupplierById(@PathVariable Long id) {
        log.info("Buscando fornecedor com ID: {}", id);
        Supplier supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(SupplierMapper.toResponseDTO(supplier));
    }

    @PostMapping
    public ResponseEntity<SupplierResponseDTO> createSupplier(@Valid @RequestBody SupplierRequestDTO dto) {
        log.info("Cadastrando um novo fornecedor: {}", dto.name());

        Supplier supplier = SupplierMapper.toEntity(dto);
        Supplier savedSupplier = supplierService.createSupplier(supplier);

        log.info("Fornecedor cadastrado com sucesso! ID: {}", savedSupplier.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(SupplierMapper.toResponseDTO(savedSupplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierRequestDTO dto) {
        log.info("Atualizando fornecedor com ID: {}", id);

        Supplier updatedSupplier = SupplierMapper.toEntity(dto);
        Supplier savedSupplier = supplierService.updateSupplier(id, updatedSupplier);

        log.info("Fornecedor atualizado com sucesso! ID: {}", savedSupplier.getId());
        return ResponseEntity.ok(SupplierMapper.toResponseDTO(savedSupplier));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        log.info("Removendo fornecedor com ID: {}", id);
        supplierService.deleteSupplier(id);
        log.info("Fornecedor removido com sucesso! ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
