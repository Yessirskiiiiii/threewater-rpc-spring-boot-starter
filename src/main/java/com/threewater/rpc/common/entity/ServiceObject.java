package com.threewater.rpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/03/15:14
 * @Description: 服务持有对象，保存具体的服务信息
 */
@AllArgsConstructor
@Getter
@Setter
public class ServiceObject {

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务 Class
     */
    private Class<?> clazz;

    /**
     * 具体服务对象
     */
    private Object obj;

}
