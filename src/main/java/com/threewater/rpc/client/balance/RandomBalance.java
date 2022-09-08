package com.threewater.rpc.client.balance;

import com.threewater.rpc.annotation.LoadBalanceAno;
import com.threewater.rpc.common.constants.LoadBalanceConstant;
import com.threewater.rpc.common.entity.Service;

import java.util.List;
import java.util.Random;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/02/20:35
 * @Description: 随机算法
 */
@LoadBalanceAno(value = LoadBalanceConstant.BALANCE_RANDOM)
public class RandomBalance implements LoadBalance {

    private static final Random random = new Random();

    @Override
    public Service chooseOne(List<Service> services) {
        return services.get(random.nextInt(services.size()));
    }

}
