package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.exception.XueChengPlusException;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.learning.service.impl
 * @className: MyCourseTablesServiceImpl
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/8 22:14
 * @version: 1.0
 */
@Service
@Slf4j
public class MyCourseTablesServiceImpl implements MyCourseTablesService {

    @Autowired
    XcChooseCourseMapper xcChooseCourseMapper;

    @Autowired
    XcCourseTablesMapper xcCourseTablesMapper;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {

        // 调用内容管理查询课程的收费信息
        CoursePublish coursePublish = contentServiceClient.getCoursepublish(courseId);
        if (null == coursePublish) {
            XueChengPlusException.cast("课程不存在");
        }

        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        // 收费规则
        String charge = coursePublish.getCharge();
        if ("201000".equals(charge)) {
            // 如果免费 则向选课记录表写入数据
            xcChooseCourse = addFreeCourse(userId, coursePublish);

            // 向到我的课程表写入记录
            XcCourseTables xcCourseTables = addCourseTabls(xcChooseCourse);

        } else {

            xcChooseCourse = addChargeCoruse(userId, coursePublish);
        }

        // 判断学生的学习资格

        XcCourseTablesDto xcCourseTablesDto = getLearningStatus(userId , courseId);

        XcChooseCourseDto xcChooseCourseDto = new XcChooseCourseDto();
        BeanUtils.copyProperties(xcChooseCourseDto , xcCourseTablesDto);
        xcChooseCourseDto.setLearnStatus(xcCourseTablesDto.getLearnStatus());

        return xcChooseCourseDto;
    }

    @Override
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId) {

        XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();
        // 查询我的课程表， 如果查不到说明没有选课
        XcCourseTables xcCourseTables = xcCourseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>()
                .eq(XcCourseTables::getUserId , userId )
                .eq(XcCourseTables::getCourseId , courseId));

        if(null == xcCourseTables){
            // 未选课 设置code
            xcCourseTablesDto.setLearnStatus("702002");
        }else{
            // 如果查到了 判断是否过期
            BeanUtils.copyProperties(xcCourseTables , xcCourseTablesDto);

            if (xcCourseTables.getValidtimeEnd().isBefore(LocalDateTime.now())) {
                // 已过期 设置code
                xcCourseTablesDto.setLearnStatus("702003");
            }
            else{
                xcCourseTablesDto.setLearnStatus("702001");
            }
        }
        return xcCourseTablesDto;
    }

    public XcChooseCourse addFreeCourse(String userId, CoursePublish coursePublish) {
        // 如果存在选课记录并且选课状态为成功 就直接返回
        // 课程id
        Long courseId = coursePublish.getId();
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, courseId)
                .eq(XcChooseCourse::getOrderType, "700001")
                .eq(XcChooseCourse::getStatus, "701001");
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(queryWrapper);

        if(xcChooseCourses.size() > 0 ){
            return xcChooseCourses.get(0);
        }

        // 向选课记录表写入数据
        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        xcChooseCourse.setCourseId(coursePublish.getId());
        xcChooseCourse.setCourseName(coursePublish.getName());
        xcChooseCourse.setCoursePrice(0f);//免费课程价格为0
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursePublish.getCompanyId());
        xcChooseCourse.setOrderType("700001");//免费课程
        xcChooseCourse.setCreateDate(LocalDateTime.now());
        xcChooseCourse.setStatus("701001");//选课成功

        xcChooseCourse.setValidDays(365);//免费课程默认365
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());
        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));
        int insert = xcChooseCourseMapper.insert(xcChooseCourse);
        if(insert <= 0){
            XueChengPlusException.cast("添加选课记录失败");
        }
        return xcChooseCourse;

    }

    //添加收费课程
    public XcChooseCourse addChargeCoruse(String userId, CoursePublish coursePublish) {

        // 如果存在选课记录并且选课状态为成功 就直接返回
        // 课程id
        Long courseId = coursePublish.getId();
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, courseId)
                .eq(XcChooseCourse::getOrderType, "700002")
                .eq(XcChooseCourse::getStatus, "701002");
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(queryWrapper);

        if(xcChooseCourses.size() > 0 ){
            return xcChooseCourses.get(0);
        }

        // 向选课记录表写入数据
        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        xcChooseCourse.setCourseId(coursePublish.getId());
        xcChooseCourse.setCourseName(coursePublish.getName());
        xcChooseCourse.setCoursePrice(0f);//免费课程价格为0
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursePublish.getCompanyId());
        xcChooseCourse.setOrderType("700002");//免费课程
        xcChooseCourse.setCreateDate(LocalDateTime.now());
        xcChooseCourse.setStatus("701002");//选课成功

        xcChooseCourse.setValidDays(365);//免费课程默认365
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());
        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));
        int insert = xcChooseCourseMapper.insert(xcChooseCourse);
        if(insert <= 0){
            XueChengPlusException.cast("添加选课记录失败");
        }
        return xcChooseCourse;
    }


    //添加到我的课程表
    public XcCourseTables addCourseTabls(XcChooseCourse xcChooseCourse) {

        String status = xcChooseCourse.getStatus();
        if(!"701001".equals(status)){
            XueChengPlusException.cast("选课未成功，无法添加到课程表");
        }


        XcCourseTables xcCourseTables = getXcCourseTables(xcChooseCourse);

        if(null == xcCourseTables){
            xcCourseTables = new XcCourseTables();
            BeanUtils.copyProperties(xcChooseCourse , xcCourseTables);

            xcCourseTables.setCourseType(xcChooseCourse.getOrderType());
            xcCourseTables.setUpdateDate(LocalDateTime.now());

            int insert = xcCourseTablesMapper.insert(xcCourseTables);

            if(insert <= 0){
                XueChengPlusException.cast("添加到我的课程表异常");
            }
        }

        return xcCourseTables;
    }

    public XcCourseTables getXcCourseTables(XcChooseCourse xcChooseCourse){
        XcCourseTables xcCourseTables = xcCourseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId, xcChooseCourse.getUserId())
                .eq(XcCourseTables::getCourseId, xcChooseCourse.getCourseId()));
        return xcCourseTables;
    }
}
