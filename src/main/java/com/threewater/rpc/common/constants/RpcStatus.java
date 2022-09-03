package com.threewater.rpc.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ThreeWater
 * @Date: 2022/08/31/19:58
 * @Description: Rpc 状态枚举类
 */
@AllArgsConstructor
@Getter
public enum RpcStatus {

    /**
     * 成功
     */
    SUCCESS(200, "SUCCESS"),

    /**
     * 失败
     */
    ERROR(500, "ERROR"),

    /**
     * 未找到
     */
    NOT_FOUND(404, "NOT FOUND");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态描述
     */
    private final String description;

}
