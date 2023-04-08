package com.xuecheng.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.exception
 * @className: GlobalExceptionHandler
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/1 11:37
 * @version: 1.0
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e) {
        log.error("【系统异常】{}",e.getErrMessage(),e);
        return new RestErrorResponse(e.getErrMessage());

    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {

        log.error("【系统异常】{}",e.getMessage(),e);

        if(e.getMessage().equals("不允许访问")){
            return new RestErrorResponse("对不起，您没有权限访问");
        }

        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());

    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        // 存放错误信息
        List<String> errors = new ArrayList<>();

        bindingResult.getFieldErrors().stream().forEach(item->{
            errors.add(item.getDefaultMessage());
        });

        String errMessage = StringUtils.join(errors, ",");
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);

        return restErrorResponse;
    }







}
