package yiyi.example.dubbo.dynamicdatasource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import yiyi.example.dubbo.dynamicdatasource.constants.DataSourceConstants;

/**
 * 自定义数据源注解
 *
 * @author bwang018 2020/12/19
 **/
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DS {
    /**
     * 数据源名称
     * @return
     */
    String value() default DataSourceConstants.DS_KEY_MASTER;
}
