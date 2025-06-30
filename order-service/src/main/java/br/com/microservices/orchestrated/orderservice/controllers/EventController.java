package br.com.microservices.orchestrated.orderservice.controllers;

import br.com.microservices.orchestrated.orderservice.documents.Order;
import br.com.microservices.orchestrated.orderservice.dtos.OrderRequest;
import br.com.microservices.orchestrated.orderservice.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }
}
