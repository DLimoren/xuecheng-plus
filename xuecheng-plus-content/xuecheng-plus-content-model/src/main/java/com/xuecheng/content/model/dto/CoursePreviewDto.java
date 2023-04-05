package com.xuecheng.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.content.model.dto
 * @className: CoursePreviewDto
 * @author: Caixi
 * @description: 课程预览的模型类
 * @date: 2023/4/5 8:51
 * @version: 1.0
 */

@Data
public class CoursePreviewDto {

    // 课程基本信息，营销信息
    private CourseBaseInfoDto courseBase;

    // 课程计划信息
    private List<TeachplanDto> teachplans;

    // 课程师资信息

}
