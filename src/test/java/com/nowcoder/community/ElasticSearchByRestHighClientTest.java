package com.nowcoder.community;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @Author Szw 2001
 * @Date 2023/6/26 19:18
 * @Slogn 致未来的你！
 */
@SpringBootTest
public class ElasticSearchByRestHighClientTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     *
     **/
    @Test
    public void createIndex() throws IOException {
        //1.创建索引
        CreateIndexRequest request = new CreateIndexRequest("user_index");
        //2.执行请求
        CreateIndexResponse response = restHighLevelClient.indices()
                .create(request, RequestOptions.DEFAULT);
    }

    /**
     * 判断索引是否存在
     **/
    @Test
    public void existIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("user_index");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println("exists:"+exists);
    }

    /**
     * 测试删除索引
     **/
    @Test
    public void deleteIndex() throws IOException {
        DeleteIndexRequest  request = new DeleteIndexRequest("user_index");
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println("delete:"+response);

    }



}
