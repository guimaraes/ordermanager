package br.com.ambevtech.ordermanager.dto;

public record SupplierResponseDTO(
        Long id,
        String name,
        String email,
        String phoneNumber
) {
}
