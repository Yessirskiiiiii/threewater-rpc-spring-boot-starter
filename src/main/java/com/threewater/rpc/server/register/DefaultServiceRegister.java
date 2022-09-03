package com.threewater.rpc.server.register;

import com.threewater.rpc.common.entity.ServiceObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/03/15:20
 * @Description: 默认服务注册器
 */
public abstract class DefaultServiceRegister implements ServiceRegister {

    private final Map<String, ServiceObject> serviceMap = new HashMap<>();

    protected String protocol;

    protected Integer port;

    protected Integer weight;

    @Override
    public void register(ServiceObject serviceObject) throws Exception {
        if (serviceObject == null) {
            throw new IllegalArgumentException("parameter cannot be empty");
        }
        serviceMap.put(serviceObject.getName(), serviceObject);
    }

    @Override
    public ServiceObject getServiceObject(String name) throws Exception {
        return serviceMap.get(name);
    }
}
