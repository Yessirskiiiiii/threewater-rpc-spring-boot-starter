package com.threewater.rpc.client.net;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.threewater.common.entity.RpcRequest;
import com.threewater.common.entity.Service;
import com.threewater.common.protocol.MessageProtocol;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/01/15:56
 * @Description:
 */
public class NettyRpcClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);

    // 利用线程池异步创建 RPC 客户端
    private static final ExecutorService threadPool = new ThreadPoolExecutor(4, 10, 200,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder()
            .setNameFormat("rpcClient-%d")
            .build());

    // 创建 Netty 的事件循环组
    private final EventLoopGroup loopGroup = new NioEventLoopGroup(4);


    @Override
    public Object sendRequest(RpcRequest rpcRequest, Service service, MessageProtocol messageProtocol) {

        String address = service.getAddress();
        return null;

    }
}
