package ru.sunlab.shopbasket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sunlab.shopbasket.model.ProductCount;

import java.util.List;

@Repository
public interface ProductCountRepository extends JpaRepository<ProductCount, Long> {

    List<ProductCount> findAllByProductId(Long productId);

    List<ProductCount> findAllByProductIdInAndStoreId(List<Long> productId, Long storeId);

    List<ProductCount> findAllByProductIdIn(List<Long> productId);

    ProductCount findByProductIdAndStoreId(Long productId, Long storeId);

    @Query("select sum(pc.quantity) " +
            "from ProductCount pc " +
            "where pc.productId = ?1" )
    Integer getProductCountInAllStores(Long productId);

    @Query("select pc.storeId " +
            "from ProductCount pc " +
            "where pc.productId = ?1 and pc.quantity > 0 " +
            "order by pc.quantity desc")
    List<Long> getAllStoreIdWithPositiveQuantity(Long productId);

    @Query(value = "select bu.product_id " +
            "from basket_unit bu " +
            "join product_count pc on bu.product_id=pc.product_id " +
            "where bu.client_id = ?1 and pc.store_id = ?2 " +
            "  and bu.quantity_buy > pc.quantity_stock", nativeQuery = true)
    List<Long> getProductIdsOutStore(Long clientId, Long storeId);

    @Query(value = "select bu.product_id " +
            "from basket_unit bu " +
            "join product_count pc on bu.product_id=pc.product_id " +
            "where bu.client_id = ?1 " +
            "group by bu.product_id, product_name, quantity_buy " +
            "having sum(quantity_stock) < quantity_buy", nativeQuery = true)
    List<Long> getProductIdsOutAllStore(Long clientId);


}
