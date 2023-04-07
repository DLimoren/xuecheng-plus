package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.exception.CommonError;
import com.xuecheng.exception.XueChengPlusException;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.model.PageResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.content.service.impl
 * @className: CoursePublishServiceImpl
 * @author: Caixi
 * @description: 课程发布相关接口实现
 * @date: 2023/4/5 8:57
 * @version: 1.0
 */

@Service
@Slf4j
public class CoursePublishServiceImpl implements CoursePublishService {


    @Autowired
    CourseBaseService courseBaseService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseMarketMapper courseMarketMapper;


    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;


    @Autowired
    MediaServiceClient mediaServiceClient;



    @Override
    /**
     * @param courseId:
     * @return CoursePreviewDto
     * @author CAIXI
     * @description TODO
     * @date 2023/4/5 8:58
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();

        //课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);

        // 课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }


    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {

        // 课程待审核信息进行校验 不满足要求就驳回


        // 查询课程图片信息，基本信息，营销信息 插入到课程预发布表
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        if (courseBaseInfo == null) {
            XueChengPlusException.cast("课程未找到");
        }

        if (courseBaseInfo.getAuditStatus().equals("202003")) {
            XueChengPlusException.cast("课程已提交，请等待审核");
        }
        if (courseBaseInfo.getPic().isEmpty()) {
            XueChengPlusException.cast("课程图片为空，请上传图片");
        }

        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (teachplanTree == null || teachplanTree.size() == 0) {
            XueChengPlusException.cast("课程计划为空,请编写课程计划");
        }

        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);

        // 营销信息转json
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String courseMarketJson = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(courseMarketJson);

        // 计划信息转json
        String teachplanTreeJson = JSON.toJSONString(teachplanTree);

        coursePublishPre.setTeachplan(teachplanTreeJson);

        // 设置机构id
        coursePublishPre.setCompanyId(companyId);

        // 修改提交状态
        coursePublishPre.setStatus("202003");

        // 提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());

        // 查询预发布表，如果有记录则更新，没有则插入；
        CoursePublishPre coursePublishObj = coursePublishPreMapper.selectById(courseId);

        if (coursePublishObj == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        // 更新课程基本信息表
        CourseBase courseBase = courseBaseMapper.selectById(courseId);

        // 修改课程基本信息状态为已提交
        courseBase.setAuditStatus("202003");

        courseBaseMapper.updateById(courseBase);

    }

    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {


        // 查询预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);

        // 课程没有审核通过不允许发布
        String status = coursePublishPre.getStatus();
        if (!status.equals("202004")) {
            XueChengPlusException.cast("课程没有审核通过，不允许发布");
        }

        // 向课程发布表写入数据
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        CoursePublish coursePublishObj = coursePublishMapper.selectById(courseId);
        if (coursePublishObj == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }

        // 向消息表写数据
        savePublishCourseMessage(courseId);


        // 删除预发布表数据
        coursePublishPreMapper.deleteById(courseId);

    }

    @Override
    public File generateCourseHtml(Long courseId)  {
        //配置freemarker
        Configuration configuration = new Configuration(Configuration.getVersion());

        //加载模板
        //选指定模板路径,classpath下templates下
        //得到classpath路径
        String classpath = this.getClass().getResource("/").getPath();
        File htmlfile = null;
        try {
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //输出流
            htmlfile = File.createTempFile("coursepublish" , ".html");
            FileOutputStream outputStream = new FileOutputStream(htmlfile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("页面静态化出现问题 courseId : {} error: {}" , courseId , e);
            e.printStackTrace();
        }


        return htmlfile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {

        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        try {
            String upload = mediaServiceClient.upload(multipartFile, "course/" + courseId + ".html");
            if(upload == null) {
                log.debug("远程调用熔断降级 ， 课程id : {}" , courseId);
                XueChengPlusException.cast("上传静态文件过程中出现异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("上传静态文件过程中出现异常");
        }
    }

    public void savePublishCourseMessage(Long courseId) {

        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}
