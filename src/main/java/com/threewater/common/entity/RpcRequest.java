package com.threewater.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: ThreeWater
 * @Date: 2022/08/31/19:46
 * @Description: Rpc 请求消息
 */
@Getter
@Setter
public class RpcRequest implements Serializable {

    /**
     * 请求消息 ID
     */
    private String requestId;

    /**
     * 请求的服务名
     */
    private String serviceName;

    /**
     * 请求服务调用的方法名
     */
    private String method;

    /**
     * 请求服务调用的方法的参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 请求服务调用的方法的参数
     */
    private Object[] parameters;

}
