package com.example.lostfound.mapper;

import com.example.lostfound.pojo.UserContact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户联系人Mapper接口
 */
@Mapper
public interface UserContactMapper {
    /**
     * 插入联系人关系
     *
     * @param userContact 联系人关系
     * @return 影响行数
     */
    int insert(UserContact userContact);
    
    /**
     * 删除联系人关系
     *
     * @param userId 用户ID
     * @param contactUserId 联系人用户ID
     * @return 影响行数
     */
    int delete(@Param("userId") Long userId, @Param("contactUserId") Long contactUserId);
    
    /**
     * 查询用户的联系人列表
     *
     * @param userId 用户ID
     * @return 联系人用户ID列表
     */
    List<Long> selectContactUserIds(Long userId);
    
    /**
     * 查询联系人关系是否存在
     *
     * @param userId 用户ID
     * @param contactUserId 联系人用户ID
     * @return 存在返回1，不存在返回0
     */
    int exists(@Param("userId") Long userId, @Param("contactUserId") Long contactUserId);
}