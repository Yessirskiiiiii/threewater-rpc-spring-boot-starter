package com.threewater.rpc.common.protocol;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.threewater.rpc.annotation.MessageProtocolAno;
import com.threewater.rpc.common.constants.MessageProtocolConstant;
import com.threewater.rpc.common.entity.RpcRequest;
import com.threewater.rpc.common.entity.RpcResponse;
import com.threewater.rpc.exception.RpcException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/09/19:29
 * @Description: Hessian 序列化消息协议
 */
@MessageProtocolAno(value = MessageProtocolConstant.PROTOCOL_HESSIAN)
public class HessianMessageProtocol implements MessageProtocol {

    @Override
    public byte[] encodeRequest(RpcRequest request) {
        return this.serialize(request);
    }

    @Override
    public RpcRequest decodeRequest(byte[] data) {
        return this.deserialize(data);
    }

    @Override
    public byte[] encodeResponse(RpcResponse response) {
        return this.serialize(response);
    }

    @Override
    public RpcResponse decodeResponse(byte[] data) {
        return this.deserialize(data);
    }

    public <T> byte[] serialize(T object) {
        byte[] data;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Hessian2Output output = new Hessian2Output(os);
            output.writeObject(object);
            output.getBytesOutputStream().flush();
            output.completeMessage();
            output.close();
            data = os.toByteArray();
        } catch (Exception e) {
            throw new RpcException("序列化失败" + e);
        }
        return data;
    }

    public <T> T deserialize(byte[] data) {
        Object result;
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            Hessian2Input input = new Hessian2Input(is);
            result = input.readObject();
        } catch (Exception e) {
            throw new RpcException("反序列化失败" + e);
        }
        return (T) result;
    }

}
