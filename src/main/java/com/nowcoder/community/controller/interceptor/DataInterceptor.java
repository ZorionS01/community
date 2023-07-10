package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DataService;
import com.nowcoder.community.util.Hostholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author Szw 2001
 * @Date 2023/7/2 20:11
 * @Slogn 致未来的你！
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private Hostholder hostholder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        dataService.recordUV(request.getRemoteUser());

        //统计DAU
        User user = hostholder.getUser();
        if (user != null){
            dataService.recordDAU(user.getId());
        }

        return true;
    }
}
