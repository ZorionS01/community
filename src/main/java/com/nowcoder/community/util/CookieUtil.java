package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @Author Szw 2001
 * @Date 2023/6/5 20:02
 * @Slogn 致未来的你！
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request,String name){
        if (request == null || name == null){
            throw new IllegalArgumentException("参数为空！");
        }

        /*Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }*/
        if (request.getCookies() != null){
            return Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(name)).map(Cookie::getValue).findFirst().orElse(null);
        }
        //Java8 stream流改写上面代码
        return null;

    }
}
