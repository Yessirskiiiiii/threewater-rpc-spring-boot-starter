package com.threewater.rpc.server.net;

import com.threewater.rpc.server.net.handler.NettyRpcServerHandler;
import com.threewater.rpc.server.net.handler.RequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/03/16:33
 * @Description: Netty Rpc 服务端
 */
public class NettyRpcServer extends RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServer.class);

    private Channel channel;

    public NettyRpcServer(int port, String protocol, RequestHandler requestHandler) {
        super(port, protocol, requestHandler);
    }

    @Override
    public void start() {
        // 进行服务端的配置
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        NettyRpcServerHandler RPC_HANDLER = new NettyRpcServerHandler();
        RPC_HANDLER.setRequestHandler(requestHandler);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(RPC_HANDLER);
                        }
                    });
            // 启动服务
            ChannelFuture future = serverBootstrap.bind(port).sync();
            logger.debug("Server started successfully.");
            channel = future.channel();
            // 等待服务通道关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("start netty sever failed, msg:{}", e.getMessage());
        } finally {
            // 释放线程组资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        this.channel.close();
    }

}
