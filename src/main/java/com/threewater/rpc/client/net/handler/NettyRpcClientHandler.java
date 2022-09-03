package com.threewater.rpc.client.net.handler;

import com.threewater.rpc.common.entity.RpcRequest;
import com.threewater.rpc.common.entity.RpcResponse;
import com.threewater.rpc.common.protocol.MessageProtocol;
import com.threewater.rpc.exception.RpcException;
import com.threewater.rpc.client.net.NettyRpcClient;
import com.threewater.rpc.client.net.RpcFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/01/16:33
 * @Description: 客户端发送请求处理器
 */
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcClientHandler.class);

    /**
     * 等待通道建立最大时间
     */
    static final int CHANNEL_WAIT_TIME = 4;

    /**
     * 等待响应最大时间
     */
    static final int RESPONSE_WAIT_TIME = 8;

    private volatile Channel channel;

    private final String remoteAddress;

    private static final Map<String, RpcFuture<RpcResponse>> requestMap = new ConcurrentHashMap<>();

    private final MessageProtocol messageProtocol;

    private final CountDownLatch latch = new CountDownLatch(1);

    public NettyRpcClientHandler(MessageProtocol messageProtocol, String remoteAddress) {
        this.messageProtocol = messageProtocol;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        latch.countDown();
    }

    /**
     * 读取数据，同时进行响应消息的解码，将响应结果放入对应的 RpcFuture 中，并释放计时器锁
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("Client reads message:{}", msg);
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] resp = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(resp);
        // 手动回收 byteBuf
        ReferenceCountUtil.release(byteBuf);
        RpcResponse response = messageProtocol.decodeResponse(resp);
        RpcFuture<RpcResponse> future = requestMap.get(response.getRequestId());
        future.setResponse(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error("Exception occurred:{}", cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 连接的服务端异常断开连接，则清理缓存中对应的 serverNode
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.error("channel inactive with remoteAddress:[{}]", remoteAddress);
        NettyRpcClient.connectedServerNodes.remove(remoteAddress);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    public RpcResponse sendRequest(RpcRequest rpcRequest) {
        RpcResponse response = null;
        RpcFuture<RpcResponse> future = new RpcFuture<>();
        requestMap.put(rpcRequest.getRequestId(), future);
        try {
            byte[] data = messageProtocol.encodeRequest(rpcRequest);
            ByteBuf reqBuf = Unpooled.buffer(data.length);
            reqBuf.writeBytes(data);
            if (latch.await(CHANNEL_WAIT_TIME, TimeUnit.SECONDS)) {
                channel.writeAndFlush(reqBuf);
                // 等待响应
                response = future.get(RESPONSE_WAIT_TIME, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            throw new RpcException(e.getMessage());
        } finally {
            requestMap.remove(rpcRequest.getRequestId());
        }
        return response;
    }

}
