package com.threewater.rpc.server.register;

import com.threewater.rpc.common.entity.ServiceObject;

/**
 * @Author: ThreeWater
 * @Date: 2022/09/03/15:10
 * @Description: 服务注册接口，定义服务注册规范
 */
public interface ServiceRegister {

    /**
     * 注册服务对象
     * @param serviceObject 服务对象
     */
    void register(ServiceObject serviceObject) throws Exception;

    /**
     * 根据服务名称找到对应的服务对象
     * @param name 服务名称
     * @return 服务对象
     */
    ServiceObject getServiceObject(String name) throws Exception;

}
