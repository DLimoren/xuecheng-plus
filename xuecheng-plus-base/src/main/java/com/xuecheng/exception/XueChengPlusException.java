package com.xuecheng.exception;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.exception
 * @className: XueChengPlusException
 * @author: Caixi
 * @description: 本项目自定义异常类型
 * @date: 2023/4/1 11:27
 * @version: 1.0
 */
public class XueChengPlusException extends RuntimeException{

    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String message) {
        super(message);
        this.errMessage = message;
    }

    public static void cast(String message){
        throw new XueChengPlusException(message);
    }

    public static void cast(CommonError error){
        throw new XueChengPlusException(error.getErrMessage());
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
