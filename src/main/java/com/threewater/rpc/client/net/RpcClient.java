package com.threewater.rpc.client.net;

import com.threewater.rpc.common.entity.RpcRequest;
import com.threewater.rpc.common.entity.RpcResponse;
import com.threewater.rpc.common.entity.Service;
import com.threewater.rpc.common.protocol.MessageProtocol;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/01/15:33
 * @Description: Rpc 网络请求客户端，定义请求规范
 */
public interface RpcClient {

    RpcResponse sendRequest(RpcRequest rpcRequest, Service service, MessageProtocol messageProtocol);

}
