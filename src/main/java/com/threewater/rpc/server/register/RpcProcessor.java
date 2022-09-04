package com.threewater.rpc.server.register;

import com.threewater.rpc.annotation.InjectService;
import com.threewater.rpc.annotation.Service;
import com.threewater.rpc.client.cache.ServiceDiscoveryCache;
import com.threewater.rpc.client.discovery.ZookeeperServiceDiscovery;
import com.threewater.rpc.client.net.ClientProxyFactory;
import com.threewater.rpc.common.constants.RpcConstant;
import com.threewater.rpc.common.entity.ServiceObject;
import com.threewater.rpc.server.net.RpcServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/04/15:01
 * @Description: RPC 处理者，支持服务自动暴露以及注入
 */
public class RpcProcessor implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RpcProcessor.class);

    private ClientProxyFactory clientProxyFactory;

    private ServiceRegister serviceRegister;

    private RpcServer rpcServer;

    // 当所有的 Bean 被成功装载，即 Spring 启动完毕过后会收到一个事件通知
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // WEB(spring mvc) 环境防止重复触发
        if (Objects.isNull(event.getApplicationContext().getParent())) {
            ApplicationContext context = event.getApplicationContext();
            startServer(context);
            injectService(context);
        }
    }

    /**
     * 启动服务(注册服务)
     * @param context Spring 上下文
     */
    private void startServer(ApplicationContext context) {
        // 找到所有被 @Service 注解过的 Bean 名称和对象
        Map<String, Object> beans = context.getBeansWithAnnotation(Service.class);
        if (beans.size() > 0) {
            boolean startServerFlag = true;
            for (Object object : beans.values()) {
                try {
                    Class<?> clazz = object.getClass();
                    Class<?>[] interfaces = clazz.getInterfaces();
                    ServiceObject serviceObject;
                    // 如果该类实现了多个接口或没有实现接口，则用注解里的用户编写的 value 作为服务名
                    if (interfaces.length != 1) {
                        Service service = clazz.getAnnotation(Service.class);
                        String value = service.value();
                        if (value.equals("")) {
                            startServerFlag = false;
                            throw new UnsupportedOperationException("The exposed interface is not specific with '" + object.getClass().getName() + "'");
                        }
                        serviceObject = new ServiceObject(value, Class.forName(value), object);
                    } else {
                        // 如果只实现了一个接口就用父类的 className 作为服务名
                        Class<?> superClass = interfaces[0];
                        serviceObject = new ServiceObject(superClass.getName(), superClass, object);
                    }
                    // 服务的注册
                    serviceRegister.register(serviceObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (startServerFlag) {
                rpcServer.start();
            }
        }
    }

    /**
     * 注入服务
     * @param context Spring 上下文
     */
    private void injectService(ApplicationContext context) {
        // 获取容器中所有 Bean 的名字
        String[] names = context.getBeanDefinitionNames();
        for (String name : names) {
            Class<?> clazz = context.getType(name);
            if (Objects.isNull(clazz)) {
                continue;
            }
            // 获得某个类的所有声明的字段，即包括 public、private 和 protected
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                // 找出标记了 @InjectService 注解的字段
                InjectService injectService = field.getAnnotation(InjectService.class);
                if (injectService == null) {
                    continue;
                }
                Class<?> fieldClass = field.getType();
                Object object = context.getBean(name);
                field.setAccessible(true);
                try {
                    // 替换该 @InjectService 注解的字段的值为动态代理的对象
                    field.set(object, clientProxyFactory.getProxy(fieldClass));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                // 添加注入的远程服务至本地服务缓存列表
                ServiceDiscoveryCache.SERVICE_CLASS_NAMES.add(fieldClass.getName());
            }
        }
        // 由于服务端注册的是临时节点，所以如果服务端下线节点会被移除
        // 只要监听 Zookeeper 的子节点，如果新增或删除子节点就直接清空本地缓存即可
        // 避免服务端因为宕机或网络问题下线了，缓存却还在就会导致客户端请求已经不可用的服务端，增加请求失败率
        if (clientProxyFactory.getServerDiscovery() instanceof ZookeeperServiceDiscovery) {
            ZookeeperServiceDiscovery serviceDiscovery = (ZookeeperServiceDiscovery) clientProxyFactory.getServerDiscovery();
            CuratorFramework zkClient = serviceDiscovery.getZkClient();
            ServiceDiscoveryCache.SERVICE_CLASS_NAMES.forEach(name -> {
                String servicePath = RpcConstant.ZK_SERVICE_PATH + RpcConstant.PATH_DELIMITER + name + RpcConstant.PATH_DELIMITER + "service";
                PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
                pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                        String path = pathChildrenCacheEvent.getData().getPath();
                        logger.debug("Child change parentPath:[{}] -- event:[{}]", path, pathChildrenCacheEvent);
                        String[] arr = path.split(RpcConstant.PATH_DELIMITER);
                        ServiceDiscoveryCache.removeAll(arr[2]);
                    }
                });
                try {
                    pathChildrenCache.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            logger.info("subscribe service zk node successfully");
        }
    }

}
