package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.xml.internal.bind.v2.TODO;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CourseCategoryService;
import com.xuecheng.exception.XueChengPlusException;
import com.xuecheng.model.PageParams;
import com.xuecheng.model.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class    CourseBaseServiceImpl implements CourseBaseService {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto) {

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()),CourseBase::getName,courseParamsDto.getCourseName());

        // 审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()),CourseBase::getAuditStatus , courseParamsDto.getAuditStatus());
        // 发布状态
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()) , CourseBase::getStatus , courseParamsDto.getPublishStatus());
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        List<CourseBase> records = pageResult.getRecords();
        long total = pageResult.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<CourseBase>(records,total,pageParams.getPageNo(),pageParams.getPageSize());

        return courseBasePageResult;
    }

    /**
     * @param companyId:
     * @param dto:
     * @return CourseBaseInfoDto
     * @author CAIXI
     * @description TODO
     * @date 2023/4/1 8:39
     */
    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        //合法性校验
//        if (StringUtils.isBlank(dto.getName())) {
//            XueChengPlusException.cast("课程名称为空");
//        }
//
//        if (StringUtils.isBlank(dto.getMt())) {
//            XueChengPlusException.cast("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getSt())) {
//            XueChengPlusException.cast("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getGrade())) {
//            XueChengPlusException.cast("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(dto.getTeachmode())) {
//            XueChengPlusException.cast("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(dto.getUsers())) {
//            XueChengPlusException.cast("适应人群为空");
//        }
//
//        if (StringUtils.isBlank(dto.getCharge())) {
//            XueChengPlusException.cast("收费规则为空");
//        }

        // 向课程基本信息表course_base写入数据
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(dto , courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");

        int insert = courseBaseMapper.insert(courseBase);
        if(insert <= 0){
            XueChengPlusException.cast("添加课程失败");
        }

        // 向课程营销表course_market写入数据

        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto , courseMarket);
        Long courseId = courseBase.getId();
        courseMarket.setId(courseId);
        saveCourseMarket(courseMarket);
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }

    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null ){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();

        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket , courseBaseInfoDto);
        }

        //todo:课程分类的名称设置到courseBaseInfoDto中

        // 课程大分类
        CourseCategory mtObj = courseCategoryMapper.selectById(courseBase.getMt());

        String mtName = mtObj.getName();
        // 课程小分类
        CourseCategory stObj = courseCategoryMapper.selectById(courseBase.getSt());

        String stName = stObj.getName();

        courseBaseInfoDto.setStName(stName);
        courseBaseInfoDto.setMtName(mtName);


        return courseBaseInfoDto;
    }


    /**
     * @param companyId: 机构id
     * @param editCourseDto: 修改课程信息
     * @return CourseBaseInfoDto 课程详细信息
     * @author CAIXI
     * @description TODO
     * @date 2023/4/1 15:38
     */
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId ,EditCourseDto editCourseDto) {

        CourseBase courseBase = courseBaseMapper.selectById(editCourseDto.getId());
        System.out.println("机构id" + courseBase.getCompanyId());
        if(!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }
        BeanUtils.copyProperties(editCourseDto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        // 更新数据库
        int i = courseBaseMapper.updateById(courseBase);
        if(i <= 0){
            XueChengPlusException.cast("修改课程失败");
        }

        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseBase.getId());
        return courseBaseInfo;
    }


    private int saveCourseMarket(CourseMarket courseMarket){
        String charge = courseMarket.getCharge();
        if(StringUtils.isEmpty(charge)){
            XueChengPlusException.cast("收费规则为空");
        }
        if(charge.equals("201001")){
              if(courseMarket.getPrice() == null || courseMarket.getPrice() <= 0){
                  XueChengPlusException.cast("课程价格不能为空或者小于零");
              }
        }
        Long id = courseMarket.getId();
        CourseMarket courseMarket1 = courseMarketMapper.selectById(id);
        if(courseMarket1 == null){
            return courseMarketMapper.insert(courseMarket);
        }else{
            return courseMarketMapper.updateById(courseMarket);
        }
    }




}
