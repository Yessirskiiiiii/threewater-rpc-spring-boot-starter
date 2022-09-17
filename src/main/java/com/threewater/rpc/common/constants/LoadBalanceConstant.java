package com.threewater.rpc.common.constants;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/04/20:13
 * @Description: 负载均衡固定值
 */
public class LoadBalanceConstant {

    private LoadBalanceConstant() {
    }

    /**
     * 负载均衡算法：随机
     */
    public static final String BALANCE_RANDOM = "random";

    /**
     * 负载均衡算法：加权随机
     */
    public static final String BALANCE_WEIGHT_RANDOM = "weightRandom";

    /**
     * 负载均衡算法：轮询
     */
    public static final String BALANCE_ROUND = "round";

    /**
     * 负载均衡算法：加权轮询
     */
    public static final String BALANCE_WEIGHT_ROUND = "weightRound";

    /**
     * 负载均衡算法：平滑加权轮询
     */
    public static final String BALANCE_SMOOTH_WEIGHT_ROUND = "smoothWeightRound";

}
