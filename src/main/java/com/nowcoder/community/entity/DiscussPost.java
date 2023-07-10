package com.nowcoder.community.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author Szw 2001
 * @Date 2023/5/28 16:50
 * @Slogn 致未来的你！
 */
//@Data注解不要有继承关系 有继承关系则 子类加@EqualsAndHashCode(callSuper = true) 否则 hash equals 不会加上父类的参数
// 或者杜绝使用@Data，而用@Getter,@Setter,@ToString代替它。
//1. callSuper = true，根据子类自身的字段值和从父类继承的字段值 来生成hashcode，当两个子类对象比较时，只有子类对象的本身的字段值和继承父类的字段值都相同，equals方法的返回值是true。
//2. callSuper = false，根据子类自身的字段值 来生成hashcode， 当两个子类对象比较时，只有子类对象的本身的字段值相同，父类字段值可以不同，equals方法的返回值是true

@Data
@Document(indexName = "discusspost" )
@Setting(shards = 6,replicas = 3)
//@EqualsAndHashCode(callSuper = true)
public class DiscussPost {

    @Id //表示主键
    private int id;

    @Field(type = FieldType.Integer)
    private int userId;

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Integer)
    private int type;

    @Field(type = FieldType.Integer)
    private int status;
//过时
//    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date)
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //无论你在实体类的 @DateTimeFormat 注解中指定了什么格式（例如"yyyy-MM-dd HH:mm:ss"），在 Elasticsearch 中存储的实际格式都是 "2019-04-04T03:53:36.000Z"（ISO 8601 格式）。
    private Date createTime;

    @Field(type = FieldType.Integer)
    private int commentCount;

    @Field(type = FieldType.Double)
    private double score;

}
