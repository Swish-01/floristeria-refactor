package com.floristeria.blomst.service.order;

import static org.springframework.data.domain.PageRequest.of;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.floristeria.blomst.dto.order.Order;
import com.floristeria.blomst.entity.order.OrderEntity;
import com.floristeria.blomst.entity.web.ProductEntity;
import com.floristeria.blomst.entity.web.WebEntity;
import com.floristeria.blomst.exception.ApiException;
import com.floristeria.blomst.repository.order.OrderRepository;
import com.floristeria.blomst.repository.product.ProductRepository;
import com.floristeria.blomst.repository.web.WebRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WebRepository webRepository;
    private final ProductRepository productRepository;
    @Override
    public Order createOrder() {
        WebEntity web = webRepository.findById(1L)
                .orElseThrow(() -> new ApiException("No existe la web con ID 1"));

        ProductEntity product = productRepository.findById(38L)
                .orElseThrow(() -> new ApiException("No existe el producto con ID 38"));

        OrderEntity orderEntity = OrderEntity.builder()
                .number(UUID.randomUUID().toString()) // Genera un número de pedido único
                .status("PENDING")
                .web(web)
                .products(Set.of(product)) // Relacionamos el pedido con el producto
                .build();

        orderEntity = orderRepository.save(orderEntity);

        return getOrderById(orderEntity.getId()); // Devuelve el DTO después de persistir
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findOrderById(id)
                .orElseThrow(() -> new ApiException("No existe un pedido con este ID: " + id));
    }

    @Override
    public Page<Order> getOrders(int page, int size) {
        return orderRepository.findOrders(of(page, size));
    }

}
