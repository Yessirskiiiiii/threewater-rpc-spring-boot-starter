package com.threewater.rpc.exception;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/02/15:41
 * @Description: Rpc 异常类
 */
public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }

}
