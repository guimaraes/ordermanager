package br.com.ambevtech.ordermanager.mapper;

import br.com.ambevtech.ordermanager.dto.SupplierRequestDTO;
import br.com.ambevtech.ordermanager.dto.SupplierResponseDTO;
import br.com.ambevtech.ordermanager.model.Supplier;

public class SupplierMapper {

    public static SupplierResponseDTO toResponseDTO(Supplier supplier) {
        return new SupplierResponseDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhoneNumber()
        );
    }

    public static Supplier toEntity(SupplierRequestDTO dto) {
        return new Supplier(
                null,
                dto.name(),
                dto.email(),
                dto.phoneNumber(),
                null
        );
    }
}
