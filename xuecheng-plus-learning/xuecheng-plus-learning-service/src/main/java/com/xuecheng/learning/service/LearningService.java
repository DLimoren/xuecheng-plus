package com.xuecheng.learning.service;

import com.xuecheng.model.RestResponse;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.learning.service
 * @className: LearningService
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/10 9:49
 * @version: 1.0
 */
public interface LearningService {

    public RestResponse<String> getVideo(String userId , Long  courseId , Long teachplanId , String mediaId);
}
