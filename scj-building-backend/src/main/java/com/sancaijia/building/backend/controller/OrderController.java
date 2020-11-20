package com.sancaijia.building.backend.controller;

import com.sancaijia.building.backend.feign.OrderMicroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderMicroService orderMicroService;
    @GetMapping("/create")
    public ResponseEntity createUser(){
        System.out.println("创建订单服务");

        orderMicroService.createUser("13468763623");

        return new ResponseEntity("ok", HttpStatus.OK);
    }
}
