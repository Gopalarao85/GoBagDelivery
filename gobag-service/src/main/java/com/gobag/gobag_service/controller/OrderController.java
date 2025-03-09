package com.gobag.gobag_service.controller;

import com.gobag.gobag_service.model.CustomerOrder;
import com.gobag.gobag_service.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public String placeOrder(@RequestBody CustomerOrder order) {
        return orderService.processOrder(order);
    }
}
