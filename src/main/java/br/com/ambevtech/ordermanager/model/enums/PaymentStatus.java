package br.com.ambevtech.ordermanager.model.enums;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.Locale;

public enum PaymentStatus {
    PENDING("Pagamento Pendente"),
    APPROVED("Pagamento Aprovado"),
    DECLINED("Pagamento Recusado"),
    REFUNDED("Reembolsado");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static PaymentStatus getByName(String name) throws DataIntegrityViolationException {
        String normalized = name.toUpperCase(Locale.ROOT);
        if (normalized.startsWith("P")) {
            return PaymentStatus.PENDING;
        } else if (normalized.startsWith("A")) {
            return PaymentStatus.APPROVED;
        } else if (normalized.startsWith("D")) {
            return PaymentStatus.DECLINED;
        } else if (normalized.startsWith("R")) {
            return PaymentStatus.REFUNDED;
        } else {
            throw new DataIntegrityViolationException("Status de pagamento inválido. Use 'P', 'A', 'D' ou 'R'.");
        }
    }
}
