package com.gobag.gobag_service.service;

import com.gobag.gobag_service.model.CustomerOrder;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public String processOrder(CustomerOrder order) {
        if (sendOrderToRestaurant(order)) {
            return "✅ Order confirmed! Please proceed with payment.";
        } else {
            return "❌ Order rejected. Please select another restaurant.";
        }
    }

    private boolean sendOrderToRestaurant(CustomerOrder order) {
        // Simulate restaurant confirmation
        return Math.random() > 0.2;  // 80% chance of acceptance
    }
}
