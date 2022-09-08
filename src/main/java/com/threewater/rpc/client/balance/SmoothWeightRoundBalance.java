package com.threewater.rpc.client.balance;

import com.threewater.rpc.annotation.LoadBalanceAno;
import com.threewater.rpc.common.constants.LoadBalanceConstant;
import com.threewater.rpc.common.entity.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/08/15:21
 * @Description: 平滑加权轮询算法
 */
@LoadBalanceAno(value = LoadBalanceConstant.BALANCE_SMOOTH_WEIGHT_ROUND)
public class SmoothWeightRoundBalance implements LoadBalance {

    /**
     * key: 服务名称
     * value: 权重
     */
    private static final Map<String, Integer> map = new HashMap<>();

    @Override
    public synchronized Service chooseOne(List<Service> services) {
        services.forEach(service -> map.computeIfAbsent(service.toString(), key -> service.getWeight()));
        Service maxWeightService = null;
        int weightSum = services.stream().mapToInt(Service::getWeight).sum();

        for (Service service : services) {
            Integer currentWeight = map.get(service.toString());
            if (maxWeightService == null || currentWeight > map.get(maxWeightService.toString())) {
                maxWeightService = service;
            }
        }

        assert maxWeightService != null;

        map.put(maxWeightService.toString(), map.get(maxWeightService.toString()) - weightSum);

        for (Service service : services) {
            Integer currentWeight = map.get(service.toString());
            map.put(service.toString(), currentWeight + service.getWeight());
        }

        return maxWeightService;
    }

}
