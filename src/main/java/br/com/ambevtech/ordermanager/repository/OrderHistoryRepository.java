package br.com.ambevtech.ordermanager.repository;

import br.com.ambevtech.ordermanager.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, UUID> {
    List<OrderHistory> findByOrderIdOrderByTimestampDesc(UUID orderId);
}
