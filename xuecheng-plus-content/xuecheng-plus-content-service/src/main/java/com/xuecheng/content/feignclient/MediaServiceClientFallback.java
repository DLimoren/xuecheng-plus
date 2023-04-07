package com.xuecheng.content.feignclient;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.content.feignclient
 * @className: MediaServiceClientFallback
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/6 16:10
 * @version: 1.0
 */
public class MediaServiceClientFallback implements MediaServiceClient{
    @Override
    public String upload(MultipartFile filedata, String objectname) throws IOException {
        return null;
    }
}
