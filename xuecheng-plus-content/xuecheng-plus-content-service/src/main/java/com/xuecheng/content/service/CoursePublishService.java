package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

public interface CoursePublishService {


    public CoursePreviewDto getCoursePreviewInfo(Long courseId);


    void commitAudit(Long companyId, Long courseId);
}
