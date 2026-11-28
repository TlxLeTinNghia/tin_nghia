package com.example.tin_nghia.application.port;

import com.example.tin_nghia.application.dto.OrderItem;
import com.example.tin_nghia.application.dto.OrderSummary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    int CreateOrder(int userId , int variantId , int quantity);
    List<OrderSummary> findOrderByUserId(int userId);
    Optional<OrderSummary> findOrderByOrderId(int orderId , int userId);
    List<OrderItem> findOrderItemById(int orderId , int userId);
}
