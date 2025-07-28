package com.hmdp.repository.es;

import com.hmdp.domain.doc.BlogDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogESRepository extends ElasticsearchRepository<BlogDoc, Long> {
}
