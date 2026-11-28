package com.example.tin_nghia.presentation;

import com.example.tin_nghia.application.dto.OrderDetail;
import com.example.tin_nghia.application.dto.OrderSummary;
import com.example.tin_nghia.application.service.CreateOrderService;
import com.example.tin_nghia.application.service.GetOrderDetailService;
import com.example.tin_nghia.application.service.GetOrderService;
import com.example.tin_nghia.presentation.dto.CreateOrderRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.web.server.servlet.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final CreateOrderService createOrderService;
    private final GetOrderService getOrderService;
    private final GetOrderDetailService getOrderDetailService;
    public OrderController(
            CreateOrderService createOrderService,
            GetOrderService getOrderService,
            GetOrderDetailService getOrderDetailService
    ){
        this.createOrderService = createOrderService;
        this.getOrderService = getOrderService;
        this.getOrderDetailService = getOrderDetailService;
    }
    @PostMapping
    public ResponseEntity<Map<String , Object>> createOrder(
            @RequestBody CreateOrderRequest request,
            HttpServletRequest httpServletRequest
            ){
        HttpSession session = httpServletRequest.getSession(false);
        Object sessionUserId = session == null ? null : session.getAttribute("userId");
        if(!(sessionUserId instanceof Integer UID)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Vui lòng đăng nhập trước khi đặt hàng!"));
        }
        if(request.getVariantId()<=0){
            throw new IllegalArgumentException("Mã biến thể phải lớn hơn 0!");
        }
        if(request.getQuantity()<1){
            throw new IllegalArgumentException("Số lượng nhỏ nhất là 1!");
        }
        int orderId = createOrderService.execute(UID , request.getVariantId() , request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("orderId",orderId));
    }
    @GetMapping
    public ResponseEntity<List<OrderSummary>> getListOrder(HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession(false);
        Object value = session == null ? null : session.getAttribute("userId");
        if(value instanceof Integer userId) {
//            return ResponseEntity.status(HttpStatus.OK).body(getOrderService.execute(userId));
            // có thể viết gọn lại như sau:
            return ResponseEntity.ok(getOrderService.execute(userId)); // Http code = 200 và body đều như nhau cho cả 2 cách viết
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(   //ResponseEntity<?> thể hiện trả j cx đc , để trùy trường hợp mà chúng ta trả cho FE
            @PathVariable(name = "orderId") int orderId,
            HttpServletRequest httpServletRequest
    ){
        HttpSession httpSession = httpServletRequest.getSession(false);
        Object sessionUserId = httpSession == null ? null : httpSession.getAttribute("userId");
        if(sessionUserId instanceof Integer userId){
            return ResponseEntity.ok(getOrderDetailService.execute(orderId , userId));
        }
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Cần đăng nhập để xem hóa đơn"));
    }
}
