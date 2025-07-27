package com.hmdp.repository.elasticsearch;

import com.hmdp.domain.doc.ShopDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopESRepository extends ElasticsearchRepository<ShopDoc, Long> {
}
