package br.com.ambevtech.ordermanager.repository;

import br.com.ambevtech.ordermanager.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByEmail(String email);
}