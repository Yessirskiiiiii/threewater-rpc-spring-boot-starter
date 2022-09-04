package com.threewater.rpc.server.net.handler;

import com.threewater.rpc.common.constants.RpcStatus;
import com.threewater.rpc.common.entity.RpcRequest;
import com.threewater.rpc.common.entity.RpcResponse;
import com.threewater.rpc.common.entity.ServiceObject;
import com.threewater.rpc.common.protocol.MessageProtocol;
import com.threewater.rpc.server.register.ServiceRegister;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/03/15:08
 * @Description: 请求处理者，提供请求解码、响应编码等操作
 */
@AllArgsConstructor
@Getter
@Setter
public class RequestHandler {

    private MessageProtocol protocol;

    private ServiceRegister serviceRegister;

    public byte[] handleRequest(byte[] data) throws Exception {
        // 1. 请求消息解码
        RpcRequest request = this.protocol.decodeRequest(data);
        // 2. 查找对应的服务对象
        ServiceObject serviceObject = serviceRegister.getServiceObject(request.getServiceName());

        RpcResponse response;

        if (serviceObject == null) {
            response = new RpcResponse(RpcStatus.NOT_FOUND);
        } else {
            try {
                // 3. 反射调用服务的对应方法
                Method method = serviceObject.getClazz().getMethod(request.getMethod(), request.getParameterTypes());
                Object returnValue = method.invoke(serviceObject.getObj(), request.getParameters());
                response = new RpcResponse(RpcStatus.SUCCESS);
                response.setReturnValue(returnValue);
            } catch (Exception e) {
                response = new RpcResponse(RpcStatus.ERROR);
                response.setException(e);
            }
        }
        // 4. 响应消息编码
        response.setRequestId(request.getRequestId());
        return this.protocol.encodeResponse(response);
    }

}
