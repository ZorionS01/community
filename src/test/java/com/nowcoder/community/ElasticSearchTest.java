package com.nowcoder.community;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * @Author Szw 2001
 * @Date 2023/6/25 19:41
 * @Slogn 致未来的你！
 */
@SpringBootTest
public class ElasticSearchTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    //使用springboot整合ES的专用客户端接口ElasticsearchRestTemplate来进行操作
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private RestHighLevelClient restHighLevelClient;


    //判断某id的文档是否存在
    @Test
    public void testExist(){
        boolean exists = discussPostRepository.existsById(101);
        System.out.println(exists);
    }


    //一次保存一条数据
    @Test
    public void testInsert(){

        discussPostRepository.save(discussPostMapper.selectDiscussPostById(110));
    }

    //保存多条数据
   /* @Test
    public void testInsertList(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134,0,100));
    }*/

    //先覆盖原内容,来修改一条数据
    @Test
    public void testUpdate(){
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(231);
        discussPost.setContent("我是新人，使劲灌水");
        discussPostRepository.save(discussPost);
    }

    // post /discusspost/_doc/109/_update
    @Test
    public void testUpdateDocument() throws IOException, ParseException {
        UpdateRequest request = new UpdateRequest("discusspost","109");
        request.timeout("1s");
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(230);
        discussPost.setContent("我是新人，使劲灌水");
        discussPost.setTitle(null);//es中的title不会变
        Date createTime = discussPost.getCreateTime();
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = isoDateFormat.format(createTime);
        Date date = isoDateFormat.parse(time);
        discussPost.setCreateTime(date);
        request.doc(JSON.toJSONString(discussPost), XContentType.JSON);
        UpdateResponse updateResponse = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println(updateResponse.status());
    }

    @Test
    public void testDelete(){
//        discussPostRepository.deleteById(231);
        discussPostRepository.deleteAll();
    }

    //不带高亮显示
    @Test
    public void noHighLightQueryByClient() throws IOException {
        SearchRequest searchRequest = new SearchRequest("discusspost");//索引名，表名

        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
        //在discusspost索引的title和content字段都查询"互联网"
        .query(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
        //matchQuery是模糊查询，会对key进行分词，searchSourceBuilder.query(QueryBuilders.matchQuery(key,value))
        //termQuery是精准查询,searchSourceBuilder.query(QueryBuilders.termQuery(key,value))
        .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
        .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
        .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
        //一个可选项,用于控制允许搜索的时间，searchSourceBuilder.timeout(new TimeValue(60),TimeUnit.SECONDS)
        //分页
        .from(0)//指定从哪条开始查
        .size(10);//指定查出的总记录条数

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("结果："+searchResponse);
        System.out.println(JSONObject.toJSONString(searchResponse));

        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit hit:searchResponse.getHits().getHits()){
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(),DiscussPost.class);
            System.out.println("对象："+discussPost);
            list.add(discussPost);

        }
    }

    /*@Test
    public void testSearchByTemplate(){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
        .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
        .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
        .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
        .withPageable(PageRequest.of(0,10))
        .withHighlightFields(new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>"))
                .build();

        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
    }*/

    @Test
    public void highlightQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost 是索引名 也就是表名

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置高亮字段
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        // 开启多个高亮
//        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color':red>");
        highlightBuilder.postTags("</span>");

        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
        .query(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
        .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
        .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
        .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
        .from(0)
        .size(10)
        .highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit hit:response.getHits().getHits()){
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(),DiscussPost.class);

            //处理高亮显示的结果
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null){
                discussPost.setTitle(titleField.getFragments()[0].toString());
            }
            HighlightField contentField = hit.getHighlightFields().get("content");
            if(contentField != null){
                discussPost.setContent(contentField.getFragments()[0].toString());
            }
            System.out.println(discussPost);
            list.add(discussPost);
        }
        list.forEach(System.out::println);

    }
}
