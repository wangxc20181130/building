package com.sancaijia.building.core.model.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.Collections;
import java.util.HashMap;

@Slf4j
@Configuration
public class MybatisPlusConfig {

    @Autowired
    private MybatisPlusMetaObjectHandler mybatisPlusMetaObjectHandler;


    // 创建全局配置
    @Bean(name = "globalConfig")
    public GlobalConfig mpGlobalConfig() {
        // 全局配置文件
        GlobalConfig globalConfig = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        // 默认为自增
        dbConfig.setIdType(IdType.AUTO);
        // 全局逻辑删除字段名默认：（0:未删除 1.已删除）
        dbConfig.setLogicDeleteField("isDelete");
        globalConfig.setDbConfig(dbConfig);
        // 元对象字段填充控制器
        globalConfig.setMetaObjectHandler(mybatisPlusMetaObjectHandler);
        // 关闭启动图像
        globalConfig.setBanner(false);
        return globalConfig;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier(value = "basisDataSource") DruidDataSource druidDataSource,
                                               @Qualifier(value = "globalConfig") GlobalConfig globalConfig,
                                               @Qualifier(value = "mybatisPlusInterceptor") MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        log.info("初始化SqlSessionFactory");
        String mapperLocations = "classpath:mybatis/mappers/*.xml";
        String typeAliasesPackage = "com.sancaijia.building.entity.*";

        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(druidDataSource);
        sqlSessionFactoryBean.setGlobalConfig(globalConfig);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        //自动扫描Mapping.xml文件
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mapperLocations));
        // 自动扫描实体类
        sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        // mybatis-plus插件注入配置
        mybatisConfiguration.addInterceptor(mybatisPlusInterceptor);
        sqlSessionFactoryBean.setConfiguration(mybatisConfiguration);

        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     * 3.x.x+ 版本推荐配置
     */
    @Bean(name = "mybatisPlusInterceptor")
    public MybatisPlusInterceptor MybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 乐观锁
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页配置
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }


}