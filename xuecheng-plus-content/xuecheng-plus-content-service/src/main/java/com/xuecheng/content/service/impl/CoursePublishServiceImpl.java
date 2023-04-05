package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.exception.XueChengPlusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        if(courseBaseInfo == null){
            XueChengPlusException.cast("课程未找到");
        }

        if(courseBaseInfo.getAuditStatus().equals("202003")){
            XueChengPlusException.cast("课程已提交，请等待审核");
        }
        if(courseBaseInfo.getPic().isEmpty()){
            XueChengPlusException.cast("课程图片为空，请上传图片");
        }

        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if(teachplanTree == null || teachplanTree.size() == 0){
            XueChengPlusException.cast("课程计划为空,请编写课程计划");
        }

        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfo , coursePublishPre);

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

        if(coursePublishObj == null){
            coursePublishPreMapper.insert(coursePublishPre);
        }
        else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        // 更新课程基本信息表
        CourseBase courseBase = courseBaseMapper.selectById(courseId);

        // 修改课程基本信息状态为已提交
        courseBase.setAuditStatus("202003");

        courseBaseMapper.updateById(courseBase);

    }
}
