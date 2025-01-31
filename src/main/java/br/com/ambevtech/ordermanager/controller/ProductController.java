package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.ProductRequestDTO;
import br.com.ambevtech.ordermanager.dto.ProductResponseDTO;
import br.com.ambevtech.ordermanager.mapper.ProductMapper;
import br.com.ambevtech.ordermanager.service.ProductService;
import br.com.ambevtech.ordermanager.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(Pageable pageable) {
        log.info("Recebendo solicitação para listar todos os produtos - Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductResponseDTO> products = productService.getAllProducts(pageable)
                .map(ProductMapper::toResponseDTO);

        log.info("Total de produtos encontrados: {}", products.getTotalElements());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        log.info("Recebendo solicitação para buscar produto com ID: {}", id);

        ProductResponseDTO productResponse = ProductMapper.toResponseDTO(productService.getProductById(id));

        log.info("Produto encontrado: {}", productResponse);
        return ResponseEntity.ok(productResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        log.info("Recebendo solicitação para atualizar produto com ID: {}", id);

        var supplier = supplierService.getSupplierById(dto.supplierId());
        var updatedProduct = ProductMapper.toEntity(dto, supplier);

        var savedProduct = productService.updateProduct(id, updatedProduct);
        var responseDTO = ProductMapper.toResponseDTO(savedProduct);

        log.info("Produto atualizado com sucesso! ID: {}", responseDTO.id());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        log.info("Recebendo solicitação para registrar um novo produto.");

        var supplier = supplierService.getSupplierById(dto.supplierId());
        var product = ProductMapper.toEntity(dto, supplier);

        var savedProduct = productService.createProduct(product);
        var responseDTO = ProductMapper.toResponseDTO(savedProduct);

        log.info("Produto criado com sucesso! ID: {}", responseDTO.id());
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Recebendo solicitação para remover produto com ID: {}", id);

        productService.deleteProduct(id);

        log.info("Produto removido com sucesso! ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
