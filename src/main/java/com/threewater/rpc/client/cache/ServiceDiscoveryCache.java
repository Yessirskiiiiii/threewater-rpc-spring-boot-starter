package com.threewater.rpc.client.cache;

import com.threewater.rpc.common.entity.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/02/20:08
 * @Description: 本地服务列表缓存
 */
public class ServiceDiscoveryCache {

    /**
     * key: serviceName
     * value: 服务列表
     */
    private static final Map<String, List<Service>> SERVICE_MAP = new ConcurrentHashMap<>();

    /**
     * 添加至本地缓存
     */
    public static void put(String serviceName, List<Service> serviceList) {
        SERVICE_MAP.put(serviceName, serviceList);
    }

    /**
     * 去除指定的缓存
     */
    public static void remove(String serviceName, Service service) {
        SERVICE_MAP.computeIfPresent(serviceName, (key, value) ->
                value.stream().filter(o -> !o.toString().equals(service.toString())).collect(Collectors.toList())
        );
    }

    /**
     * 去除该服务名所有的缓存
     */
    public static void removeAll(String serviceName) {
        SERVICE_MAP.remove(serviceName);
    }

    /**
     * 判断缓存是否为空
     */
    public static boolean isEmpty(String serviceName) {
        return SERVICE_MAP.get(serviceName) == null || SERVICE_MAP.get(serviceName).size() == 0;
    }

    /**
     * 得到该服务名称的缓存服务列表
     */
    public static List<Service> get(String serviceName) {
        return SERVICE_MAP.get(serviceName);
    }

}
