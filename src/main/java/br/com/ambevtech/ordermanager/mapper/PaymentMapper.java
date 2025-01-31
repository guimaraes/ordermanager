package br.com.ambevtech.ordermanager.mapper;

import br.com.ambevtech.ordermanager.dto.PaymentRequestDTO;
import br.com.ambevtech.ordermanager.dto.PaymentResponseDTO;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.Payment;

import java.time.LocalDateTime;

public class PaymentMapper {

    public static PaymentResponseDTO toResponseDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPaymentMethod(),
                payment.getAmountPaid(),
                payment.getStatus()
        );
    }

    public static Payment toEntity(PaymentRequestDTO dto, Order order) {
        return new Payment(
                null,
                order,
                LocalDateTime.now(),
                dto.status(),
                dto.amountPaid(),
                dto.paymentMethod()
        );
    }
}
