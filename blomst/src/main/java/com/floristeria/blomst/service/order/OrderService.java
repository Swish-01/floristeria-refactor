package com.floristeria.blomst.service.order;

import org.springframework.data.domain.Page;

import com.floristeria.blomst.dto.order.Order;

public interface OrderService {
    Order createOrder();
    Order getOrderById(Long id);
    Page<Order> getOrders(int page,int size);
}
