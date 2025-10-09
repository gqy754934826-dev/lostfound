package com.example.lostfound.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 当前页数据
     */
    private List<T> list;
    
    /**
     * 当前页码
     */
    private int pageNum;
    
    /**
     * 每页大小
     */
    private int pageSize;
}