package com.xuecheng.content.api;


import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.exception.ValidationGroups;
import com.xuecheng.model.PageParams;
import com.xuecheng.model.PageResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseService courseBaseService;

    @ApiOperation("课程分页查询接口")
    @PostMapping ("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams,@RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){

        PageResult<CourseBase> pageResult = courseBaseService.queryCourseBaseList(pageParams , queryCourseParamsDto);
        return pageResult;

    }

    @ApiOperation("新增课程")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDto addCourseDto){

        Long companyId = 12121212L;
        CourseBaseInfoDto courseBase = courseBaseService.createCourseBase(companyId, addCourseDto);

        return courseBase;
    }

    /**
     * @param courseId:
     * @return CourseBaseInfoDto
     * @author CAIXI
     * @description TODO
     * @date 2023/4/1 13:54
     */
    @ApiOperation("根据课程id查询")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){

        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }

}
