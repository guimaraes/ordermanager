package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.exception.ProductNotFoundException;
import br.com.ambevtech.ordermanager.model.Product;
import br.com.ambevtech.ordermanager.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getAllProducts(Pageable pageable) {
        log.info("Buscando todos os produtos - Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        log.info("Buscando produto com ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Produto com ID {} não encontrado!", id);
                    return new ProductNotFoundException("Produto não encontrado: " + id);
                });
    }

    @Transactional
    public Product createProduct(Product product) {
        log.info("Criando novo produto: {}", product.getName());
        Product savedProduct = productRepository.save(product);
        log.info("Produto criado com sucesso! ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        log.info("Atualizando produto com ID: {}", id);
        Product existingProduct = getProductById(id);
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setSupplier(updatedProduct.getSupplier());

        Product savedProduct = productRepository.save(existingProduct);
        log.info("Produto atualizado com sucesso! ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Removendo produto com ID: {}", id);
        Product product = getProductById(id);
        productRepository.delete(product);
        log.info("Produto removido com sucesso! ID: {}", id);
    }
}
