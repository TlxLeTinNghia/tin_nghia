package com.example.tin_nghia.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDetail {
    private final int orderId;
    private final LocalDateTime orderDate;
    private final BigDecimal totalAmount;
    private final List<OrderItem> items;
    public OrderDetail(int orderId , LocalDateTime orderDate , BigDecimal totalAmount , List<OrderItem> items){
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public int getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
