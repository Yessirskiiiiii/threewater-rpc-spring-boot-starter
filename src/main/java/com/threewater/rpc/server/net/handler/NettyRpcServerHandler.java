package com.threewater.rpc.server.net.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/03/16:47
 * @Description: 服务端请求处理器
 */
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServerHandler.class);

    private RequestHandler requestHandler;

    private static final ExecutorService pool = new ThreadPoolExecutor(
            4,
            10,
            200,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactoryBuilder()
                    .setNameFormat("rpcServer-%d")
                    .build());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("Channel active :{}", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        pool.submit(() -> {
            try {
                logger.debug("the server receives message :{}", msg);
                ByteBuf byteBuf = (ByteBuf) msg;
                // 1. 消息写入reqData
                byte[] reqData = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(reqData);
                // 2. 手动回收
                ReferenceCountUtil.release(byteBuf);
                // 3. 处理请求
                byte[] respData = requestHandler.handleRequest(reqData);
                ByteBuf respBuf = Unpooled.buffer(respData.length);
                respBuf.writeBytes(respData);
                logger.debug("Send response:{}", respBuf);
                ctx.writeAndFlush(respBuf);
            } catch (Exception e) {
                logger.error("server read exception", e);
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error("Exception occurred:{}", cause.getMessage());
        ctx.close();
    }

    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }
}
