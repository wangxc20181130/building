package com.sancaijia.building.backend.feign.fallback;

import com.sancaijia.building.backend.feign.OrderMicroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 降级处理
 */
@Component
public class OrderMicroServiceFallBack implements OrderMicroService {
    @Override
    public ResponseEntity createUser(String phone) {
        System.out.println("创建用户时降级处理："+phone);
        return new ResponseEntity("error", HttpStatus.BAD_REQUEST);
    }
}
