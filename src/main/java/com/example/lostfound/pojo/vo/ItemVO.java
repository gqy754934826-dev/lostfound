package com.example.lostfound.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 失物/招领信息视图对象
 */
@Data
public class ItemVO {
    /**
     * 信息ID
     */
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 类型（lost/claim）
     */
    private String type;
    
    /**
     * 地点
     */
    private String location;
    
    /**
     * 时间
     */
    private LocalDateTime itemTime;
    
    /**
     * 图片（OSS）
     */
    private String imageUrl;
    
    /**
     * 状态（0待审核，1已通过，2已拒绝，3已解决）
     */
    private Integer status;
    
    /**
     * 发布人ID
     */
    private Long userId;
    
    /**
     * 发布人用户名
     */
    private String username;
    
    /**
     * 发布时间
     */
    private LocalDateTime createTime;
    
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}