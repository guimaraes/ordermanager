package br.com.ambevtech.ordermanager.model;

import br.com.ambevtech.ordermanager.model.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private LocalDateTime shippedDate;

    @Column(nullable = false)
    private String trackingNumber; // Código de rastreamento

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status; // Status do envio
}
