package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.Hostholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author Szw 2001
 * @Date 2023/6/24 20:59
 * @Slogn 致未来的你！
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private Hostholder hostholder;

    @Autowired
    private MessageService messageService;


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostholder.getUser();
        if (user != null && modelAndView != null){
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
            modelAndView.addObject("allUnreadCount",letterUnreadCount + noticeUnreadCount);
        }

    }
}
