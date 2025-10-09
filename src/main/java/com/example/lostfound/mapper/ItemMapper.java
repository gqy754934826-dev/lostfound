package com.example.lostfound.mapper;

import com.example.lostfound.pojo.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 失物招领信息Mapper接口
 */
@Mapper
public interface ItemMapper {
    /**
     * 根据ID查询信息
     *
     * @param id 信息ID
     * @return 信息
     */
    Item selectById(Long id);

    /**
     * 插入信息
     *
     * @param item 信息
     * @return 影响行数
     */
    int insert(Item item);

    /**
     * 更新信息
     *
     * @param item 信息
     * @return 影响行数
     */
    int update(Item item);

    /**
     * 更新信息状态
     *
     * @param id     信息ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 查询信息列表
     *
     * @param status   状态
     * @param type     类型
     * @param title    标题
     * @param location 地点
     * @return 信息列表
     */
    List<Item> selectList(@Param("status") Integer status, @Param("type") String type, 
                          @Param("title") String title, @Param("location") String location);
                          
    /**
     * 查询状态在指定列表中的信息列表
     *
     * @param statusList 状态列表
     * @param type       类型
     * @param title      标题
     * @param location   地点
     * @return 信息列表
     */
    List<Item> selectListWithStatusIn(@Param("statusList") Integer[] statusList, @Param("type") String type, 
                                    @Param("title") String title, @Param("location") String location);

    /**
     * 查询用户发布的信息列表
     *
     * @param userId   用户ID
     * @param title    标题
     * @param location 地点
     * @param type     类型
     * @param status   状态
     * @return 信息列表
     */
    List<Item> selectByUserId(@Param("userId") Long userId, 
                              @Param("title") String title, 
                              @Param("location") String location, 
                              @Param("type") String type, 
                              @Param("status") Integer status);

    /**
     * 查询待审核的信息列表
     *
     * @return 信息列表
     */
    List<Item> selectPendingList();

    /**
     * 统计用户发布的信息数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    int countByUserId(Long userId);

    /**
     * 统计用户发布的待审核信息数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    int countPendingByUserId(Long userId);

    /**
     * 统计用户发布的已解决信息数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    int countResolvedByUserId(Long userId);

    /**
     * 统计待审核信息数量
     *
     * @return 数量
     */
    int countPending();

    /**
     * 统计今日新增信息数量
     *
     * @return 数量
     */
    int countTodayNew();
    
    /**
     * 根据ID删除信息
     *
     * @param id 信息ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 统计各类型信息数量
     *
     * @return 各类型信息数量列表
     */
    List<java.util.Map<String, Object>> countByType();
    
    /**
     * 统计各状态信息数量
     *
     * @return 各状态信息数量列表
     */
    List<java.util.Map<String, Object>> countByStatus();
    
    /**
     * 统计最近7天每天的信息发布数量
     *
     * @return 最近7天每天的信息发布数量列表
     */
    List<java.util.Map<String, Object>> countByDay();
    
    /**
     * 统计用户发布的各类型信息数量
     *
     * @param userId 用户ID
     * @return 统计结果
     */
    List<Map<String, Object>> countUserItemByType(Long userId);
    
    /**
     * 统计用户发布的每日信息数量
     *
     * @param userId 用户ID
     * @return 统计结果
     */
    List<Map<String, Object>> countUserItemByDay(Long userId);
    
    /**
     * 统计用户发布的各状态信息数量
     *
     * @param userId 用户ID
     * @return 各状态信息数量
     */
    List<Map<String, Object>> countUserItemByStatus(@Param("userId") Long userId);
}