package com.threewater.rpc.client.balance;

import com.threewater.rpc.annotation.LoadBalanceAno;
import com.threewater.rpc.common.constants.LoadBalanceConstant;
import com.threewater.rpc.common.entity.Service;

import java.util.List;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/08/10:45
 * @Description: 轮询算法
 */
@LoadBalanceAno(value = LoadBalanceConstant.BALANCE_ROUND)
public class RoundBalance implements LoadBalance {

    private int index;

    @Override
    public synchronized Service chooseOne(List<Service> services) {
        // 加锁防止多线程情况下，index 超出 services.size()
        if (index == services.size()) {
            index = 0;
        }
        return services.get(index++);
    }

}
