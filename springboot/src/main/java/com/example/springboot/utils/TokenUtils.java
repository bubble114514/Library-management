package com.example.springboot.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.springboot.entity.Admin;
import com.example.springboot.service.IAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.PostConstruct; // 修改导入路径
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest; // 修改导入路径
import java.util.Date;

@Component
@Slf4j
public class TokenUtils {

    private static IAdminService staticAdminService;

    @Resource
    private IAdminService adminService;

    @PostConstruct
    public void setUserService() {
        staticAdminService = adminService;
    }

    /**
     * 生成token
     *
     * @return
     */
    public static String genToken(String adminId, String sign) {
        return JWT.create().withAudience(adminId) // 将 user id 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 5)) // 2小时后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }

    public static String genToken(String adminId, String sign,int days) {
        return JWT.create().withAudience(adminId) // 将 user id 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetDay(new Date(), days)) // 2小时后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return user对象
     *  /admin?token=xxxx
     */
    public static Admin getCurrentAdmin() {
        String token = null;
        try {//获取当前请求的属性
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                log.error("获取当前请求属性失败");
                return null;
            }
            HttpServletRequest request = requestAttributes.getRequest();
            //从请求头中获取Token
            token = request.getHeader("token");
            if (StrUtil.isBlank(token)) {
                //如果请求头中没有Token，则从请求参数中获取Token
                token = request.getParameter("token");
            }
            if (StrUtil.isBlank(token)) {
                log.error("获取当前登录的token失败， token: {}", token);
                return null;
            }
            //解析Token，获取管理员ID
            String adminId = JWT.decode(token).getAudience().get(0);
            //根据管理员ID从数据库中获取管理员信息并返回
            return staticAdminService.getById(Integer.valueOf(adminId));
        } catch (Exception e) {
            log.error("获取当前登录的管理员信息失败, token={}", token, e);
            return null;
        }
    }
}
