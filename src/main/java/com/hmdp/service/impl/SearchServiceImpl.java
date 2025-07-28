package com.hmdp.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.domain.doc.BlogDoc;
import com.hmdp.domain.doc.ShopDoc;
import com.hmdp.domain.doc.UserDoc;
import com.hmdp.domain.dto.search.BlogSearchDTO;
import com.hmdp.domain.dto.search.ShopSearchDTO;
import com.hmdp.domain.dto.search.UserSearchDTO;
import com.hmdp.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public List<BlogDoc> searchBlog(BlogSearchDTO blogSearchDTO) {
        // 构建复合查询参数
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (ObjectUtil.isNotNull(blogSearchDTO.getShopId())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("shopId", blogSearchDTO.getShopId()));
        }
        if (ObjectUtil.isNotNull(blogSearchDTO.getUserId())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", blogSearchDTO.getUserId()));
        }
        if (StrUtil.isNotBlank(blogSearchDTO.getName())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", blogSearchDTO.getName()));
        }
        if (ObjectUtil.isNotNull(blogSearchDTO.getIsLike())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("isLike", blogSearchDTO.getIsLike()));
        }
        if (StrUtil.isNotBlank(blogSearchDTO.getTitle())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", blogSearchDTO.getTitle()));
        }
        if (StrUtil.isNotBlank(blogSearchDTO.getContent())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("content", blogSearchDTO.getContent()));
        }
        if (ObjectUtil.isNotNull(blogSearchDTO.getLiked())) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("liked").gte(blogSearchDTO.getLiked()));
        }
        if (ObjectUtil.isNotNull(blogSearchDTO.getComments())) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("comments").gte(blogSearchDTO.getComments()));
        }
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        // 查询
        SearchHits<BlogDoc> searchHits = elasticsearchRestTemplate.search(query, BlogDoc.class);
        // 处理查询结果
        List<BlogDoc> blogDocList = searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        return blogDocList;
    }

    @Override
    public List<ShopDoc> searchShop(ShopSearchDTO shopSearchDTO) {
        // 构建复合查询参数
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StrUtil.isNotBlank(shopSearchDTO.getName())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", shopSearchDTO.getName()));
        }
        if (ObjectUtil.isNotNull(shopSearchDTO.getTypeId())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("typeId", shopSearchDTO.getTypeId()));
        }
        if (StrUtil.isNotBlank(shopSearchDTO.getArea())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("area", shopSearchDTO.getArea()));
        }
        if (StrUtil.isNotBlank(shopSearchDTO.getAddress())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("address", shopSearchDTO.getAddress()));
        }
        if (ObjectUtil.isNotNull(shopSearchDTO.getMinAvgPrice())) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("avgPrice").gte(shopSearchDTO.getMinAvgPrice()));
        }
        if (ObjectUtil.isNotNull(shopSearchDTO.getMaxAvgPrice())) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("avgPrice").lte(shopSearchDTO.getMaxAvgPrice()));
        }
        if (ObjectUtil.isNotNull(shopSearchDTO.getSold())) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("sold").gte(shopSearchDTO.getSold()));
        }
        if (ObjectUtil.isNotNull(shopSearchDTO.getComments())) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("comments").gte(shopSearchDTO.getComments()));
        }
        if (ObjectUtil.isNotNull(shopSearchDTO.getScore())) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("score").gte(shopSearchDTO.getScore()));
        }
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        // 查询
        SearchHits<ShopDoc> searchHits = elasticsearchRestTemplate.search(query, ShopDoc.class);
        // 处理查询结果
        List<ShopDoc> shopDocList = searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        return shopDocList;
    }

    @Override
    public List<UserDoc> searchUser(UserSearchDTO userSearchDTO) {
        // 构建复合查询参数
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StrUtil.isNotBlank(userSearchDTO.getNickName())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("nickName", userSearchDTO.getNickName()));
        }
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        // 查询
        SearchHits<UserDoc> searchHits = elasticsearchRestTemplate.search(query, UserDoc.class);
        // 处理查询结果
        List<UserDoc> userDocList = searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        return userDocList;
    }
}
