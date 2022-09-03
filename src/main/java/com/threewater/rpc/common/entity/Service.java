package com.threewater.rpc.common.entity;

import lombok.*;

import java.util.Objects;

/**
 * @Author: ThreeWater
 * @Date: 2022/08/31/19:35
 * @Description: 服务类
 */
@Getter
@Setter
@ToString
public class Service {

    /**
     * 服务名称
     */
    private String name;

    /**
     * 序列化协议
     */
    private String protocol;

    /**
     * 服务地址（格式：ip:port）
     */
    private String address;

    /**
     * 权重，越大优先级越高
     */
    private Integer weight;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(name, service.name) &&
                Objects.equals(protocol, service.protocol) &&
                Objects.equals(address, service.address) &&
                Objects.equals(weight, service.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, protocol, address, weight);
    }
}
