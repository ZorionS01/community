package com.nowcoder.community.entity;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author Szw 2001
 * @Date 2023/6/4 15:27
 * @Slogn 致未来的你！
 */
//@Data注解不要有继承关系 有继承关系则 子类加@EqualsAndHashCode(callSuper = true) 否则 hash equals 不会加上父类的参数
// 或者杜绝使用@Data，而用@Getter,@Setter,@ToString代替它。
@Getter
@Setter
@ToString
public class LoginTicket {

    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;


}
