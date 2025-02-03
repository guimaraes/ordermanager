package br.com.ambevtech.ordermanager.repository;

import br.com.ambevtech.ordermanager.model.OrderErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderErrorLogRepository extends JpaRepository<OrderErrorLog, Long> {
}
