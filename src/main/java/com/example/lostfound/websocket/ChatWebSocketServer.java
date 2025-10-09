package com.example.lostfound.websocket;

import com.example.lostfound.service.ChatService;
import com.example.lostfound.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务器，处理实时聊天消息
 */
@ServerEndpoint("/ws/chat/{userId}")
@Component
@Slf4j
public class ChatWebSocketServer {

    // 用静态变量保存所有WebSocket连接
    private static final Map<Integer, ChatWebSocketServer> connections = new ConcurrentHashMap<>();
    
    // 用户ID
    private Integer userId;
    
    // 当前会话
    private Session session;
    
    // 由于@ServerEndpoint不支持自动注入，需要使用静态变量
    private static ChatService chatService;
    private static ObjectMapper objectMapper;
    
    @Autowired
    public void setChatService(ChatService chatService) {
        ChatWebSocketServer.chatService = chatService;
    }
    
    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        ChatWebSocketServer.objectMapper = objectMapper;
    }
    
    /**
     * 连接建立时调用
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) {
        this.session = session;
        this.userId = userId;
        
        // 将当前连接保存到Map中
        connections.put(userId, this);
        log.info("WebSocket连接已建立，用户ID: {}，当前连接数: {}", userId, connections.size());
    }
    
    /**
     * 连接关闭时调用
     */
    @OnClose
    public void onClose() {
        // 从Map中移除当前连接
        connections.remove(this.userId);
        log.info("WebSocket连接已关闭，用户ID: {}，当前连接数: {}", this.userId, connections.size());
    }
    
    /**
     * 收到消息时调用
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("收到来自用户 {} 的消息: {}", this.userId, message);
        
        try {
            // 解析消息
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);
            String type = (String) messageMap.get("type");
            
            // 处理不同类型的消息
            if ("HEARTBEAT".equals(type)) {
                // 心跳消息，回复心跳响应
                sendMessage(Map.of("type", "HEARTBEAT"));
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
        }
    }
    
    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误，用户ID: {}", this.userId, error);
    }
    
    /**
     * 发送消息给指定用户
     * @param userId 用户ID
     * @param message 消息内容
     * @return 是否发送成功
     */
    public static boolean sendMessageToUser(Integer userId, Object message) {
        ChatWebSocketServer server = connections.get(userId);
        if (server != null) {
            return server.sendMessage(message);
        }
        return false;
    }
    
    /**
     * 发送消息
     * @param message 消息内容
     * @return 是否发送成功
     */
    private boolean sendMessage(Object message) {
        try {
            String messageText = objectMapper.writeValueAsString(message);
            this.session.getBasicRemote().sendText(messageText);
            return true;
        } catch (IOException e) {
            log.error("发送WebSocket消息失败", e);
            return false;
        }
    }
    
    /**
     * 获取当前连接数
     */
    public static int getConnectionCount() {
        return connections.size();
    }
    
    /**
     * 检查用户是否在线
     */
    public static boolean isUserOnline(Integer userId) {
        return connections.containsKey(userId);
    }
}