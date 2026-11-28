package com.example.tin_nghia.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSummary {
    private final int orderId;
    private final LocalDateTime orderDate;
    private final BigDecimal totalAmount;

    public OrderSummary(int orderId , LocalDateTime orderDate , BigDecimal totalAmount){
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
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
}
