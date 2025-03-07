package com.floristeria.blomst.controller.order;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static com.floristeria.blomst.utils.RequestUtils.getResponse;

import com.floristeria.blomst.domain.Response;
import com.floristeria.blomst.dto.order.Order;
import com.floristeria.blomst.service.EmailService;
import com.floristeria.blomst.service.order.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = { "/order" })
public class OrderController {
    private final OrderService orderService;
    private final EmailService emailService;

    @PostMapping("/create")
    public ResponseEntity<Response> createOrder(HttpServletRequest request) {
        return ResponseEntity.created(URI.create("")).body(
                getResponse(request, Map.of("Order", orderService.createOrder()), "Pedido creado con exito",
                        HttpStatus.CREATED));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Response> getOrderById(@PathVariable("id") Long id, HttpServletRequest request) {
        return ResponseEntity.ok().body(
                getResponse(request, Map.of("Order", orderService.getOrderById(id)), "Pedido encontrado con exito",
                        HttpStatus.OK));
    }

    @GetMapping("/list")
    public ResponseEntity<Response> getOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        Order order = orderService.getOrderById(1L);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("orderNumber", "999");
        placeholders.put("total", "50.00"); // Reemplazar con el valor real
        placeholders.put("or.getProduct_description()", "Descripción del producto");
        placeholders.put("or.getImageUrl()", "https://ruta-imagen.jpg");
        placeholders.put("accept", "https://tu-api.com/accept/" + order.getId());
        placeholders.put("denied", "https://tu-api.com/denied/" + order.getId());
        placeholders.put("or.getSendTo()", "Cliente");
        placeholders.put("or.getDedicatory()", "Mensaje especial");
        placeholders.put("or.getDelivery_card()", "Tarjeta de condolencias");
        placeholders.put("or.getCustomer_note()", "Nota del cliente");
        placeholders.put("or.getDirection()", "Calle 123");
        placeholders.put("or.getLocality()", "Ciudad");
        placeholders.put("or.getState()", "Provincia");
        placeholders.put("or.getPostcode()", "12345");

        emailService.sendOrderPropostal("enyel.venecia@gmail.com", placeholders);

        return ResponseEntity.ok().body(
                getResponse(request, Map.of("Orders", orderService.getOrders(page, size)),
                        "Pedidos encontrados con exito", HttpStatus.OK));
    }
}
