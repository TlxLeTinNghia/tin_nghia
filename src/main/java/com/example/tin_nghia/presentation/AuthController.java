package com.example.tin_nghia.presentation;

import com.example.tin_nghia.application.service.LoginService;
import com.example.tin_nghia.domain.User;
import com.example.tin_nghia.presentation.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LoginService loginService;
    public AuthController(
            LoginService loginService
    ){
        this.loginService = loginService;
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String , Object>> login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest){
        Optional<User> rs = loginService.execute(request.getUserName() , request.getPassword());
        if(rs.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Mật khẩu và tên đăng nhập không đúng!"));
        }
        User user = rs.get();
        HttpSession session = httpServletRequest.getSession();
        httpServletRequest.changeSessionId();
        session.setAttribute("userId",user.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("userId",user.getUserId() , "userName" , user.getUserName()));
    }
}
