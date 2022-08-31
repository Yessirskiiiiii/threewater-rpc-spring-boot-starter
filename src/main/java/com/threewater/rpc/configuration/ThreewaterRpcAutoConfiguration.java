package com.threewater.rpc.configuration;

import com.threewater.rpc.properties.ThreewaterRpcProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: ThreeWater
 * @Date: 2022/08/31/17:23
 * @Description: starter 的自动配置类，注入需要的 bean
 */
@Configuration
@ConditionalOnClass(ThreewaterRpcProperties.class)
@EnableConfigurationProperties(ThreewaterRpcProperties.class)
public class ThreewaterRpcAutoConfiguration {

    @Bean
    public ThreewaterRpcProperties threewaterRpcProperties() {
        return new ThreewaterRpcProperties();
    }

}
