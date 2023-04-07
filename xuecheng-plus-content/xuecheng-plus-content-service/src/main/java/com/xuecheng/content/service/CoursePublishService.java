package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public interface CoursePublishService {


    public CoursePreviewDto getCoursePreviewInfo(Long courseId);


    public void commitAudit(Long companyId, Long courseId);


    public void publish(Long companyId ,Long courseId);

    public File generateCourseHtml(Long courseId) ;

    public void uploadCourseHtml(Long courseId , File file);
}
