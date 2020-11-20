package com.sancaijia.building.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/create/user/{phone}")
    public ResponseEntity createUser(@PathVariable("phone")String phone){
        System.out.println("backend服务调用创建user"+phone);
        return new ResponseEntity("创建成功", HttpStatus.OK);
    }
}
