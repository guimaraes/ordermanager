package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.dto.PaymentRequestDTO;
import br.com.ambevtech.ordermanager.dto.PaymentResponseDTO;
import br.com.ambevtech.ordermanager.exception.OrderNotFoundException;
import br.com.ambevtech.ordermanager.exception.PaymentNotFoundException;
import br.com.ambevtech.ordermanager.mapper.PaymentMapper;
import br.com.ambevtech.ordermanager.mapper.ShipmentMapper;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.Payment;
import br.com.ambevtech.ordermanager.model.Shipment;
import br.com.ambevtech.ordermanager.model.enums.PaymentStatus;
import br.com.ambevtech.ordermanager.repository.OrderRepository;
import br.com.ambevtech.ordermanager.repository.PaymentRepository;
import br.com.ambevtech.ordermanager.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private UUID paymentId;
    private UUID orderId;
    private Payment payment;
    private Order order;
    private PaymentRequestDTO paymentRequest;
    private PaymentResponseDTO paymentResponse;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        order = new Order();
        order.setId(orderId);

        payment = new Payment();
        payment.setId(paymentId);
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmountPaid(BigDecimal.valueOf(100.00));
        payment.setPaymentMethod("Credit Card");

        // Ajustando para corresponder à assinatura correta do construtor
        paymentRequest = new PaymentRequestDTO(
                orderId,
                "Credit Card",
                BigDecimal.valueOf(100.00), 
                PaymentStatus.PENDING
        );

        paymentResponse = new PaymentResponseDTO(
                paymentId,
                orderId,
                "Credit Card",
                BigDecimal.valueOf(100.00), 
                PaymentStatus.PENDING
        );
    }


    @Test
    void getPaymentById_ShouldReturnPayment() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        try (var mockStatic = Mockito.mockStatic(PaymentMapper.class)) {
            mockStatic.when(() -> PaymentMapper.toResponseDTO(payment)).thenReturn(paymentResponse);

            PaymentResponseDTO result = paymentService.getPaymentById(paymentId);

            assertNotNull(result);
            assertEquals(paymentResponse, result);
        }
    }

    @Test
    void getPaymentById_ShouldThrowException_WhenNotFound() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(paymentId));
    }

    @Test
    void createPayment_ShouldSaveAndReturnPayment() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try (var mockStatic = Mockito.mockStatic(PaymentMapper.class)) {
            mockStatic.when(() -> PaymentMapper.toEntity(paymentRequest, order)).thenReturn(payment);
            mockStatic.when(() -> PaymentMapper.toResponseDTO(payment)).thenReturn(paymentResponse);

            when(paymentRepository.save(payment)).thenReturn(payment);

            PaymentResponseDTO result = paymentService.createPayment(paymentRequest);

            assertNotNull(result);
            assertEquals(paymentResponse, result);
        }
    }

    @Test
    void createPayment_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> paymentService.createPayment(paymentRequest));
    }

    @Test
    void updatePaymentStatus_ShouldUpdateAndReturnPayment() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        // Criando um Shipment válido para evitar NullPointerException
        Shipment shipment = new Shipment();
        shipment.setId(UUID.randomUUID());
        shipment.setTrackingNumber("TRK-123456");

        // Mockando o comportamento de shipmentRepository.save()
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        try (var mockStatic = Mockito.mockStatic(PaymentMapper.class)) {
            mockStatic.when(() -> PaymentMapper.toResponseDTO(payment)).thenReturn(paymentResponse);

            PaymentResponseDTO result = paymentService.updatePaymentStatus(
                    paymentId,
                    PaymentStatus.APPROVED,
                    BigDecimal.valueOf(100.00),
                    "Pix"
            );

            assertNotNull(result);
            assertEquals(paymentResponse, result);
        }
    }

}
