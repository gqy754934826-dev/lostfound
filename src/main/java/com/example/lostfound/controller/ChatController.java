package com.example.lostfound.controller;

import com.example.lostfound.pojo.Chat;
import com.example.lostfound.pojo.dto.ChatDTO;
import com.example.lostfound.pojo.vo.Result;
import com.example.lostfound.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 聊天消息控制器
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 发送消息
     *
     * @param chatDTO 消息
     * @param request 请求
     * @return 结果
     */
    @PostMapping("/send")
    public Result<String> sendMessage(@RequestBody @Valid ChatDTO chatDTO, HttpServletRequest request) {
        Long fromUserId = Long.valueOf(request.getAttribute("userId").toString());
        log.info("发送消息：fromUserId={}, toUserId={}", fromUserId, chatDTO.getToUser());
        return chatService.sendMessage(chatDTO, fromUserId);
    }

    /**
     * 获取聊天记录
     *
     * @param otherUserId 对方用户ID
     * @param request     请求
     * @return 结果
     */
    @GetMapping("/history/{otherUserId}")
    public Result<List<Chat>> getChatHistory(@PathVariable("otherUserId") Long otherUserId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        return chatService.getChatHistory(userId, otherUserId);
    }

    /**
     * 获取聊天用户列表（包含最后一条消息和未读消息数量）
     *
     * @param request 请求
     * @return 结果
     */
    @GetMapping("/user/list/detail")
    public Result<List<Map<String, Object>>> getChatUserListWithLastMessageAndUnreadCount(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        return chatService.getChatUserListWithLastMessageAndUnreadCount(userId);
    }
    
    /**
     * 删除联系人
     *
     * @param contactUserId 联系人用户ID
     * @param request       请求
     * @return 结果
     */
    @DeleteMapping("/contact/{contactUserId}")
    public Result<String> deleteContact(@PathVariable("contactUserId") Long contactUserId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        log.info("删除联系人：userId={}, contactUserId={}", userId, contactUserId);
        return chatService.deleteContact(userId, contactUserId);
    }
    
    /**
     * 标记消息为已读
     *
     * @param fromUserId 发送方用户ID
     * @param request    请求
     * @return 结果
     */
    @PostMapping("/read/{fromUserId}")
    public Result<String> markMessageAsRead(@PathVariable("fromUserId") Long fromUserId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        log.info("标记消息为已读：userId={}, fromUserId={}", userId, fromUserId);
        return chatService.markMessageAsRead(userId, fromUserId);
    }
}