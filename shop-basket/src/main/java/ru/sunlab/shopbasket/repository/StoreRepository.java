package ru.sunlab.shopbasket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sunlab.shopbasket.model.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("select s.name from Store s where s.id = ?1")
    String getStoreName(Long storeId);
}
