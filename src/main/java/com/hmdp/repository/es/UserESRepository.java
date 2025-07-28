package com.hmdp.repository.es;

import com.hmdp.domain.doc.UserDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserESRepository extends ElasticsearchRepository<UserDoc, Long> {
}
