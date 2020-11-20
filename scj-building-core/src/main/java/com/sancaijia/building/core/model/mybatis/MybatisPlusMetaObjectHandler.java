package com.sancaijia.building.core.model.mybatis;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert field....");
//        this.strictInsertFill(metaObject, "createTime", Date.class, DateUtil.date());
        this.strictInsertFill(metaObject, "isDelete", Integer.class, 0);
        this.strictInsertFill(metaObject, "version", Integer.class, 1);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
    }

}