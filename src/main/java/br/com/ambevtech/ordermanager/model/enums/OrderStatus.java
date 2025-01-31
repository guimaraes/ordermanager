package br.com.ambevtech.ordermanager.model.enums;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.Locale;

public enum OrderStatus {
    PENDING("Pendente"),
    APPROVED("Aprovado"),
    SHIPPED("Enviado"),
    DELIVERED("Entregue"),
    CANCELLED("Cancelado");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static OrderStatus getByName(String name) throws DataIntegrityViolationException {
        String normalized = name.toUpperCase(Locale.ROOT);
        if (normalized.startsWith("P")) {
            return OrderStatus.PENDING;
        } else if (normalized.startsWith("A")) {
            return OrderStatus.APPROVED;
        } else if (normalized.startsWith("S")) {
            return OrderStatus.SHIPPED;
        } else if (normalized.startsWith("D")) {
            return OrderStatus.DELIVERED;
        } else if (normalized.startsWith("C")) {
            return OrderStatus.CANCELLED;
        } else {
            throw new DataIntegrityViolationException("Status do pedido inválido. Use 'P', 'A', 'S', 'D' ou 'C'.");
        }
    }
}
