package br.com.ambevtech.ordermanager.dto;

import java.util.UUID;

public record CustomerResponseDTO(
        UUID id,
        String name,
        String email,
        String phoneNumber
) {
}
