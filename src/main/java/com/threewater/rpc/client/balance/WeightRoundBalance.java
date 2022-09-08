package com.threewater.rpc.client.balance;

import com.threewater.rpc.annotation.LoadBalanceAno;
import com.threewater.rpc.common.constants.LoadBalanceConstant;
import com.threewater.rpc.common.entity.Service;

import java.util.List;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/08/10:50
 * @Description: 加权轮询算法
 */
@LoadBalanceAno(value = LoadBalanceConstant.BALANCE_WEIGHT_ROUND)
public class WeightRoundBalance implements LoadBalance {

    private int index;

    @Override
    public synchronized Service chooseOne(List<Service> services) {
        int weightSum = services.stream().mapToInt(Service::getWeight).sum();
        int number = (index++) % weightSum;
        for(Service service : services){
            if (service.getWeight() > number){
                return service;
            }
            number -= service.getWeight();
        }
        return null;
    }

}
