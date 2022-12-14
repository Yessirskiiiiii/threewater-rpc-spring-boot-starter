package com.threewater.rpc.common.constants;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/04/20:14
 * @Description: 序列化固定值
 */
public class MessageProtocolConstant {

    private MessageProtocolConstant() {
    }

    /**
     * JDK 序列化协议
     */
    public static final String PROTOCOL_JAVA = "java";

    /**
     * Kryo 序列化协议
     */
    public static final String PROTOCOL_KRYO = "kryo";

    /**
     * Protobuf 序列化协议
     */
    public static final String PROTOCOL_PROTOBUF = "protobuf";

    /**
     * Hessian 序列化协议
     */
    public static final String PROTOCOL_HESSIAN = "hessian";

}
