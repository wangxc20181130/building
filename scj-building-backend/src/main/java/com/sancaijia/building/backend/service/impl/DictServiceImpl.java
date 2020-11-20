package com.sancaijia.building.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sancaijia.building.backend.mapper.DictMapper;
import com.sancaijia.building.backend.service.DictService;
import com.sancaijia.building.entity.Dict;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author Cc
 * @since 2020-11-18
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

}
