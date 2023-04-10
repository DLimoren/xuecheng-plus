package com.xuecheng.content.api;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.content.api
 * @className: CoursePublishController
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/5 8:33
 * @version: 1.0
 */

@Controller
@Api("课程发布接口")
public class CoursePublishController {




    @Autowired
    CoursePublishService coursePublishService;
    private Long courseId;

    @ApiOperation("查询课程发布信息")
    @GetMapping("/course/whole/{courseId}")
    @ResponseBody
    public CoursePreviewDto getCoursePublishPre(@PathVariable("courseId") Long courseId){
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);

        if(null == coursePublish){
            return coursePreviewDto;
        }

        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish , courseBaseInfoDto);

        String teachplanJson = coursePublish.getTeachplan();

        List<TeachplanDto> teachplans = JSON.parseArray(teachplanJson, TeachplanDto.class);

        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachplans(teachplans);

        return coursePreviewDto;
    }

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId) {

        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("course_template");
        modelAndView.addObject("model", coursePreviewInfo);

        return modelAndView;
    }



    @ResponseBody
    @PostMapping ("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId,courseId);
    }


    @ResponseBody
    @PostMapping("/coursepublish/{courseId}")
    public void coursePublish(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.publish(companyId , courseId);
    }

    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursePublish(@PathVariable("courseId") Long courseId){
        // 查询课程发布信息
        CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);
        return coursePublish;
    }

}
