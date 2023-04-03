package com.xuecheng.exception;

import java.io.Serializable;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.exception
 * @className: RestErrorResponse
 * @author: Caixi
 * @description: 和前端约定返回的异常信息
 * @date: 2023/4/1 11:25
 * @version: 1.0
 */
public class RestErrorResponse implements Serializable {

        private String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }
}
