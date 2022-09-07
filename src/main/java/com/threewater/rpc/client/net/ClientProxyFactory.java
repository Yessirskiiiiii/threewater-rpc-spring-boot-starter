package com.threewater.rpc.client.net;

import com.threewater.rpc.client.balance.LoadBalance;
import com.threewater.rpc.client.cache.ServiceDiscoveryCache;
import com.threewater.rpc.client.discovery.ServiceDiscovery;
import com.threewater.rpc.common.entity.RpcRequest;
import com.threewater.rpc.common.entity.RpcResponse;
import com.threewater.rpc.common.entity.Service;
import com.threewater.rpc.common.protocol.MessageProtocol;
import com.threewater.rpc.exception.RpcException;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/02/17:25
 * @Description: 客户端代理工厂，用于创建远程服务代理类，封装请求以及发送操作
 */
@Getter
@Setter
public class ClientProxyFactory {

    private ServiceDiscovery serviceDiscovery;

    private RpcClient rpcClient;

    private Map<String, MessageProtocol> supportMessageProtocols;

    private final Map<Class<?>, Object> objectCache = new HashMap<>();

    private LoadBalance loadBalance;

    public <T> T getProxy(Class<T> clazz) {
        return (T) objectCache.computeIfAbsent(clazz, clz ->
                Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, new ClientInvocationHandler(clz))
        );
    }

    private class ClientInvocationHandler implements InvocationHandler {

        private final Class<?> clazz;

        public ClientInvocationHandler(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("toString")) {
                return proxy.toString();
            }
            if (method.getName().equals("hashCode")) {
                return 0;
            }
            // 1. 获得服务信息
            String serviceName = clazz.getName();
            List<Service> services = getServiceList(serviceName);
            // 负载均衡算法选择一个服务提供者
            Service service = loadBalance.chooseOne(services);
            // 2. 构造request对象
            RpcRequest request = new RpcRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setServiceName(service.getName());
            request.setMethod(method.getName());
            request.setParameters(args);
            request.setParameterTypes(method.getParameterTypes());
            // 3. 协议层编组
            MessageProtocol messageProtocol = supportMessageProtocols.get(service.getProtocol());
            RpcResponse response = rpcClient.sendRequest(request, service, messageProtocol);
            // 4. 结果处理
            if (response == null) {
                throw new RpcException("the response is null");
            }
            if (response.getException() != null) {
                return response.getException();
            }
            return response.getReturnValue();
        }

        /**
         * 根据服务名称获取可用的服务列表
         * @param serviceName 服务名称
         * @return 服务列表
         */
        private List<Service> getServiceList(String serviceName) throws Exception {
            List<Service> services;
            synchronized (serviceName) {
                // 先查本地缓存，缓存没有再查询 Zookeeper
                if (ServiceDiscoveryCache.isEmpty(serviceName)) {
                    services = serviceDiscovery.findServiceList(serviceName);
                    if (services == null || services.size() == 0) {
                        throw new RpcException("No provider available!");
                    }
                    ServiceDiscoveryCache.put(serviceName, services);
                } else {
                    services = ServiceDiscoveryCache.get(serviceName);
                }
            }
            return services;
        }
    }

}
