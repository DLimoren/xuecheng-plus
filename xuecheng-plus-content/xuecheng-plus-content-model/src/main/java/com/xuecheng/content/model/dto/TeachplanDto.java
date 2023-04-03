package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.content.model.dto
 * @className: TeachplanDto
 * @author: Caixi
 * @description: 课程计划信息模型类
 * @date: 2023/4/1 21:59
 * @version: 1.0
 */

@Data
@ToString
public class TeachplanDto extends Teachplan {
    // 媒资管理信息
    private TeachplanMedia teachplanMedia;

    // 小章节list
    private List<TeachplanDto> teachPlanTreeNodes;


}
