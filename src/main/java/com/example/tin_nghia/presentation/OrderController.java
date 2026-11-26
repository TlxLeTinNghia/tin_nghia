package com.example.tin_nghia.presentation;

import com.example.tin_nghia.application.service.CreateOrderService;
import com.example.tin_nghia.presentation.dto.CreateOrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final CreateOrderService createOrderService;
    public OrderController(CreateOrderService createOrderService){
        this.createOrderService = createOrderService;
    }
    @PostMapping
    public ResponseEntity<Map<String , Integer>> createOrder(@RequestBody CreateOrderRequest request){
        if(request.getVariantId()<=0){
            throw new IllegalArgumentException("Mã biến thể phải lớn hơn 0!");
        }
        if(request.getQuantity()<1){
            throw new IllegalArgumentException("Số lượng nhỏ nhất là 1!");
        }
        int userId = 1;
        int orderId = createOrderService.execute(userId , request.getVariantId() , request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("orderId",orderId));
    }
}
