package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.PaymentRequestDTO;
import br.com.ambevtech.ordermanager.dto.PaymentResponseDTO;
import br.com.ambevtech.ordermanager.model.enums.PaymentStatus;
import br.com.ambevtech.ordermanager.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentResponseDTO paymentResponse;
    private PaymentRequestDTO paymentRequest;
    private UUID paymentId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        // Ajustar a ordem e número de parâmetros conforme esperado no construtor do DTO
        paymentResponse = new PaymentResponseDTO(
                paymentId,
                orderId,
                "Credit Card",  // paymentMethod (agora antes do amountPaid)
                BigDecimal.valueOf(100.00), // amountPaid
                PaymentStatus.PENDING // status
        );

        paymentRequest = new PaymentRequestDTO(
                orderId,
                "Credit Card",  // paymentMethod (agora antes do amountPaid)
                BigDecimal.valueOf(100.00), // amountPaid
                PaymentStatus.PENDING // status (adicionado)
        );
    }


    @Test
    void getPaymentById_ShouldReturnPayment() {
        when(paymentService.getPaymentById(paymentId)).thenReturn(paymentResponse);

        ResponseEntity<PaymentResponseDTO> response = paymentController.getPaymentById(paymentId);

        assertNotNull(response.getBody());
        assertEquals(paymentResponse, response.getBody());
        verify(paymentService, times(1)).getPaymentById(paymentId);
    }

    @Test
    void getPaymentByOrderId_ShouldReturnPayment() {
        when(paymentService.getPaymentByOrderId(orderId)).thenReturn(paymentResponse);

        ResponseEntity<PaymentResponseDTO> response = paymentController.getPaymentByOrderId(orderId);

        assertNotNull(response.getBody());
        assertEquals(paymentResponse, response.getBody());
        verify(paymentService, times(1)).getPaymentByOrderId(orderId);
    }

    @Test
    void updatePaymentStatus_ShouldUpdateAndReturnPayment() {
        BigDecimal amountPaid = BigDecimal.valueOf(100.00);
        String paymentMethod = "Pix";

        when(paymentService.updatePaymentStatus(paymentId, PaymentStatus.APPROVED, amountPaid, paymentMethod))
                .thenReturn(paymentResponse);

        ResponseEntity<PaymentResponseDTO> response = paymentController.updatePaymentStatus(paymentId, PaymentStatus.APPROVED, amountPaid, paymentMethod);

        assertNotNull(response.getBody());
        assertEquals(paymentResponse, response.getBody());
        verify(paymentService, times(1)).updatePaymentStatus(paymentId, PaymentStatus.APPROVED, amountPaid, paymentMethod);
    }

    @Test
    void createPayment_ShouldCreateAndReturnPayment() {
        when(paymentService.createPayment(paymentRequest)).thenReturn(paymentResponse);

        ResponseEntity<PaymentResponseDTO> response = paymentController.createPayment(paymentRequest);

        assertNotNull(response.getBody());
        assertEquals(paymentResponse, response.getBody());
        verify(paymentService, times(1)).createPayment(paymentRequest);
    }
}
