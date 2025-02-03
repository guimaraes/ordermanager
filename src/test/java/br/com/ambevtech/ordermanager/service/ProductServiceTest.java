package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.exception.ProductNotFoundException;
import br.com.ambevtech.ordermanager.model.Product;
import br.com.ambevtech.ordermanager.model.Supplier;
import br.com.ambevtech.ordermanager.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Fornecedor Teste");

        product = new Product();
        product.setId(1L);
        product.setName("Produto Teste");
        product.setDescription("Descrição Teste");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setSupplier(supplier);
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Product result = productService.getProductById(product.getId());

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(product.getId()));
    }

    @Test
    void createProduct_ShouldSaveAndReturnProduct() {
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals("Produto Teste", result.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setName("Produto Atualizado");
        updatedProduct.setDescription("Nova Descrição");
        updatedProduct.setPrice(BigDecimal.valueOf(150.00));
        updatedProduct.setSupplier(product.getSupplier());

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(updatedProduct);

        Product result = productService.updateProduct(product.getId(), updatedProduct);

        assertNotNull(result);
        assertEquals("Produto Atualizado", result.getName());
        assertEquals("Nova Descrição", result.getDescription());
        assertEquals(BigDecimal.valueOf(150.00), result.getPrice());

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(product.getId(), product));

        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId());

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(product.getId()));

        verify(productRepository, never()).delete(any());
    }
}
