package com.mallsystem.mall.product.repository;

import com.mallsystem.mall.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            select p from Product p
            where p.status = com.mallsystem.mall.product.entity.ProductStatus.ACTIVE
              and (:keyword is null or :keyword = ''
                   or lower(p.productCode) like lower(concat('%', :keyword, '%'))
                   or lower(p.name) like lower(concat('%', :keyword, '%')))
            """)
    Page<Product> searchActive(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            select p from Product p
            where p.productCode = :productCode
              and p.status = com.mallsystem.mall.product.entity.ProductStatus.ACTIVE
            """)
    Optional<Product> findActiveByProductCode(@Param("productCode") String productCode);

    Optional<Product> findByProductCode(String productCode);

    // 条件更新让并发下单不会把库存扣成负数。
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Product p
               set p.stock = p.stock - :quantity,
                   p.version = p.version + 1,
                   p.updatedAt = CURRENT_TIMESTAMP
             where p.productCode = :productCode
               and p.status = com.mallsystem.mall.product.entity.ProductStatus.ACTIVE
               and p.stock >= :quantity
            """)
    int decrementStockIfEnough(@Param("productCode") String productCode, @Param("quantity") int quantity);

    // 退款时原子恢复库存，同时推进版本号，避免覆盖并发更新。
    @Modifying(flushAutomatically = true)
    @Query("""
            update Product p
               set p.stock = p.stock + :quantity,
                   p.version = p.version + 1,
                   p.updatedAt = CURRENT_TIMESTAMP
             where p.productCode = :productCode
            """)
    int incrementStock(@Param("productCode") String productCode, @Param("quantity") int quantity);
}
