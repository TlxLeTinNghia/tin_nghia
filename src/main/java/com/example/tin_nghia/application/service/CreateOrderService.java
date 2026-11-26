package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.exception.InsufficientStockException;
import com.example.tin_nghia.application.exception.ProductNotFoundException;
import com.example.tin_nghia.application.port.OrderRepository;
import com.example.tin_nghia.application.port.ProductVariantRepository;
import com.example.tin_nghia.domain.ProductVariant;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderService {
    private final OrderRepository orderRepository;
    public CreateOrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }
    public int execute(int userId , int variantId , int quantity){
        return orderRepository.CreateOrder(userId , variantId , quantity);
    }
}
