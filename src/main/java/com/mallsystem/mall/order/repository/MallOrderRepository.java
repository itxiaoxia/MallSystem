package com.mallsystem.mall.order.repository;

import com.mallsystem.mall.order.entity.MallOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MallOrderRepository extends JpaRepository<MallOrder, Long> {

    Optional<MallOrder> findByOrderNo(String orderNo);

    boolean existsByOrderNo(String orderNo);
}
