package com.xuecheng.media.api;

import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.model.RestResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.media.api
 * @className: MediaOpenController
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/5 12:48
 * @version: 1.0
 */


@RestController
@RequestMapping("/open")
public class MediaOpenController {

    @Autowired
    MediaFileService mediaFileService;

    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId){
        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);

        if(mediaFiles == null){
            return RestResponse.validfail("找不到视频");
        }
        String url = mediaFiles.getUrl();
        if(StringUtils.isEmpty(url)){
            return RestResponse.validfail("该视频正在处理中");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }
}
