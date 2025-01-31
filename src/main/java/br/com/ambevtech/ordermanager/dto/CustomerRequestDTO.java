package br.com.ambevtech.ordermanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerRequestDTO(
        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @Email(message = "E-mail inválido.")
        String email,

        @Pattern(regexp = "\\d{10,11}", message = "Número de telefone inválido.")
        String phoneNumber
) {
}
