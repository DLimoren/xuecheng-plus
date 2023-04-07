package com.xuecheng.content.feignclient;


import com.xuecheng.content.config.MultipartSupportConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


// 使用fallback定义降级类是无法拿到熔断异常，
// 使用fallbackFactory可以拿到熔断的异常信息
@Configuration
@FeignClient(value = "media-api",configuration = MultipartSupportConfig.class,fallbackFactory = MediaServiceClientFallbackFactory.class)
public interface MediaServiceClient {

    @PostMapping(value = "/media/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart("filedata") MultipartFile filedata ,
                                      @RequestParam(value = "objectName",required = false) String objectname) throws IOException ;

}
