package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.dto.OrderDetail;
import com.example.tin_nghia.application.dto.OrderItem;
import com.example.tin_nghia.application.dto.OrderSummary;
import com.example.tin_nghia.application.exception.OrderNotFoundException;
import com.example.tin_nghia.application.port.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetOrderDetailService {
    private final OrderRepository orderRepository;
    public GetOrderDetailService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }
    public OrderDetail execute(int orderId , int userId){
        OrderSummary orderSummary = orderRepository.findOrderByOrderId(orderId , userId).orElseThrow(()->new OrderNotFoundException(orderId));
        List<OrderItem> orderItems = orderRepository.findOrderItemById(orderId , userId);
        return new OrderDetail(
                orderId,
                orderSummary.getOrderDate(),
                orderSummary.getTotalAmount(),
                orderItems
        );
    }
}
