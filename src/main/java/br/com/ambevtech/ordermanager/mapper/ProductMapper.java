package br.com.ambevtech.ordermanager.mapper;

import br.com.ambevtech.ordermanager.dto.ProductRequestDTO;
import br.com.ambevtech.ordermanager.dto.ProductResponseDTO;
import br.com.ambevtech.ordermanager.model.Product;
import br.com.ambevtech.ordermanager.model.Supplier;

public class ProductMapper {

    public static ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSupplier().getId()
        );
    }

    public static Product toEntity(ProductRequestDTO dto, Supplier supplier) {
        return new Product(
                null,
                dto.name(),
                dto.description(),
                dto.price(),
                supplier
        );
    }
}
