package ru.sunlab.shopbasket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sunlab.shopbasket.model.BasketUnit;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BasketRepository extends JpaRepository<BasketUnit, Long> {

    BasketUnit getByClientIdAndProductId(Long clientId, Long productId);

    List<BasketUnit> findAllByClientId(Long clientId);

    void deleteAllByClientId(Long clientId);
}
