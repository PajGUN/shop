package ru.sunlab.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sunlab.shop.exception.ProductNotFoundException;
import ru.sunlab.shop.exception.ProductTypeException;
import ru.sunlab.shop.model.Product;
import ru.sunlab.shop.model.ProductType;
import ru.sunlab.shop.repository.ProductRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product add(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product getById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        return productOptional.orElseThrow(()-> new ProductNotFoundException("Product id - "+
                productId +" not found!"));
    }

    @Transactional
    public Product update(Long productId, Product product) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()){
            product.setId(productId);
            return productRepository.save(product);
        } else {
            throw new ProductNotFoundException("Product id - "+ productId +" not found!");
        }
    }

    @Transactional
    public Product delete(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()){
            productRepository.deleteById(productId);
            return productOptional.get();
        } else {
            throw new ProductNotFoundException("Product id - "+ productId +" not found!");
        }
    }

    @Transactional
    public List<Product> getByType(String type, Pageable pageable) {
        ProductType productType;
        try {
            productType = ProductType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ProductTypeException("Value - "+ type +" is not ProductTypeEnum");
        }
        Page<Product> allByType = productRepository.getAllByType(productType, pageable);
        return allByType.getContent();
    }

    public List<Product> getByProductByName(String productName, Pageable pageable) {
        Page<Product> products = productRepository.getAllByNameContains(productName, pageable);
        return products.getContent();
    }
}
