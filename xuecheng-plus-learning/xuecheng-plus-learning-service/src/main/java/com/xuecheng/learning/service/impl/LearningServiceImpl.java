package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.exception.XueChengPlusException;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.service.MyCourseTablesService;
import com.xuecheng.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.learning.service.impl
 * @className: LearningServiceImpl
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/10 9:49
 * @version: 1.0
 */

@Service
@Slf4j
public class LearningServiceImpl implements LearningService {


    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Autowired
    ContentServiceClient contentServiceClient;


    @Autowired
    MediaServiceClient mediaServiceClient;


    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {

        // 查询课程

        CoursePublish coursePublish = contentServiceClient.getCoursepublish(courseId);
        if(null == coursePublish){
            return RestResponse.validfail("课程不存在");
        }



        // todo:如果支持试学 则返回试学课程视频地址
//        String teachplan = coursePublish.getTeachplan();
//        JSON.parseArray(teachplan , Teachplan.class);


        //  获取学习资格
        if(userId.isEmpty()){ // 用户未登录
            String charge = coursePublish.getCharge();
            if("201000".equals(charge)){
                // 有资格学习 ，返回视频的播放地址
                // 远程调用媒资获取视频地址
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }

        }
        // 获取学习资格
        XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);
        String learnStatus = learningStatus.getLearnStatus();
        if("702002".equals(learnStatus)){
            return RestResponse.validfail("无法学习，因为没有选课或选课后未支付");
        }else if("702003".equals(learnStatus)){
            return RestResponse.validfail("已过期，需申请续期或重新支付");
        }else{
            // 有资格学习 ，返回视频的播放地址
            //  远程调用媒资获取视频地址
            RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
            return playUrlByMediaId;
        }
    }
}
