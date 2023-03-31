package com.xuecheng.model;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PageParams {
//    当前页码
    private Long pageNo = 1L;

    // 每页记录数量
    private Long pageSize = 30L;

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PageParams() {
    }
}
