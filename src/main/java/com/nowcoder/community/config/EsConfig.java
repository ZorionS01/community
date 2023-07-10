package com.nowcoder.community.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

/**
 * @Author Szw 2001
 * @Date 2023/6/26 12:17
 * @Slogn 致未来的你！
 */
//@Configuration
public class EsConfig {

    @Value("${spring.elasticsearch.uris}")
    private String url;

    @Bean
    RestHighLevelClient client(){
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(url)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
