package br.com.ambevtech.ordermanager.repository;

import br.com.ambevtech.ordermanager.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    boolean existsByCustomerIdAndOrderDate(UUID customerId, LocalDateTime orderDate);

    boolean existsByExternalOrderId(String externalOrderId); // ✅ Método corrigido

    Optional<Order> findByExternalOrderId(String externalOrderId); // ✅ Método para buscar pedido externo

    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") UUID orderId);
}
