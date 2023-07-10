package com.nowcoder.community.entity;



import lombok.*;

import java.util.Date;

/**
 * @Author Szw 2001
 * @Date 2023/5/28 13:07
 * @Slogn 致未来的你！
 */
//@Data注解不要有继承关系 有继承关系则 子类加@EqualsAndHashCode(callSuper = true) 否则 hash equals 不会加上父类的参数
// 或者杜绝使用@Data，而用@Getter,@Setter,@ToString代替它。
    @Getter
    @Setter
public class User {

    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;

    public User(){

    }

    public User(String username, int type, Date createTime) {
        this.username = username;
        this.type = type;
        this.createTime = createTime;
    }
}
