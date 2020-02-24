package ru.sunlab.shopbasket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sunlab.shopbasket.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select sum(oi.price * oi.quantity) " +
           "from OrderItem oi " +
           "where oi.orderId = ?1")
    BigDecimal getSummaryCost(Long orderId);

    List<OrderItem> findAllByOrderId(Long orderId);
}
