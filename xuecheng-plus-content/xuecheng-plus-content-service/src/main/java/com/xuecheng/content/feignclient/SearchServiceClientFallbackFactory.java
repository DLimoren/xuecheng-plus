package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.content.feignclient
 * @className: SearchServiceClientFallbackFactory
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/7 11:48
 * @version: 1.0
 */

@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {

    @Override
    public SearchServiceClient create(Throwable throwable) {
         return new SearchServiceClient() {
             @Override
             public Boolean add(CourseIndex courseIndex) {
                 log.error("添加课程索引发生熔断，索引信息: {} ,熔断异常信息：{}" , courseIndex , throwable.toString() ,throwable);

                 return false;
             }
         };
    }
}
