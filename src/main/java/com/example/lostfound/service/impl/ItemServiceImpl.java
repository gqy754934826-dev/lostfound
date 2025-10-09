package com.example.lostfound.service.impl;

import com.example.lostfound.mapper.ChatMapper;
import com.example.lostfound.mapper.ItemMapper;
import com.example.lostfound.mapper.UserMapper;
import com.example.lostfound.pojo.Item;
import com.example.lostfound.pojo.User;
import com.example.lostfound.pojo.dto.ItemDTO;
import com.example.lostfound.pojo.vo.ItemVO;
import com.example.lostfound.pojo.vo.PageResult;
import com.example.lostfound.pojo.vo.Result;
import com.example.lostfound.service.ItemService;
import com.example.lostfound.util.OssUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 失物招领信息服务实现类
 */
@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private OssUtil ossUtil;

    @Override
    public Result<String> publishItem(ItemDTO itemDTO, Long userId) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 创建信息
        Item item = new Item();
        BeanUtils.copyProperties(itemDTO, item);

        // 设置时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        item.setItemTime(LocalDateTime.parse(itemDTO.getItemTime(), formatter));

        // 设置状态和用户ID
        item.setStatus(0); // 0待审核，1已通过，2已拒绝，3已解决
        item.setUserId(userId);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        // 插入信息
        itemMapper.insert(item);

        return Result.success("发布成功，等待审核");
    }

    @Override
    public Result<String> uploadItemImage(MultipartFile file) {
        try {
            // 上传图片
            String imageUrl = ossUtil.uploadItemImage(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            log.error("上传图片失败", e);
            return Result.error("上传图片失败");
        }
    }

    @Override
    public Result<PageResult<ItemVO>> getItemList(int pageNum, int pageSize, Integer status, String type, String title, String location) {
        // 如果前端没有指定状态，则只查询已通过和已完成的信息（状态为1和3）
        // 用户端不应该看到待审核(0)和已拒绝(2)的信息
        List<Item> itemList;
        PageInfo<Item> pageInfo;
        
        if (status == null) {
            // 分页查询，使用自定义条件
            PageHelper.startPage(pageNum, pageSize);
            // 只查询状态为1(已通过)或3(已完成)的信息
            itemList = itemMapper.selectListWithStatusIn(new Integer[]{1, 3}, type, title, location);
            pageInfo = new PageInfo<>(itemList);
        } else {
            // 分页查询，支持所有状态的查询（包括待审核和已拒绝）
            PageHelper.startPage(pageNum, pageSize);
            itemList = itemMapper.selectList(status, type, title, location);
            pageInfo = new PageInfo<>(itemList);
        }
        
        // 转换为ItemVO列表
        List<ItemVO> itemVOList = itemList.stream().map(item -> {
            ItemVO itemVO = new ItemVO();
            BeanUtils.copyProperties(item, itemVO);
            
            // 获取用户名
            User user = userMapper.selectById(item.getUserId());
            if (user != null) {
                itemVO.setUsername(user.getUsername());
            }
            
            return itemVO;
        }).toList();
        
        // 封装为PageResult
        PageResult<ItemVO> pageResult = new PageResult<>();
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setList(itemVOList);
        pageResult.setPageNum(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());

        return Result.success(pageResult);
    }

    @Override
    public Result<ItemVO> getItemDetail(Long itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return Result.error("信息不存在");
        }
        
        // 转换为ItemVO并设置用户名
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(item, itemVO);
        
        // 获取用户名
        if (item.getUserId() != null) {
            User user = userMapper.selectById(item.getUserId());
            if (user != null) {
                itemVO.setUsername(user.getUsername());
            }
        }

        return Result.success(itemVO);
    }

    @Override
    public Result<PageInfo<Item>> getUserItemList(Long userId, int pageNum, int pageSize, String title, String location, String type, Integer status) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Item> itemList = itemMapper.selectByUserId(userId, title, location, type, status);
        PageInfo<Item> pageInfo = new PageInfo<>(itemList);

        return Result.success(pageInfo);
    }

    @Override
    public Result<String> updateItemStatus(Long itemId, Integer status, Long userId, String role) {
        // 查询信息
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return Result.error("信息不存在");
        }
        
        // 验证是否为信息发布者或管理员
        boolean isAdmin = "admin".equals(role);
        
        if (!isAdmin && !item.getUserId().equals(userId)) {
            log.error("更新信息状态失败：用户{}不是信息{}的发布者且不是管理员", userId, itemId);
            return Result.error("您不是该信息的发布者，无权操作");
        }

        // 更新状态
        itemMapper.updateStatus(itemId, status);

        return Result.success("操作成功");
    }

    @Override
    public Result<PageInfo<Item>> getPendingItemList(int pageNum, int pageSize) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Item> itemList = itemMapper.selectPendingList();
        PageInfo<Item> pageInfo = new PageInfo<>(itemList);

        return Result.success(pageInfo);
    }

    @Override
    public Result<Map<String, Object>> getUserDashboard(Long userId) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 统计数据
        int totalCount = itemMapper.countByUserId(userId);
        int pendingCount = itemMapper.countPendingByUserId(userId);
        int resolvedCount = itemMapper.countResolvedByUserId(userId);
        int unreadCount = chatMapper.countUnreadMessage(userId);

        // 封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("pendingCount", pendingCount);
        result.put("resolvedCount", resolvedCount);
        result.put("unreadCount", unreadCount);

        return Result.success(result);
    }

    @Override
    public Result<Map<String, Object>> getAdminDashboard() {
        // 统计数据
        List<User> userList = userMapper.selectList();
        int totalUserCount = userList.size();
        int bannedUserCount = (int) userList.stream().filter(user -> user.getStatus() == 1).count();
        int pendingItemCount = itemMapper.countPending();
        int todayNewItemCount = itemMapper.countTodayNew();

        // 封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("totalUserCount", totalUserCount);
        result.put("bannedUserCount", bannedUserCount);
        result.put("pendingItemCount", pendingItemCount);
        result.put("todayNewItemCount", todayNewItemCount);

        return Result.success(result);
    }
    
    @Override
    public Result<List<Map<String, Object>>> getItemTypeStats() {
        List<Map<String, Object>> stats = itemMapper.countByType();
        // 转换数据格式，将type和count转换为name和value
        List<Map<String, Object>> formattedStats = new ArrayList<>();
        for (Map<String, Object> stat : stats) {
            Map<String, Object> formattedStat = new HashMap<>();
            // 将英文类型转换为中文名称
            String type = (String) stat.get("type");
            String typeName;
            switch (type) {
                case "lost":
                    typeName = "失物";
                    break;
                case "claim":
                    typeName = "招领";
                    break;
                default:
                    typeName = type; // 如果是其他类型，保持原样
                    break;
            }
            formattedStat.put("name", typeName);
            Object countObj = stat.get("count");
            Integer count = countObj instanceof Long ? ((Long) countObj).intValue() : (Integer) countObj;
            formattedStat.put("value", count);
            formattedStats.add(formattedStat);
        }
        return Result.success(formattedStats);
    }
    
    @Override
    public Result<List<Map<String, Object>>> getItemStatusStats() {
        List<Map<String, Object>> stats = itemMapper.countByStatus();
        // 转换数据格式，将status和count转换为name和value
        List<Map<String, Object>> formattedStats = new ArrayList<>();
        for (Map<String, Object> stat : stats) {
            Map<String, Object> formattedStat = new HashMap<>();
            // 将状态数字转换为状态名称
            Object statusObj = stat.get("status");
            Integer status = statusObj instanceof Long ? ((Long) statusObj).intValue() : (Integer) statusObj;
            String statusName;
            switch (status) {
                case 0:
                    statusName = "待审核";
                    break;
                case 1:
                    statusName = "已通过";
                    break;
                case 2:
                    statusName = "已拒绝";
                    break;
                case 3:
                    statusName = "已完成";
                    break;
                default:
                    statusName = "未知";
                    break;
            }
            formattedStat.put("name", statusName);
            Object countObj = stat.get("count");
            Integer count = countObj instanceof Long ? ((Long) countObj).intValue() : (Integer) countObj;
            formattedStat.put("value", count);
            formattedStats.add(formattedStat);
        }
        return Result.success(formattedStats);
    }
    
    @Override
    public Result<List<Map<String, Object>>> getItemDailyStats() {
        List<Map<String, Object>> stats = itemMapper.countByDay();
        // 确保返回的数据格式正确，包含day和count字段
        List<Map<String, Object>> formattedStats = new ArrayList<>();
        for (Map<String, Object> stat : stats) {
            Map<String, Object> formattedStat = new HashMap<>();
            // 确保day字段存在且为字符串
            Object dayObj = stat.get("day");
            String day = dayObj != null ? dayObj.toString() : "";
            formattedStat.put("day", day);
            
            // 确保count字段存在且为数字
            Object countObj = stat.get("count");
            Integer count = 0;
            if (countObj != null) {
                try {
                    count = Integer.valueOf(countObj.toString());
                } catch (NumberFormatException e) {
                    // 转换失败时使用默认值0
                }
            }
            formattedStat.put("count", count);
            
            formattedStats.add(formattedStat);
        }
        return Result.success(formattedStats);
    }
    
    @Override
    public Result<List<Map<String, Object>>> getUserItemTypeStats(Long userId) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        List<Map<String, Object>> stats = itemMapper.countUserItemByType(userId);
        // 转换数据格式，将type和count转换为name和value
        List<Map<String, Object>> formattedStats = new ArrayList<>();
        for (Map<String, Object> stat : stats) {
            Map<String, Object> formattedStat = new HashMap<>();
            // 将英文类型转换为中文名称
            String type = (String) stat.get("type");
            String typeName;
            switch (type) {
                case "lost":
                    typeName = "失物";
                    break;
                case "claim":
                    typeName = "招领";
                    break;
                default:
                    typeName = type; // 如果是其他类型，保持原样
                    break;
            }
            formattedStat.put("name", typeName);
            Object countObj = stat.get("count");
            Integer count = countObj instanceof Long ? ((Long) countObj).intValue() : (Integer) countObj;
            formattedStat.put("value", count);
            formattedStats.add(formattedStat);
        }
        return Result.success(formattedStats);
    }
    
    @Override
    public Result<List<Map<String, Object>>> getUserItemDailyStats(Long userId) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        List<Map<String, Object>> stats = itemMapper.countUserItemByDay(userId);
        // 确保返回的数据格式正确，包含day和count字段
        List<Map<String, Object>> formattedStats = new ArrayList<>();
        for (Map<String, Object> stat : stats) {
            Map<String, Object> formattedStat = new HashMap<>();
            // 确保day字段存在且为字符串
            Object dayObj = stat.get("day");
            String day = dayObj != null ? dayObj.toString() : "";
            formattedStat.put("day", day);
            
            // 确保count字段存在且为数字
            Object countObj = stat.get("count");
            Integer count = 0;
            if (countObj != null) {
                try {
                    count = Integer.valueOf(countObj.toString());
                } catch (NumberFormatException e) {
                    // 转换失败时使用默认值0
                }
            }
            formattedStat.put("count", count);
            
            formattedStats.add(formattedStat);
        }
        return Result.success(formattedStats);
    }
    
    @Override
    public Result<String> deleteItem(Long itemId, Long userId, String role) {
        // 查询信息
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return Result.error("信息不存在");
        }
        
        // 权限检查：管理员可以删除任何信息，普通用户只能删除自己的信息
        if (!"admin".equals(role) && !item.getUserId().equals(userId)) {
            return Result.error("无权限删除该信息");
        }
        
        // 删除信息
        int rows = itemMapper.deleteById(itemId);
        if (rows > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @Override
    public Result<String> updateItem(ItemDTO itemDTO, Long userId, String role) {
        // 查询信息
        Item item = itemMapper.selectById(itemDTO.getId());
        if (item == null) {
            return Result.error("信息不存在");
        }
        
        // 检查权限
        if ("admin".equals(role)) {
            // 管理员可以更新任何信息
        } else if (item.getUserId().equals(userId)) {
            // 用户只能更新自己的信息
        } else {
            return Result.error("无权限更新该信息");
        }
        
        // 更新信息
        Item updateItem = new Item();
        // 手动复制属性，避免类型转换问题
        updateItem.setId(itemDTO.getId());
        updateItem.setTitle(itemDTO.getTitle());
        updateItem.setDescription(itemDTO.getDescription());
        updateItem.setType(itemDTO.getType());
        updateItem.setLocation(itemDTO.getLocation());
        updateItem.setImageUrl(itemDTO.getImageUrl());
        
        // 处理时间转换
        if (itemDTO.getItemTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            updateItem.setItemTime(LocalDateTime.parse(itemDTO.getItemTime(), formatter));
        }
        
        updateItem.setUpdateTime(LocalDateTime.now());
        
        // 如果是管理员，可以修改状态
        if ("admin".equals(role) && itemDTO.getStatus() != null) {
            updateItem.setStatus(itemDTO.getStatus());
        } else {
            // 如果是普通用户修改信息，状态变为待审核
            updateItem.setStatus(0); // 0待审核，1已通过，2已拒绝，3已解决
        }
        
        // 更新信息
        int result = itemMapper.update(updateItem);
        if (result > 0) {
            if ("admin".equals(role)) {
                return Result.success("更新成功");
            } else {
                return Result.success("更新成功，等待审核");
            }
        } else {
            return Result.error("更新失败");
        }
    }
    
    @Override
    public Result<String> completeItem(Long itemId, Long userId, String role) {
        // 查询信息
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return Result.error("信息不存在");
        }
        
        // 检查权限
        if ("admin".equals(role)) {
            // 管理员可以标记任何信息为已完成
        } else if (item.getUserId().equals(userId)) {
            // 用户只能标记自己的信息为已完成
        } else {
            return Result.error("无权限标记该信息为已完成");
        }
        
        // 更新信息状态为已完成
        Item updateItem = new Item();
        updateItem.setId(itemId);
        updateItem.setStatus(3); // 3表示已完成
        updateItem.setUpdateTime(LocalDateTime.now());
        
        // 更新信息
        int result = itemMapper.update(updateItem);
        if (result > 0) {
            return Result.success("标记完成成功");
        } else {
            return Result.error("标记完成失败");
        }
    }
    
    @Override
    public Result<List<Map<String, Object>>> getUserItemStatusStats(Long userId) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        List<Map<String, Object>> stats = itemMapper.countUserItemByStatus(userId);
        // 转换数据格式，将status和count转换为name和value
        List<Map<String, Object>> formattedStats = new ArrayList<>();
        for (Map<String, Object> stat : stats) {
            Map<String, Object> formattedStat = new HashMap<>();
            // 将状态码转换为状态名称
            Object statusObj = stat.get("status");
            Integer status = statusObj instanceof Long ? ((Long) statusObj).intValue() : (Integer) statusObj;
            String statusName;
            switch (status) {
                case 0:
                    statusName = "待审核";
                    break;
                case 1:
                    statusName = "已通过";
                    break;
                case 2:
                    statusName = "已拒绝";
                    break;
                case 3:
                    statusName = "已解决";
                    break;
                default:
                    statusName = "未知状态";
                    break;
            }
            formattedStat.put("name", statusName);
            Object countObj = stat.get("count");
            Integer count = countObj instanceof Long ? ((Long) countObj).intValue() : (Integer) countObj;
            formattedStat.put("value", count);
            formattedStats.add(formattedStat);
        }
        return Result.success(formattedStats);
    }
}