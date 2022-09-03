package com.threewater.rpc.server.net;

import com.threewater.rpc.server.net.handler.RequestHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/02/20:46
 * @Description: Rpc 网络服务端抽象类，定义服务端规范
 */
@AllArgsConstructor
@Getter
@Setter
public abstract class RpcServer {

    /**
     * 服务端口
     */
    protected int port;

    /**
     * 服务协议
     */
    protected String protocol;

    /**
     * 请求处理者
     */
    protected RequestHandler requestHandler;

    /**
     * 开启服务
     */
    public abstract void start();

    /**
     * 关闭服务
     */
    public abstract void stop();

}
