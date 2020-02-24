package ru.sunlab.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sunlab.shop.model.ProductCount;

import java.util.List;

@Repository
public interface ProductCountRepository extends JpaRepository<ProductCount, Long> {

    List<ProductCount> findAllByProductId(Long productId);

    ProductCount findByProductIdAndStoreId(Long productId, Long storeId);
}
