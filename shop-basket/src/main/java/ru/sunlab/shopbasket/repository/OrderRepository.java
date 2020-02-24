package ru.sunlab.shopbasket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sunlab.shopbasket.model.Order;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.created < ?1 AND o.archive = false AND o.payment = false")
    List<Order> getAllByTimeBefore(LocalDateTime localDateTime);

    @Query("select o from Order o where o.clientId = ?1 and o.archive = false")
    List<Order> getActiveOrders(Long clientId);

    @Query("select o from Order o where o.clientId = ?1 and o.archive = true")
    Page<Order> getArchiveOrders(Long clientId, Pageable pageable);

}
