package com.threewater.common.entity;

import com.threewater.common.constants.RpcStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: ThreeWater
 * @Date: 2022/08/31/19:46
 * @Description: Rpc 响应消息
 */
@Getter
@Setter
public class RpcResponse implements Serializable {

    /**
     * 响应消息 ID
     */
    private String requestId;

    /**
     * 请求调用的方法的返回值
     */
    private Object returnValue;

    /**
     * 响应异常
     */
    private Exception exception;

    /**
     * 响应状态
     */
    private RpcStatus rpcStatus;

    public RpcResponse(RpcStatus rpcStatus) {
        this.rpcStatus = rpcStatus;
    }

}
