package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.content.api
 * @className: CourseOpenController
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/5 12:43
 * @version: 1.0
 */
@RestController
@RequestMapping("/open")
public class CourseOpenController {

    @Autowired
    CourseBaseService courseBaseService;

    @Autowired
    CoursePublishService coursePublishService;

    // 根据课程id查询课程信息
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId){
        CoursePreviewDto coursePreviewDto = coursePublishService.getCoursePreviewInfo(courseId);
        return coursePreviewDto;

    }


}
