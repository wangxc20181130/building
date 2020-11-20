package com.sancaijia.building.backend.feign;

import com.sancaijia.building.backend.feign.fallback.OrderMicroServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "scj-building-user",fallback = OrderMicroServiceFallBack.class)
public interface OrderMicroService {

    @GetMapping("/user/create/user/{phone}")
    ResponseEntity createUser(@PathVariable("phone")String phone);
}
