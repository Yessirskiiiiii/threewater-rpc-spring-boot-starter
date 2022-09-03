package com.threewater.rpc.common.protocol;

import com.threewater.rpc.common.entity.RpcRequest;
import com.threewater.rpc.common.entity.RpcResponse;

import java.io.*;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/02/20:37
 * @Description: Java 序列化消息协议
 */
public class JavaSerializeMessageProtocol implements MessageProtocol {
    @Override
    public byte[] encodeRequest(RpcRequest request) throws Exception {
        return this.serialize(request);
    }

    @Override
    public RpcRequest decodeRequest(byte[] data) throws Exception {
        return (RpcRequest) this.deserialize(data);
    }

    @Override
    public byte[] encodeResponse(RpcResponse response) throws Exception {
        return new byte[0];
    }

    @Override
    public RpcResponse decodeResponse(byte[] data) throws Exception {
        return (RpcResponse) this.deserialize(data);
    }

    // 序列化
    private byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(o);
        return bout.toByteArray();
    }

    // 反序列化
    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
        return in.readObject();
    }

}
