package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author Szw 2001
 * @Date 2023/6/17 16:30
 * @Slogn 致未来的你！
 */
/*@Component
@Aspect //切面组件*/
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    //第一个* 表示返回值类型不限
    //包名service下的(*)任何类(*)任何方法 (..)任何参数
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){

    }
//    @Before("execution(* com.nowcoder.community.service.*.*(..))") 这样写也可以
    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        //用户[1.2.3.4],在xx时间，访问了[com.nowcoder.community.service.xxx()]
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();//工具类
        //特殊调用 不是controller调用service 消费者调用service
        if (attributes == null){
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //joinPoint.getSignature().getDeclaringTypeName() 目标类名
        //joinPoint.getSignature().getName() 目标方法名
        String target = joinPoint.getSignature().getDeclaringTypeName() + "."+ joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].",ip,now,target));
    }
}
