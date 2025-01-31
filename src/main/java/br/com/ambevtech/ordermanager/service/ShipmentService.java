package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.exception.ShipmentNotFoundException;
import br.com.ambevtech.ordermanager.model.Shipment;
import br.com.ambevtech.ordermanager.model.enums.ShipmentStatus;
import br.com.ambevtech.ordermanager.repository.ShipmentRepository;
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
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    /**
     * Obtém todas as entregas com paginação.
     */
    public Page<Shipment> getAllShipments(Pageable pageable) {
        log.info("Buscando todas as entregas - Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return shipmentRepository.findAll(pageable);
    }

    /**
     * Obtém uma entrega pelo ID.
     */
    public Shipment getShipmentById(UUID id) {
        log.info("Buscando entrega com ID: {}", id);
        return shipmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Entrega com ID {} não encontrada!", id);
                    return new ShipmentNotFoundException("Entrega não encontrada: " + id);
                });
    }

    /**
     * Registra uma nova entrega.
     */
    @Transactional
    public Shipment createShipment(Shipment shipment) {
        log.info("Registrando nova entrega para o pedido ID: {}", shipment.getOrder().getId());
        return shipmentRepository.save(shipment);
    }

    /**
     * Atualiza os detalhes de uma entrega existente.
     */
    @Transactional
    public Shipment updateShipment(UUID id, Shipment updatedShipment) {
        log.info("Atualizando entrega com ID: {}", id);

        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Entrega com ID {} não encontrada!", id);
                    return new ShipmentNotFoundException("Entrega não encontrada: " + id);
                });

        existingShipment.setTrackingNumber(updatedShipment.getTrackingNumber());
        existingShipment.setStatus(updatedShipment.getStatus());
        existingShipment.setShippedDate(updatedShipment.getShippedDate());

        return shipmentRepository.save(existingShipment);
    }

    /**
     * Atualiza o status da entrega.
     */
    @Transactional
    public Shipment updateShipmentStatus(UUID id, ShipmentStatus status) {
        log.info("Atualizando status da entrega com ID: {} para {}", id, status);

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Entrega com ID {} não encontrada!", id);
                    return new ShipmentNotFoundException("Entrega não encontrada: " + id);
                });

        shipment.setStatus(status);
        return shipmentRepository.save(shipment);
    }

    /**
     * Remove uma entrega pelo ID.
     */
    @Transactional
    public void deleteShipment(UUID id) {
        log.info("Removendo entrega com ID: {}", id);

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Entrega com ID {} não encontrada!", id);
                    return new ShipmentNotFoundException("Entrega não encontrada: " + id);
                });

        shipmentRepository.delete(shipment);
        log.info("Entrega removida com sucesso! ID: {}", id);
    }
}
