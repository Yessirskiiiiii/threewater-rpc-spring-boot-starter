package com.threewater.rpc.client.discovery;

import com.alibaba.fastjson.JSON;
import com.threewater.rpc.common.constants.RpcConstant;
import com.threewater.rpc.common.entity.Service;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/01/11:46
 * @Description: Zookeeper 实现服务发现
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private final CuratorFramework zkClient;

    public ZookeeperServiceDiscovery(String zkAddress) {
        // retryPolicy: 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        zkClient = CuratorFrameworkFactory.newClient(zkAddress, retryPolicy);
        zkClient.start();
    }

    /**
     * 使用 Zookeeper 客户端 Curator，通过服务名获取服务列表
     * @param name 服务名格式：接口全路径
     * @return 服务列表
     */
    @Override
    public List<Service> findServiceList(String name) throws Exception {
        String servicePath = RpcConstant.ZK_SERVICE_PATH + RpcConstant.PATH_DELIMITER + name + "/service";
        List<String> children = zkClient.getChildren().forPath(servicePath);
        // ofNullable：如果 children 不为空就将其赋值 ArrayList，为空创建一个空对象集合赋值给 newList，也就避免了空指针异常
        return Optional.ofNullable(children).orElse(new ArrayList<>()).stream().map(str -> {
            String deCh = null;
            try {
                // 使用decode解码出服务名字，再根据服务名字，转换成对应的服务
                deCh = URLDecoder.decode(str, RpcConstant.UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return JSON.parseObject(deCh, Service.class); // Json 字符串 → Service 对象
        }).collect(Collectors.toList());
    }

    public CuratorFramework getZkClient() {
        return zkClient;
    }

}
