package com.threewater.rpc.common.protocol;

import com.threewater.rpc.annotation.MessageProtocolAno;
import com.threewater.rpc.common.constants.MessageProtocolConstant;
import com.threewater.rpc.common.entity.RpcRequest;
import com.threewater.rpc.common.entity.RpcResponse;
import com.threewater.rpc.exception.RpcException;

import java.io.*;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/02/20:37
 * @Description: Java 序列化消息协议
 */
@MessageProtocolAno(value = MessageProtocolConstant.PROTOCOL_JAVA)
public class JavaSerializeMessageProtocol implements MessageProtocol {

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

    /**
     * 序列化
     */
    private <T> byte[] serialize(T object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RpcException("序列化失败：" + e);
        }
    }

    /**
     * 反序列化
     */
    private <T> T deserialize(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("反序列化失败" + e);
        }
    }

}
