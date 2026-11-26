package com.example.tin_nghia.presentation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateOrderRequest {
    private final int variantId;
    private final int quantity;
    @JsonCreator
    public CreateOrderRequest(
            // @JsonProperty() cho Jackson biết tên trường cần lấy ra trong Json
            @JsonProperty("variantId") int variantId,
            @JsonProperty("quantity") int quantity){
        this.variantId = variantId;
        this.quantity = quantity;
    }
    public int getVariantId() {
        return variantId;
    }
    public int getQuantity() {
        return quantity;
    }
}
