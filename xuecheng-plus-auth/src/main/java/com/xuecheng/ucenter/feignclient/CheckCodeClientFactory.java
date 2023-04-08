package com.xuecheng.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.ucenter.feignclient
 * @className: CheckCodeClientFactory
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/8 9:06
 * @version: 1.0
 */
@Slf4j
@Component
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable throwable) {
        return new CheckCodeClient() {
            @Override
            public Boolean verify(String key, String code) {
                log.debug("远程调用验证码校验发生熔断异常：{}" , throwable.getMessage());
                return null;
            }
        };
    }
}
