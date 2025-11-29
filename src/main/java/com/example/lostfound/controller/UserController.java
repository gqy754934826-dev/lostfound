package com.example.lostfound.controller;

import com.example.lostfound.pojo.User;
import com.example.lostfound.pojo.dto.UserLoginDTO;
import com.example.lostfound.pojo.dto.UserRegisterDTO;
import com.example.lostfound.pojo.vo.Result;
import com.example.lostfound.service.UserService;
import com.example.lostfound.util.CaptchaUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Base64;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 结果
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
        log.info("用户注册：{}", registerDTO.getUsername());
        return userService.register(registerDTO);
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid UserLoginDTO loginDTO) {
        log.info("用户登录：{}", loginDTO.getUsername());
        return userService.login(loginDTO);
    }

    /**
     * 获取用户信息
     *
     * @param request 请求
     * @return 结果
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        return userService.getUserInfo(userId);
    }
    
    /**
     * 根据用户ID获取用户信息（用于聊天功能）
     *
     * @param id 用户ID
     * @return 结果
     */
    @GetMapping("/info/{id}")
    public Result<User> getUserInfoById(@PathVariable("id") Long id) {
        return userService.getUserInfo(id);
    }

    /**
     * 更新用户信息
     *
     * @param user    用户信息
     * @param request 请求
     * @return 结果
     */
    @PutMapping("/info")
    public Result<String> updateUserInfo(@RequestBody User user, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        user.setId(userId);
        return userService.updateUserInfo(user);
    }

    /**
     * 更新用户头像
     *
     * @param file    头像文件
     * @param request 请求
     * @return 结果
     */
    @PostMapping("/avatar")
    public Result<String> updateAvatar(@RequestParam(value = "file", required = false) MultipartFile file, 
                                      HttpServletRequest request) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            // 再次尝试从request中获取文件
            if (request instanceof MultipartHttpServletRequest) {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                file = multipartRequest.getFile("file");
            }
            
            if (file == null || file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }
        }
        
        // 检查是否是已登录用户更新头像
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            Long userId = Long.valueOf(userIdObj.toString());
            return userService.updateAvatar(userId, file);
        } else {
            // 如果用户未登录，说明是注册时上传头像，直接上传文件并返回URL
            try {
                String avatarUrl = userService.uploadAvatar(file);
                return Result.success(avatarUrl);
            } catch (Exception e) {
                log.error("上传头像失败", e);
                return Result.error("上传头像失败: " + e.getMessage());
            }
        }
    }

    /**
     * 更新用户密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param request     请求
     * @return 结果
     */
    @PutMapping("/password")
    public Result<String> updatePassword(String oldPassword,
                                        String newPassword,
                                        HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        return userService.updatePassword(userId, oldPassword, newPassword);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        return userService.logout(token);
    }
    
    /**
     * 获取验证码
     *
     * @param session HttpSession
     * @return 验证码图片
     */
    @GetMapping("/captcha")
    public Result<String> getCaptcha(HttpSession session) {
        CaptchaUtil.CaptchaResult captchaResult = CaptchaUtil.generateCaptcha();
        if (captchaResult != null) {
            // 将验证码文本存储在session中
            session.setAttribute("captcha", captchaResult.getText());
            
            // 返回Base64编码的图片
            return Result.success(captchaResult.getImage());
        } else {
            return Result.error("验证码生成失败");
        }
    }
}