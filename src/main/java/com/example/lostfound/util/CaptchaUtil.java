package com.example.lostfound.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码工具类
 */
public class CaptchaUtil {
    
    private static final char[] CHARS = {'2', '3', '4', '5', '6', '7', '8', '9',
                                   'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
                                   'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                                   'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm',
                                   'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    
    private static final int SIZE = 4;
    private static final int LINES = 5;
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    
    /**
     * 生成验证码图片和文本
     *
     * @return 验证码结果对象
     */
    public static CaptchaResult generateCaptcha() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        
        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 设置边框
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
        
        // 生成随机验证码
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            char c = CHARS[random.nextInt(CHARS.length)];
            captcha.append(c);
        }
        
        // 绘制验证码
        Font font = new Font("Arial", Font.BOLD, 24);
        g.setFont(font);
        for (int i = 0; i < captcha.length(); i++) {
            g.setColor(getRandomColor());
            g.drawString(String.valueOf(captcha.charAt(i)), 20 + i * 20, 28);
        }
        
        // 绘制干扰线
        for (int i = 0; i < LINES; i++) {
            g.setColor(getRandomColor());
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }
        
        g.dispose();
        
        // 转换为Base64字符串
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            
            return new CaptchaResult(captcha.toString(), "data:image/png;base64," + base64Image);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 生成随机颜色
     *
     * @return 随机颜色
     */
    private static Color getRandomColor() {
        Random random = new Random();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
    
    /**
     * 验证码结果类
     */
    public static class CaptchaResult {
        private String text;
        private String image;
        
        public CaptchaResult(String text, String image) {
            this.text = text;
            this.image = image;
        }
        
        public String getText() {
            return text;
        }
        
        public String getImage() {
            return image;
        }
    }
}