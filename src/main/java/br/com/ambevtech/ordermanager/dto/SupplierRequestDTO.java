package br.com.ambevtech.ordermanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SupplierRequestDTO(
        @NotBlank(message = "O nome do fornecedor é obrigatório.")
        @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres.")
        String name,

        @Email(message = "E-mail inválido.")
        @NotBlank(message = "O e-mail do fornecedor é obrigatório.")
        String email,

        @Pattern(regexp = "\\d{10,11}", message = "Número de telefone inválido.")
        @NotBlank(message = "O telefone do fornecedor é obrigatório.")
        String phoneNumber
) {
}
