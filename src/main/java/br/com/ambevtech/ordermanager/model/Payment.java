package br.com.ambevtech.ordermanager.model;

import br.com.ambevtech.ordermanager.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // Status do pagamento

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid; // Valor pago

    @Column(nullable = false, length = 20)
    private String paymentMethod; // Método de pagamento (PIX, Cartão, Boleto, etc.)
}
