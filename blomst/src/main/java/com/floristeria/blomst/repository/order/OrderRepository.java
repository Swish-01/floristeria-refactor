package com.floristeria.blomst.repository.order;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.floristeria.blomst.dto.order.Order;
import com.floristeria.blomst.entity.order.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query("SELECT new com.floristeria.blomst.dto.order.Order(o.id, o.createdBy, o.updatedBy, o.number, o.status) FROM OrderEntity o WHERE o.id = :id")
    Optional<Order> findOrderById(Long id);



    @Query("SELECT new com.floristeria.blomst.dto.order.Order(o.id, o.createdBy, o.updatedBy, o.number, o.status) FROM OrderEntity o")
    Page<Order> findOrders(Pageable pageable);
}
