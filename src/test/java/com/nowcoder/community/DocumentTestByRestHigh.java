package com.nowcoder.community;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nowcoder.community.entity.User;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author Szw 2001
 * @Date 2023/6/26 19:35
 * @Slogn 致未来的你！
 */
@SpringBootTest
public class DocumentTestByRestHigh {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 添加文档
     **/
    @Test
    public void addDocument() throws IOException {
        //创建对象
        User user = new User("test",18,new Date());
        //创建请求
        IndexRequest indexRequest = new IndexRequest("es_index");
        //规则 put /es_index/_doc/1
        indexRequest.id("1");
        //将我们的数据放入请求json
        String jsonUser = JSON.toJSONString(user);
        indexRequest.source(jsonUser, XContentType.JSON);
        //客户端发送请求
        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("index:"+index);
        System.out.println("indexStatus:"+index.status());
    }

    /**
     * 获取文档
     **/
    @Test
    public void getDocument() throws IOException {
        //get /es_index/_doc/1
        GetRequest request = new GetRequest("es_index","1");
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);

        System.out.println("getResponse.getSourceAsString:"+response.getSourceAsString());
        System.out.println("getResponse:"+response);
    }

    /**
     * 更新文档
     **/
    @Test
    public void updateDocument() throws IOException {
        //post /es_index/_doc/1/_update
        UpdateRequest request = new UpdateRequest("es_index","1");
        //修改内容
        User user = new User("test2",20,new Date());
        request.doc(JSONObject.toJSONString(user),XContentType.JSON);
        UpdateResponse updateResponse = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println("updateResponse:"+updateResponse);

    }

    /**
     * 删除文档
     **/
    @Test
    public void deleteDocument() throws IOException {
        //DELETE /es_index/_doc/1
        DeleteRequest deleteRequest = new DeleteRequest("es_index","1");
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("deleteResponse:"+deleteResponse);
    }

    /**
     * 批量删除
     **/
    @Test
    public void bulk() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        List<User> userList = new ArrayList<>();
        userList.add(new User("h1",1,new Date()));
        userList.add(new User("h2",2,new Date()));
        userList.add(new User("h3",3,new Date()));
        userList.add(new User("h4",4,new Date()));
        userList.add(new User("h5",5,new Date()));
        //批量处理
        int size = userList.size();
        for (int i = 0; i < size; i++){
            //如果是批量处理更新、删除这里就可以
            bulkRequest.add(new IndexRequest("es_index",""+i+1)
            .source(JSONObject.toJSONString(userList.get(i)),XContentType.JSON));
        }
        restHighLevelClient.bulk(bulkRequest,RequestOptions.DEFAULT);
    }

    /**
     *查询文档操作
     * 1.searchRequest 搜索请求
     * 2.sourceBuilder 条件构造器
     * 3.TermQueryBuilder精确查询
     * 4.MatchAllQueryBuilder查询所有
     **/
    @Test
    public void search1(){
        SearchRequest searchRequest = new SearchRequest("es_index");
        //使用条件构造
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询条件，使用QueryBuilders工具类,来实现
        //QueryBuilders.temQuery精确查找
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("username","h1");

        //QueryBuilders.matchAllQuery 匹配所有
        //MatchAllQueryBuilder all = QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(queryBuilder);
    }




}
