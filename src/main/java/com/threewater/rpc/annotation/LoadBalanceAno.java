package com.threewater.rpc.annotation;

import java.lang.annotation.*;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/04/20:07
 * @Description: 负载均衡注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoadBalanceAno {

    String value() default "";

}
