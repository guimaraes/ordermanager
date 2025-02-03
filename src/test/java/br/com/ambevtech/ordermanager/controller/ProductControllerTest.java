package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.ProductRequestDTO;
import br.com.ambevtech.ordermanager.dto.ProductResponseDTO;
import br.com.ambevtech.ordermanager.mapper.ProductMapper;
import br.com.ambevtech.ordermanager.model.Product;
import br.com.ambevtech.ordermanager.model.Supplier;
import br.com.ambevtech.ordermanager.service.ProductService;
import br.com.ambevtech.ordermanager.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private ProductController productController;

    private Product product;
    private Supplier supplier;
    private ProductRequestDTO requestDTO;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Fornecedor Teste");

        product = new Product();
        product.setId(1L);
        product.setName("Produto Teste");
        product.setDescription("Descrição Teste");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setSupplier(supplier);

        requestDTO = new ProductRequestDTO("Produto Teste", "Descrição Teste", BigDecimal.valueOf(100.00), 1L);
        responseDTO = new ProductResponseDTO(1L, "Produto Teste", "Descrição Teste", BigDecimal.valueOf(100.00), 1L);
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productService.getAllProducts(pageable)).thenReturn(productPage);

        try (var mockStatic = Mockito.mockStatic(ProductMapper.class)) {
            mockStatic.when(() -> ProductMapper.toResponseDTO(product)).thenReturn(responseDTO);

            ResponseEntity<Page<ProductResponseDTO>> response = productController.getAllProducts(pageable);

            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().getTotalElements());
            verify(productService, times(1)).getAllProducts(pageable);
        }
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        when(productService.getProductById(product.getId())).thenReturn(product);

        try (var mockStatic = Mockito.mockStatic(ProductMapper.class)) {
            mockStatic.when(() -> ProductMapper.toResponseDTO(product)).thenReturn(responseDTO);

            ResponseEntity<ProductResponseDTO> response = productController.getProductById(product.getId());

            assertNotNull(response.getBody());
            assertEquals(responseDTO, response.getBody());
        }
    }

    @Test
    void createProduct_ShouldCreateAndReturnProduct() {
        when(supplierService.getSupplierById(requestDTO.supplierId())).thenReturn(supplier);

        try (var mockStatic = Mockito.mockStatic(ProductMapper.class)) {
            mockStatic.when(() -> ProductMapper.toEntity(requestDTO, supplier)).thenReturn(product);
            mockStatic.when(() -> ProductMapper.toResponseDTO(product)).thenReturn(responseDTO);

            when(productService.createProduct(product)).thenReturn(product);

            ResponseEntity<ProductResponseDTO> response = productController.createProduct(requestDTO);

            assertNotNull(response.getBody());
            assertEquals(responseDTO, response.getBody());
        }
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        doNothing().when(productService).deleteProduct(product.getId());

        ResponseEntity<Void> response = productController.deleteProduct(product.getId());

        assertEquals(204, response.getStatusCode().value());
        verify(productService, times(1)).deleteProduct(product.getId());
    }
}
