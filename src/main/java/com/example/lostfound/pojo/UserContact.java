package com.example.lostfound.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户联系人实体类
 */
@Data
public class UserContact {
    /**
     * 联系人关系ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 联系人用户ID
     */
    private Long contactUserId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}