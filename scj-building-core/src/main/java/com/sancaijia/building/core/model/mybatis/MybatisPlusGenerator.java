package com.sancaijia.building.core.model.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * 代碼生成
 */
public class MybatisPlusGenerator {
    //生成文件所在项目路径
    private static String baseProjectPath = "E:\\sancaijia";
    //基本包名
    private static String basePackage="com.sancaijia";
    //作者
    private static String authorName="Cc";
    //要生成的表名
    private static String[] tables= {"t_dict"};
    //数据库配置四要素
    private static String driverName = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://120.77.221.218:13306/crm?serverTimezone=Hongkong&useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static String username = "root";
    private static String password = "123456";


    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(baseProjectPath);
        gc.setFileOverride(true);
        gc.setActiveRecord(true);
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(false);// XML columList
        gc.setOpen(true);//生成后打开文件夹
        gc.setAuthor(authorName);
        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName(driverName);
        dsc.setUsername(username);
        dsc.setPassword(password);
        dsc.setUrl(url);
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // strategy.setCapitalMode(true);// 全局大写命名 ORACLE 注意
         strategy.setTablePrefix(new String[]{"t_"});// 此处可以修改为您的表前缀
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
        strategy.setInclude(tables); // 需要生成的表
        strategy.setEntityLombokModel(true);//是否为lombok模型（默认 false）
        // strategy.setExclude(new String[]{"test"}); // 排除生成的表
        strategy.setRestControllerStyle(true);
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(basePackage);
        pc.setController("controller");
        mpg.setPackageInfo(pc);
        mpg.execute();
    }
}
