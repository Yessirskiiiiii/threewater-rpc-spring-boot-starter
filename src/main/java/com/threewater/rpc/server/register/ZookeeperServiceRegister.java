package com.threewater.rpc.server.register;

import com.alibaba.fastjson.JSON;
import com.threewater.rpc.common.entity.Service;
import com.threewater.rpc.common.entity.ServiceObject;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import java.net.InetAddress;
import java.net.URLEncoder;

import static com.threewater.rpc.common.constants.RpcConstant.*;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/03/15:24
 * @Description: Zookeeper 服务注册器，提供服务注册、暴露服务的能力
 */
public class ZookeeperServiceRegister extends DefaultServiceRegister {

    private final CuratorFramework zkClient;

    public ZookeeperServiceRegister(String zkAddress, Integer port, String protocol, Integer weight) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        zkClient = CuratorFrameworkFactory.newClient(zkAddress, retryPolicy);
        this.port = port;
        this.protocol = protocol;
        this.weight = weight;
    }

    @Override
    public void register(ServiceObject serviceObject) throws Exception {
        super.register(serviceObject);
        Service service = new Service();
        String host = InetAddress.getLocalHost().getHostAddress();
        String address = host + ":" + port;
        service.setAddress(address);
        service.setName(serviceObject.getClazz().getName());
        service.setProtocol(protocol);
        service.setWeight(this.weight);
        this.exportService(service);
    }

    /**
     * 服务暴露，将服务信息保存到 Zookeeper 上
     * @param service 服务信息
     */
    private void exportService(Service service) {
        String serviceName = service.getName();
        String uri = JSON.toJSONString(service);
        try {
            uri = URLEncoder.encode(uri, UTF_8);
            String servicePath = ZK_SERVICE_PATH + PATH_DELIMITER + serviceName + "/service";
            // Zookeeper 中没有该节点就创建
            if (zkClient.checkExists().forPath(servicePath) == null) {
                // creatingParentsIfNeeded(): 递归创建父节点
                // CreateMode.PERSISTENT: 持久节点
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            String uriPath = servicePath + PATH_DELIMITER + uri;
            if (zkClient.checkExists().forPath(uriPath) != null) {
                // 删除之前的节点
                zkClient.delete().forPath(uriPath);
            }
            // 创建一个临时节点，会话失效即被清理
            // CreateMode.EPHEMERAL: 临时节点
            zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(uriPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
