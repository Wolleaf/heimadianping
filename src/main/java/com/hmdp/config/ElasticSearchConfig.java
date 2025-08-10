package com.hmdp.config;

import com.hmdp.property.ElasticSearchProperties;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(ElasticSearchProperties elasticSearchProperties) {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(elasticSearchProperties.getUris(), 9200, "http"))
        );
    }

    @Bean(name = "elasticsearchTemplate")
    public ElasticsearchRestTemplate elasticsearchTemplate(RestHighLevelClient restHighLevelClient) {
        return new ElasticsearchRestTemplate(restHighLevelClient);
    }
}
