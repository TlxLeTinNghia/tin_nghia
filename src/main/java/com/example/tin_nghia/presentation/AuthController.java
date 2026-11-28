package com.example.tin_nghia.presentation;

import com.example.tin_nghia.application.exception.UserNotFoundException;
import com.example.tin_nghia.application.service.CreateUserService;
import com.example.tin_nghia.application.service.GetUserService;
import com.example.tin_nghia.application.service.LoginService;
import com.example.tin_nghia.domain.User;
import com.example.tin_nghia.presentation.dto.LoginRequest;
import com.example.tin_nghia.presentation.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LoginService loginService;
    private final CreateUserService createUserService;
    private final GetUserService getUserService;
    public AuthController(
            LoginService loginService,
            CreateUserService createUserService,
            GetUserService getUserService
    ){
        this.loginService = loginService;
        this.createUserService = createUserService;
        this.getUserService = getUserService;
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
    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(
            @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest
            ){
        // Kiểm tra dữ liệu đầu vào có trống hoặc hợp lệ hay ko
        // Tên không đc Blank() , mật khẩu ko được null hoặc dưới 8 kí tự và gồm cả hoa thường và ký tự đặc biệt
        if(request.getUserName() == null||request.getUserName().isBlank() ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Tên không được để trống."));
        }
        String pw = request.getPassword();
        if(pw == null){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Mật khẩu không được để trống!"));
        }
        if(pw.length()<8){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Mật khẩu không được ít hơn 8 ký tự!"));
        }
        boolean hasLowercase = false;
        boolean hasUppercase = false;
        boolean hasSpecial = false;
        for (int i = 0 ; i<pw.length();i++) {
            char ch = pw.charAt(i);
            if(Character.isLowerCase(ch)){
                hasLowercase = true;
                continue;
            }
            if(Character.isUpperCase(ch)){
                hasUppercase = true;
                continue;
            }
            if(!Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch)){
                hasSpecial = true;
            }
        }
        if(!hasLowercase || !hasUppercase || !hasSpecial){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Mật khẩu gồm chữ hoa, thường và kí tự đặc biệt!"));
        }
        createUserService.execute(request.getUserName(),pw);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Đăng ký thành công!"));
    }
    @GetMapping("/me")
    public ResponseEntity<?> getUser(HttpServletRequest httpServletRequest){
        HttpSession httpSession = httpServletRequest.getSession(false);
        Object sessionUserId = httpSession == null ? null : httpSession.getAttribute("userId");
        if(sessionUserId instanceof Integer userId){
            User user;
            try {
                user = getUserService.execute(userId);
            }catch (UserNotFoundException ex){
                httpSession.invalidate();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Không còn phiên đăng nhập hợp lệ!"));
            }
            return ResponseEntity.ok(Map.of("userId",user.getUserId(),"userName",user.getUserName(),"role",user.getRole()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Bạn chưa đăng nhập!"));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession(false);
        Object sessionUserId = session == null ? null : session.getAttribute("userId");
        if(sessionUserId instanceof Integer userId){
            session.invalidate();
            return ResponseEntity.ok("Đăng xuất thành công!");
        }
        return ResponseEntity.ok("Bạn đã đăng xuất!");
    }
}
