package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.dto.OrderSummary;
import com.example.tin_nghia.application.port.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetOrderService {
    private final OrderRepository orderRepository;
    public GetOrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }
    public List<OrderSummary> execute(int userId){
        return orderRepository.findOrderByUserId(userId);
    }
}
