package com.mallsystem.mall.order.repository;

import com.mallsystem.mall.order.entity.MallOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MallOrderRepository extends JpaRepository<MallOrder, Long> {

    Optional<MallOrder> findByOrderNo(String orderNo);

    @Query("select o from MallOrder o where o.orderNo = :orderNo")
    Optional<MallOrder> findByOrderNoForUpdate(@Param("orderNo") String orderNo);

    boolean existsByOrderNo(String orderNo);
}
