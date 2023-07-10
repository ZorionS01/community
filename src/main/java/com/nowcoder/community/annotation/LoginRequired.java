package com.nowcoder.community.annotation;

import java.lang.annotation.*;

/**
 * @Author Szw 2001
 * @Date 2023/6/11 13:30
 * @Slogn 致未来的你！
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
