package com.example.lostfound.service;

import com.example.lostfound.pojo.vo.ItemVO;
import com.example.lostfound.pojo.vo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 缓存服务接口
 */
public interface CacheService {
    
    /**
     * 缓存信息列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param status   状态
     * @param type     类型
     * @param title    标题
     * @param location 地点
     * @param pageResult 分页结果
     */
    void cacheItemList(int pageNum, int pageSize, Integer status, String type, 
                      String title, String location, PageResult<ItemVO> pageResult);
    
    /**
     * 获取缓存的信息列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param status   状态
     * @param type     类型
     * @param title    标题
     * @param location 地点
     * @return 分页结果
     */
    PageResult<ItemVO> getCachedItemList(int pageNum, int pageSize, Integer status, 
                                        String type, String title, String location);
    
    /**
     * 缓存信息详情
     *
     * @param itemId 信息ID
     * @param itemVO 信息详情
     */
    void cacheItemDetail(Long itemId, ItemVO itemVO);
    
    /**
     * 获取缓存的信息详情
     *
     * @param itemId 信息ID
     * @return 信息详情
     */
    ItemVO getCachedItemDetail(Long itemId);
    
    /**
     * 清除信息相关的缓存
     *
     * @param itemId 信息ID（可选，为null时清除所有信息缓存）
     */
    void clearItemCache(Long itemId);
    
    /**
     * 清除信息列表缓存
     */
    void clearItemListCache();

    /**
     * 更新缓存中的信息状态
     * 
     * @param itemId 信息ID
     * @param newStatus 新状态
     */
    void updateItemStatusInCache(Long itemId, Integer newStatus);
    
    /**
     * 更新缓存中的信息详情
     * 
     * @param itemId 信息ID
     * @param itemVO 更新后的信息详情
     */
    void updateItemDetailInCache(Long itemId, ItemVO itemVO);
    
    /**
     * 缓存用户仪表盘数据
     * 
     * @param userId 用户ID
     * @param dashboardData 仪表盘数据
     */
    void cacheUserDashboard(Long userId, Map<String, Object> dashboardData);
    
    /**
     * 获取缓存的用户仪表盘数据
     * 
     * @param userId 用户ID
     * @return 仪表盘数据
     */
    Map<String, Object> getCachedUserDashboard(Long userId);
    
    /**
     * 清除用户仪表盘缓存
     * 
     * @param userId 用户ID
     */
    void clearUserDashboardCache(Long userId);
    
    /**
     * 缓存全局信息状态统计
     * 
     * @param stats 统计数据
     */
    void cacheItemStatusStats(List<Map<String, Object>> stats);
    
    /**
     * 获取缓存的全局信息状态统计
     * 
     * @return 统计数据
     */
    List<Map<String, Object>> getCachedItemStatusStats();
    
    /**
     * 清除全局信息状态统计缓存
     */
    void clearItemStatusStatsCache();
}