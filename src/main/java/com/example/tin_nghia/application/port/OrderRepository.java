package com.example.tin_nghia.application.port;

import org.springframework.stereotype.Repository;

public interface OrderRepository {
    int CreateOrder(int userId , int variantId , int quantity);
}
