package com.threewater.rpc.common.protocol;

import com.threewater.rpc.common.entity.RpcRequest;
import com.threewater.rpc.common.entity.RpcResponse;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/01/15:39
 * @Description: 消息协议接口，定义请求编码、响应解码、请求解码、响应编码的规范
 */
public interface MessageProtocol {

    /**
     * 请求编码
     * @param request 请求信息
     * @return 请求编码后的字节数组
     * @throws Exception 异常
     */
    byte[] encodeRequest(RpcRequest request) throws Exception;

    /**
     * 请求解码
     * @param data 请求编码后的字节数组
     * @return 请求信息
     * @throws Exception 异常
     */
    RpcRequest decodeRequest(byte[] data) throws Exception;

    /**
     * 响应编码
     * @param response 响应信息
     * @return 响应编码后的字节数组
     * @throws Exception 异常
     */
    byte[] encodeResponse(RpcResponse response) throws Exception;

    /**
     * 响应解码
     * @param data 响应编码后的字节数组
     * @return 响应信息
     * @throws Exception 异常
     */
    RpcResponse decodeResponse(byte[] data) throws Exception;

}
