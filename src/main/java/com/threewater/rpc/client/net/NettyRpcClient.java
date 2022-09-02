package com.threewater.rpc.client.net;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.threewater.common.entity.RpcRequest;
import com.threewater.common.entity.RpcResponse;
import com.threewater.common.entity.Service;
import com.threewater.common.protocol.MessageProtocol;
import com.threewater.rpc.client.net.handler.SendHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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

    /**
     * 已连接的服务缓存
     * key：服务地址，格式：ip:port
     * value：SendHandler
     */
    public static Map<String, SendHandler> connectedServerNodes = new ConcurrentHashMap<>();

    /**
     * 发送请求
     *
     * @param rpcRequest      请求
     * @param service         服务信息
     * @param messageProtocol 消息协议
     * @return 返回响应
     */
    @Override
    public RpcResponse sendRequest(RpcRequest rpcRequest, Service service, MessageProtocol messageProtocol) {

        String address = service.getAddress();
        synchronized (address) {
            // 请求地址（ip+port）如果在 connectedServerNodes 中存在则使用 connectedServerNodes 中的 handler 处理不再重新建立连接
            if (connectedServerNodes.containsKey(address)) {
                SendHandler handler = connectedServerNodes.get(address);
                logger.info("使用现有的连接");
                return handler.sendRequest(rpcRequest);
            }
            // 不存在则建立新的连接，并将地址和 handler 缓存到 connectedServerNodes 中方便复用
            String[] addrInfo = address.split(":");
            final String serverAddress = addrInfo[0];
            final String serverPort = addrInfo[1];
            final SendHandler handler = new SendHandler(messageProtocol, address);
            threadPool.submit(() -> {
                // 进行客户端的配置
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(loopGroup).channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(handler);
                            }
                        });
                // 启用客户端连接
                ChannelFuture channelFuture = bootstrap.connect(serverAddress, Integer.parseInt(serverPort));
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        // 将地址和 handler 缓存到 connectedServerNodes 中方便复用
                        connectedServerNodes.put(address, handler);
                    }
                });
            });
            logger.info("使用新的连接..........");
            return handler.sendRequest(rpcRequest);
        }

    }
}
