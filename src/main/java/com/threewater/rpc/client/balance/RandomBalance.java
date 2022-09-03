package com.threewater.rpc.client.balance;

import com.threewater.rpc.common.entity.Service;

import java.util.List;
import java.util.Random;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/02/20:35
 * @Description: 随机算法
 */
public class RandomBalance implements LoadBalance {

    private static final Random random = new Random();

    @Override
    public Service chooseOne(List<Service> services) {
        return services.get(random.nextInt(services.size()));
    }
}
