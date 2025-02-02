package br.com.ambevtech.ordermanager.repository;

import br.com.ambevtech.ordermanager.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderBatchRepository {

    private final EntityManager entityManager;

    @Transactional
    public void batchInsertOrders(List<Order> orders) {
        int batchSize = 50; // Pode ser ajustado conforme a necessidade

        for (int i = 0; i < orders.size(); i++) {
            entityManager.persist(orders.get(i));

            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
}

