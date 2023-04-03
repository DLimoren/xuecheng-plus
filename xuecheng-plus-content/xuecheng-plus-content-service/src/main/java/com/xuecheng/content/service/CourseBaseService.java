package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.model.PageParams;
import com.xuecheng.model.PageResult;

public interface CourseBaseService {
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams , QueryCourseParamsDto courseParamsDto);

    CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto dto);


    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    CourseBaseInfoDto updateCourseBase(Long companyId ,EditCourseDto editCourseDto);
}
