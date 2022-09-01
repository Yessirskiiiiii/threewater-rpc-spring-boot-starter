package com.threewater.rpc.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: ThreeWater
 * @Date: 2022/08/31/16:42
 * @Description: starter 的参数类
 */
@Component
@ConfigurationProperties(prefix = "threewater.rpc")
@Getter
@Setter
public class ThreewaterRpcProperties {

    /**
     * 服务注册中心地址（默认为 127.0.0.1:2181）
     */
    private String registerAddress = "127.0.0.1:2181";

    /**
     * 服务暴露端口（默认为 9999）
     */
    private Integer serverPort = 9999;

    /**
     * 序列化协议（默认为 jdk 自带的序列化）
     */
    private String protocol = "java";

    /**
     * 负载均衡算法（默认为 random）
     */
    private String loadBalance = "random";

    /**
     * 权重（默认为 1）
     */
    private Integer weight = 1;

}
