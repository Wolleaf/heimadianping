package com.hmdp.convert;

import com.hmdp.domain.doc.BlogDoc;
import com.hmdp.domain.entity.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlogConverter {

    @Mapping(target = "shopName", ignore = true)
    BlogDoc blog2BlogDoc(Blog blog);
}
