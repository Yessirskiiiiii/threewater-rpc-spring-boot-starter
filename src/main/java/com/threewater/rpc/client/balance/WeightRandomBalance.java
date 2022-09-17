package com.threewater.rpc.client.balance;

import com.threewater.rpc.annotation.LoadBalanceAno;
import com.threewater.rpc.common.constants.LoadBalanceConstant;
import com.threewater.rpc.common.entity.Service;

import java.util.List;
import java.util.Random;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/17/14:20
 * @Description: 加权随机算法
 */
@LoadBalanceAno(value = LoadBalanceConstant.BALANCE_WEIGHT_RANDOM)
public class WeightRandomBalance implements LoadBalance {

    private static final Random random = new Random();

    @Override
    public Service chooseOne(List<Service> services) {
        int weightSum = services.stream().mapToInt(Service::getWeight).sum();
        int number = random.nextInt(weightSum);
        for(Service service : services){
            if (service.getWeight() > number){
                return service;
            }
            number -= service.getWeight();
        }
        return null;
    }

}
