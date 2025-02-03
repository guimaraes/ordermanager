package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.exception.ShipmentNotFoundException;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.Shipment;
import br.com.ambevtech.ordermanager.model.enums.ShipmentStatus;
import br.com.ambevtech.ordermanager.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @InjectMocks
    private ShipmentService shipmentService;

    @Mock
    private ShipmentRepository shipmentRepository;

    private UUID shipmentId;
    private UUID orderId;
    private Shipment shipment;
    private Order order;
    private Page<Shipment> shipmentPage;

    @BeforeEach
    void setUp() {
        shipmentId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        order = new Order();
        order.setId(orderId);

        shipment = new Shipment();
        shipment.setId(shipmentId);
        shipment.setOrder(order);
        shipment.setTrackingNumber("TRACK123");
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setShippedDate(LocalDateTime.now());

        shipmentPage = new PageImpl<>(List.of(shipment), PageRequest.of(0, 10), 1);
    }

    @Test
    void getAllShipments_ShouldReturnPagedShipments() {
        Pageable pageable = PageRequest.of(0, 10);
        when(shipmentRepository.findAll(pageable)).thenReturn(shipmentPage);

        Page<Shipment> result = shipmentService.getAllShipments(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(shipmentRepository, times(1)).findAll(pageable);
    }

    @Test
    void getShipmentById_ShouldReturnShipment() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));

        Shipment result = shipmentService.getShipmentById(shipmentId);

        assertNotNull(result);
        assertEquals(shipment, result);
        verify(shipmentRepository, times(1)).findById(shipmentId);
    }

    @Test
    void getShipmentById_ShouldThrowShipmentNotFoundException() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.empty());

        assertThrows(ShipmentNotFoundException.class, () -> shipmentService.getShipmentById(shipmentId));

        verify(shipmentRepository, times(1)).findById(shipmentId);
    }

    @Test
    void createShipment_ShouldCreateAndReturnShipment() {
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        Shipment result = shipmentService.createShipment(shipment);

        assertNotNull(result);
        assertEquals(shipment, result);
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }

    @Test
    void updateShipment_ShouldUpdateAndReturnShipment() {
        Shipment updatedShipment = new Shipment();
        updatedShipment.setTrackingNumber("TRACK999");
        updatedShipment.setStatus(ShipmentStatus.IN_TRANSIT);
        updatedShipment.setShippedDate(LocalDateTime.now());

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(updatedShipment);

        Shipment result = shipmentService.updateShipment(shipmentId, updatedShipment);

        assertNotNull(result);
        assertEquals(updatedShipment.getTrackingNumber(), result.getTrackingNumber());
        assertEquals(updatedShipment.getStatus(), result.getStatus());
        verify(shipmentRepository, times(1)).findById(shipmentId);
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }

    @Test
    void updateShipment_ShouldThrowShipmentNotFoundException() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.empty());

        assertThrows(ShipmentNotFoundException.class, () -> shipmentService.updateShipment(shipmentId, shipment));

        verify(shipmentRepository, times(1)).findById(shipmentId);
        verify(shipmentRepository, never()).save(any(Shipment.class));
    }

    @Test
    void updateShipmentStatus_ShouldUpdateAndReturnShipment() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        Shipment result = shipmentService.updateShipmentStatus(shipmentId, ShipmentStatus.DELIVERED);

        assertNotNull(result);
        assertEquals(ShipmentStatus.DELIVERED, result.getStatus());
        verify(shipmentRepository, times(1)).findById(shipmentId);
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }

    @Test
    void updateShipmentStatus_ShouldThrowShipmentNotFoundException() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.empty());

        assertThrows(ShipmentNotFoundException.class, () -> shipmentService.updateShipmentStatus(shipmentId, ShipmentStatus.DELIVERED));

        verify(shipmentRepository, times(1)).findById(shipmentId);
        verify(shipmentRepository, never()).save(any(Shipment.class));
    }

    @Test
    void deleteShipment_ShouldDeleteShipment() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        doNothing().when(shipmentRepository).delete(shipment);

        assertDoesNotThrow(() -> shipmentService.deleteShipment(shipmentId));

        verify(shipmentRepository, times(1)).findById(shipmentId);
        verify(shipmentRepository, times(1)).delete(shipment);
    }

    @Test
    void deleteShipment_ShouldThrowShipmentNotFoundException() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.empty());

        assertThrows(ShipmentNotFoundException.class, () -> shipmentService.deleteShipment(shipmentId));

        verify(shipmentRepository, times(1)).findById(shipmentId);
        verify(shipmentRepository, never()).delete(any(Shipment.class));
    }
}
