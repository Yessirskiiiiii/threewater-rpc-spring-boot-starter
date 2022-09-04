package com.threewater.rpc.annotation;

import java.lang.annotation.*;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/04/20:07
 * @Description: 消息协议序列化方式的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageProtocolAno {

    String value() default "";

}
