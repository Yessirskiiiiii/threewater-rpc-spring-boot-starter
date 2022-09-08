package com.threewater.rpc.common.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.threewater.rpc.annotation.MessageProtocolAno;
import com.threewater.rpc.common.constants.MessageProtocolConstant;
import com.threewater.rpc.common.entity.RpcRequest;
import com.threewater.rpc.common.entity.RpcResponse;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/08/15:38
 * @Description: Kryo 序列化消息协议
 */
@MessageProtocolAno(value = MessageProtocolConstant.PROTOCOL_KRYO)
public class KryoMessageProtocol implements MessageProtocol {

    // 由于 Kryo 本身是线程不安全的，因此将其存放在 ThreadLocal 中
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 检测循环依赖，默认值为true
        kryo.setReferences(false);
        // 注册需要序列化的消息类型
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        // 设置默认的实例化器
        Kryo.DefaultInstantiatorStrategy strategy = (Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy();
        strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        return kryo;
    });

    /**
     * 获得当前线程的 Kryo 实例
     * @return 当前线程的 Kryo 实例
     */
    public static Kryo getInstance() {
        return kryoThreadLocal.get();
    }

    @Override
    public byte[] encodeRequest(RpcRequest request) {
        return this.serialize(request);
    }

    @Override
    public RpcRequest decodeRequest(byte[] data) {
        return (RpcRequest) this.deserialize(data);
    }

    @Override
    public byte[] encodeResponse(RpcResponse response) {
        return this.serialize(response);
    }

    @Override
    public RpcResponse decodeResponse(byte[] data) {
        return (RpcResponse) this.deserialize(data);
    }

    /**
     * 序列化
     */
    private byte[] serialize(Object object) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Output output = new Output(os);
        Kryo kryo = getInstance();
        kryo.writeClassAndObject(output, object);
        byte[] bytes = output.toBytes();
        output.flush();
        return bytes;
    }

    /**
     * 反序列化
     */
    private Object deserialize(byte[] data) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        Input input = new Input(is);
        Kryo kryo = getInstance();
        Object object = kryo.readClassAndObject(input);
        input.close();
        return object;
    }

}
