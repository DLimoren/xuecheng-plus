package com.xuecheng.content.service.jobhandler;

import com.xuecheng.content.feignclient.CourseIndex;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.exception.XueChengPlusException;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.content.service.jobhandler
 * @className: CoursePublishTask
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/6 10:08
 * @version: 1.0
 */

@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    SearchServiceClient searchServiceClient;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        process(shardIndex,shardTotal,"course_publish" , 50 , 60);
    }
    @Override
    public boolean execute(MqMessage mqMessage) {

        // 从mqMessage拿到课程id
        Long courseId = Long.parseLong(mqMessage.getBusinessKey1());

        // 课程静态化 上传html页面到minio
        generateCourseHtml(mqMessage , courseId);



        //  向elasticsearch写索引数据
        saveCourseIndex(mqMessage , courseId);

        // 向redis写缓存

        saveCache(mqMessage , courseId);

        // 返回true 表示任务完成

        return true;
    }

    private void saveCache(MqMessage mqMessage, Long courseId) {
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stagThree = mqMessageService.getStageThree(taskId);
        if(stagThree > 0){
            log.debug("课程缓存到redis已完成，无需处理...");
            return ;
        }

        // 将课程缓存到redis



        // 修改缓存任务状态为已完成
        mqMessageService.completedStageThree(taskId);
    }

    /**
     * @param :  * @return null
     * @author CAIXI
     * @description 课程静态化处理  第一阶段任务
     * @date 2023/4/6 10:21
     */
    private void generateCourseHtml(MqMessage mqMessage , Long courseId){
        // 消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 作任务幂等性处理
        // 取出该阶段的执行状态
        int stagOne = mqMessageService.getStageOne(taskId);
        if(stagOne > 0){
            log.debug("课程静态化任务完成，无需处理...");
            return ;
        }

        // 开始进行课程静态化处理
        File file = coursePublishService.generateCourseHtml(courseId);

        if(file == null){
            XueChengPlusException.cast("生成的静态页面为空 ");
        }

        coursePublishService.uploadCourseHtml(courseId , file);

        // 任务处理完成，修改任务状态为完成
        mqMessageService.completedStageOne(taskId);
    }

    /**
     * @param :  * @return null
     * @author CAIXI
     * @description 保存课程索引信息  第二阶段任务
     * @date 2023/4/6 10:21
     */
    private void saveCourseIndex(MqMessage mqMessage , Long courseId){
        // 任务幂等性处理
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageTwo = mqMessageService.getStageTwo(taskId);
        if(stageTwo > 0){
            log.debug("课程索引信息已写入,无需执行...");
            return ;
        }

        // 查询课程信息 ，调用搜索服务添加索引...

        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);

        // 远程调用
        Boolean add = searchServiceClient.add(courseIndex);
        if(!add){
            XueChengPlusException.cast("远程调用添加课程索引失败");
        }


        // 完成本阶段任务，将状态表更新为完成
        mqMessageService.completedStageTwo(taskId);
    }
}
