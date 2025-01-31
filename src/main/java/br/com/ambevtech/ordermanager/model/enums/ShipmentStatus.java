package br.com.ambevtech.ordermanager.model.enums;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.Locale;

public enum ShipmentStatus {
    PENDING("Aguardando envio"),
    IN_TRANSIT("Em trânsito"),
    DELIVERED("Entregue"),
    FAILED("Falha na entrega");

    private final String status;

    ShipmentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static ShipmentStatus getByName(String name) throws DataIntegrityViolationException {
        String normalized = name.toUpperCase(Locale.ROOT);
        if (normalized.startsWith("P")) {
            return ShipmentStatus.PENDING;
        } else if (normalized.startsWith("I")) {
            return ShipmentStatus.IN_TRANSIT;
        } else if (normalized.startsWith("D")) {
            return ShipmentStatus.DELIVERED;
        } else if (normalized.startsWith("F")) {
            return ShipmentStatus.FAILED;
        } else {
            throw new DataIntegrityViolationException("Status de envio inválido. Use 'P', 'I', 'D' ou 'F'.");
        }
    }
}
