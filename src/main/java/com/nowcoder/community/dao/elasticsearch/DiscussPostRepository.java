package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author Szw 2001
 * @Date 2023/6/25 19:31
 * @Slogn 致未来的你！
 */
/**
 *ElasticsearchRepository<DiscussPost,Integer>
 *DiscussPost：接口要处理的实体类
 * Integer：实体类中的主键是什么类型
 * ElasticsearchRepository：父接口，其中已经事先定义好了对es服务器访问的增删改查各种方法
 * Spring会给它自动做一个实现,我们直接调用即可
 **/

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
