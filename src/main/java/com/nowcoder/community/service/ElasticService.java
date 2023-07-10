package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Szw 2001
 * @Date 2023/6/27 12:42
 * @Slogn 致未来的你！
 */
@Service
public class ElasticService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    public void saveDiscussPost(DiscussPost discussPost){
        discussPostRepository.save(discussPost);
    }

    public void deleteDiscussPostById(int id){
        discussPostRepository.deleteById(id);
    }

    public SearchPage<DiscussPost> searchDiscussPost(String keyword, int current, int limit){


        //构造搜索条件
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC),
                        SortBuilders.fieldSort("score").order(SortOrder.DESC),
                        SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current,limit))
                .withHighlightFields(new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>"))
                .build();
        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(build, DiscussPost.class);
        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(search, build.getPageable());



        if (!page.isEmpty()){
            for (SearchHit<DiscussPost> discussPostSearchPage : page){
                DiscussPost discussPost = discussPostSearchPage.getContent();
                //取高亮部分
                List<String> title = discussPostSearchPage.getHighlightFields().get("title");
                if (title!=null){
                    discussPost.setTitle(title.get(0));
                }

                List<String> content = discussPostSearchPage.getHighlightFields().get("content");
                if (content!=null){
                    discussPost.setContent(content.get(0));
                }
            }
        }
        return page;
    }
}
