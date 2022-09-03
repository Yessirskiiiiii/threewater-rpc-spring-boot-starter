package com.threewater.rpc.client.balance;

import com.threewater.rpc.common.entity.Service;

import java.util.List;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/02/17:39
 * @Description: 负载均衡算法接口
 */
public interface LoadBalance {

    /**
     * 从服务列表中依据负载均衡算法选出一个服务
     * @param services 服务列表
     * @return 服务
     */
    Service chooseOne(List<Service> services);

}
