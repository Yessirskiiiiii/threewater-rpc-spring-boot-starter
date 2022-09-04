package com.threewater.rpc.annotation;

import java.lang.annotation.*;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/03/19:52
 * @Description: 该注解用于注入远程服务
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectService {


}
