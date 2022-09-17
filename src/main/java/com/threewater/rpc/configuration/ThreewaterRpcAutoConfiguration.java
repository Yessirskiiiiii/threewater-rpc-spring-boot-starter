package com.threewater.rpc.configuration;

import com.threewater.rpc.annotation.LoadBalanceAno;
import com.threewater.rpc.annotation.MessageProtocolAno;
import com.threewater.rpc.client.balance.LoadBalance;
import com.threewater.rpc.client.discovery.ZookeeperServiceDiscovery;
import com.threewater.rpc.client.net.ClientProxyFactory;
import com.threewater.rpc.client.net.NettyRpcClient;
import com.threewater.rpc.common.protocol.MessageProtocol;
import com.threewater.rpc.exception.RpcException;
import com.threewater.rpc.properties.ThreewaterRpcProperties;
import com.threewater.rpc.server.net.NettyRpcServer;
import com.threewater.rpc.server.net.RpcServer;
import com.threewater.rpc.server.net.handler.RequestHandler;
import com.threewater.rpc.server.register.RpcProcessor;
import com.threewater.rpc.server.register.ServiceRegister;
import com.threewater.rpc.server.register.ZookeeperServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @Author: ThreeWater
 * @Date: 2022/08/31/17:23
 * @Description: starter 的自动配置类，注入需要的 bean
 */
@Configuration
@ConditionalOnClass(ThreewaterRpcProperties.class)
@EnableConfigurationProperties(ThreewaterRpcProperties.class)
public class ThreewaterRpcAutoConfiguration {

    /**
     * 注入 starter 的参数类模块
     */
    @Bean
    public ThreewaterRpcProperties threewaterRpcProperties() {
        return new ThreewaterRpcProperties();
    }

    /**
     * 注入服务注册模块
     */
    @Bean
    public ServiceRegister serviceRegister(@Autowired ThreewaterRpcProperties threewaterRpcProperties) throws InterruptedException {
        return new ZookeeperServiceRegister(
                threewaterRpcProperties.getRegisterAddress(),
                threewaterRpcProperties.getServerPort(),
                threewaterRpcProperties.getProtocol(),
                threewaterRpcProperties.getWeight()
        );
    }

    /**
     * 注入请求处理器模块
     */
    @Bean
    public RequestHandler requestHandler(@Autowired ServiceRegister serviceRegister,
                                         @Autowired ThreewaterRpcProperties threewaterRpcProperties) {
        return new RequestHandler(
                getMessageProtocol(threewaterRpcProperties.getProtocol()),
                serviceRegister);
    }

    /**
     * 注入服务端模块
     */
    @Bean
    public RpcServer rpcServer(@Autowired RequestHandler requestHandler,
                               @Autowired ThreewaterRpcProperties threewaterRpcProperties) {
        return new NettyRpcServer(
                threewaterRpcProperties.getServerPort(),
                threewaterRpcProperties.getProtocol(),
                requestHandler);
    }

    /**
     * 注入代理对象模块
     */
    @Bean
    public ClientProxyFactory proxyFactory(@Autowired ThreewaterRpcProperties threewaterRpcProperties) throws InterruptedException {
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory();

        // 设置服务发现者
        clientProxyFactory.setServiceDiscovery(new ZookeeperServiceDiscovery(threewaterRpcProperties.getRegisterAddress()));
        // 设置支持的序列化协议
        clientProxyFactory.setSupportMessageProtocols(buildSupportMessageProtocols());
        // 设置负载均衡算法
        clientProxyFactory.setLoadBalance(getLoadBalance(threewaterRpcProperties.getLoadBalance()));
        // 设置网络层实现
        clientProxyFactory.setRpcClient(new NettyRpcClient());

        return clientProxyFactory;
    }

    /**
     * 使用 spi 匹配符合配置的序列化算法
     * @param name 用户在配置项填入的序列化算法名称
     * @return 序列化协议对象
     */
    private MessageProtocol getMessageProtocol(String name) {
        // 加载 META-INF/services 文件下的配置文件
        ServiceLoader<MessageProtocol> load = ServiceLoader.load(MessageProtocol.class);
        for (MessageProtocol messageProtocol : load) {
            // 获取序列化协议对象上的注解对象
            MessageProtocolAno annotation = messageProtocol.getClass().getAnnotation(MessageProtocolAno.class);
            Assert.notNull(annotation, "message protocol name can not be empty!");
            // 如果注解上的序列化算法名称和用户在配置项填入名称一样，则注入该序列化对象
            if (name.equals(annotation.value())) {
                return messageProtocol;
            }
        }
        throw new RpcException("invalid message protocol config!");
    }

    /**
     * 使用 spi 匹配符合配置的负载均衡算法
     * @param name 用户在配置项填入的负载均衡算法名称
     * @return 负载均衡算法对象
     */
    private LoadBalance getLoadBalance(String name) {
        ServiceLoader<LoadBalance> load = ServiceLoader.load(LoadBalance.class);
        for (LoadBalance loadBalance : load) {
            LoadBalanceAno annotation = loadBalance.getClass().getAnnotation(LoadBalanceAno.class);
            Assert.notNull(annotation, "load balance name can not be empty!");
            if (name.equals(annotation.value())) {
                return loadBalance;
            }
        }
        throw new RpcException("invalid load balance config!");
    }

    /**
     * 构建 ClientProxyFactory 的 supportMessageProtocols 字段
     */
    private Map<String, MessageProtocol> buildSupportMessageProtocols() {
        Map<String, MessageProtocol> supportMessageProtocols = new HashMap<>();
        ServiceLoader<MessageProtocol> load = ServiceLoader.load(MessageProtocol.class);
        for (MessageProtocol messageProtocol : load) {
            MessageProtocolAno annotation = messageProtocol.getClass().getAnnotation(MessageProtocolAno.class);
            Assert.notNull(annotation, "message protocol name can not be empty!");
            supportMessageProtocols.put(annotation.value(), messageProtocol);
        }
        return supportMessageProtocols;
    }

    /**
     * 注入 Rpc 处理者
     */
    @Bean
    public RpcProcessor rpcProcessor(@Autowired ClientProxyFactory clientProxyFactory,
                                     @Autowired ServiceRegister serviceRegister,
                                     @Autowired RpcServer rpcServer) {
        return new RpcProcessor(clientProxyFactory, serviceRegister, rpcServer);
    }

}
