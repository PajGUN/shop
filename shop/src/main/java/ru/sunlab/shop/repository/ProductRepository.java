package ru.sunlab.shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sunlab.shop.model.Product;
import ru.sunlab.shop.model.ProductType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> getAllByType(ProductType type, Pageable pageable);

    @Query("select p from Product p where lower(p.name) like lower(concat('%',?1,'%'))")
    Page<Product> getAllByNameContains(String name, Pageable pageable);

}
