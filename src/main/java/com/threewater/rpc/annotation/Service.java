package com.threewater.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/03/19:51
 * @Description: 被该注解标记的服务可提供远程 RPC 访问功能
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface Service {

    // 默认值
    String value() default "";

}
