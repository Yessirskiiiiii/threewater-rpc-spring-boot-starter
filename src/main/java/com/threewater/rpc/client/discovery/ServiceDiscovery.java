package com.threewater.rpc.client.discovery;

import com.threewater.common.entity.Service;

import java.util.List;

/**
 * @Author: ThreeWater
 * @Date: 2022/08/31/19:33
 * @Description: 服务发现接口，定义服务发现规范
 */
public interface ServiceDiscovery {

    /**
     * 通过服务名获取该服务名下的服务列表
     * @param name 服务名
     * @return 服务列表
     */
    List<Service> findServiceList(String name);

}
