package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.exception.SupplierDuplicateEmailException;
import br.com.ambevtech.ordermanager.exception.SupplierNotFoundException;
import br.com.ambevtech.ordermanager.model.Supplier;
import br.com.ambevtech.ordermanager.repository.SupplierRepository;
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
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public Page<Supplier> getAllSuppliers(Pageable pageable) {
        log.info("Buscando fornecedores - Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return supplierRepository.findAll(pageable);
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        log.info("Cadastrando fornecedor: {}", supplier.getName());

        boolean exists = supplierRepository.existsByEmail(supplier.getEmail());
        if (exists) {
            log.error("Erro ao cadastrar fornecedor: O e-mail {} já está em uso.", supplier.getEmail());
            throw new SupplierDuplicateEmailException("Já existe um fornecedor cadastrado com este e-mail.");
        }

        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier updatedSupplier) {
        log.info("Atualizando fornecedor com ID: {}", id);

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Fornecedor com ID {} não encontrado!", id);
                    return new SupplierNotFoundException("Fornecedor não encontrado: " + id);
                });

        existingSupplier.setName(updatedSupplier.getName());
        existingSupplier.setEmail(updatedSupplier.getEmail());
        existingSupplier.setPhoneNumber(updatedSupplier.getPhoneNumber());

        return supplierRepository.save(existingSupplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Removendo fornecedor com ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Fornecedor com ID {} não encontrado!", id);
                    return new SupplierNotFoundException("Fornecedor não encontrado: " + id);
                });

        supplierRepository.delete(supplier);
        log.info("Fornecedor removido com sucesso! ID: {}", id);
    }

    public Supplier getSupplierById(Long id) {
        log.info("Buscando fornecedor com ID: {}", id);
        return supplierRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Fornecedor com ID {} não encontrado!", id);
                    return new SupplierNotFoundException("Fornecedor não encontrado: " + id);
                });
    }

}
