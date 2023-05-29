package com.nowcoder.community.entity;



import lombok.Data;

import java.util.Date;

/**
 * @Author Szw 2001
 * @Date 2023/5/28 16:50
 * @Slogn 致未来的你！
 */
@Data
public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
}
