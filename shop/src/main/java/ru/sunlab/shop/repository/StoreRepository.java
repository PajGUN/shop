package ru.sunlab.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sunlab.shop.model.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
