package com.sancaijia.building.backend.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sancaijia.building.backend.service.DictService;
import com.sancaijia.building.entity.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author Cc
 * @since 2020-11-18
 */
@RestController
@RequestMapping("/dict")
public class DictController {
    @Autowired
    private DictService dictService;
    @GetMapping("/list")
    public ResponseEntity query(){
        List<Dict> list = dictService.list();


//        Dict dict = new Dict();
//        dict.setName("222");
//        dict.setType("22222");
//        dictService.save(dict);
        IPage<Dict> page = new Page<>(1,2);
        IPage<Dict> list1 = dictService.page(page);
        return new ResponseEntity(list1, HttpStatus.OK);
    }
}

