package com.threewater.rpc.common.protocol;

import com.threewater.rpc.annotation.MessageProtocolAno;
import com.threewater.rpc.common.constants.MessageProtocolConstant;
import com.threewater.rpc.common.entity.RpcRequest;
import com.threewater.rpc.common.entity.RpcResponse;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/08/18:51
 * @Description: Protobuf 序列化消息协议
 */
@MessageProtocolAno(value = MessageProtocolConstant.PROTOCOL_PROTOBUF)
public class ProtoBufMessageProtocol implements MessageProtocol {

    @Override
    public byte[] encodeRequest(RpcRequest request) {
        return this.serialize(request);
    }

    @Override
    public RpcRequest decodeRequest(byte[] data) {
        return this.deserialize(data, RpcRequest.class);
    }

    @Override
    public byte[] encodeResponse(RpcResponse response) {
        return this.serialize(response);
    }

    @Override
    public RpcResponse decodeResponse(byte[] data) {
        return this.deserialize(data, RpcResponse.class);
    }

    /**
     * 序列化
     */
    private <T> byte[] serialize(T object) {
        Schema<T> schema = RuntimeSchema.getSchema((Class<T>) object.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        final byte[] result;
        try {
            result = ProtobufIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
        return result;
    }

    /**
     * 反序列化
     */
    private <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T t = schema.newMessage();
        ProtobufIOUtil.mergeFrom(data, t, schema);
        return t;
    }

}
