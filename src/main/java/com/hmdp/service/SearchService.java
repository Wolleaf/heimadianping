package com.hmdp.service;

import com.hmdp.domain.doc.BlogDoc;
import com.hmdp.domain.doc.ShopDoc;
import com.hmdp.domain.doc.UserDoc;
import com.hmdp.domain.dto.search.BlogSearchDTO;
import com.hmdp.domain.dto.search.ShopSearchDTO;
import com.hmdp.domain.dto.search.UserSearchDTO;

import java.util.List;

public interface SearchService {


    List<BlogDoc> searchBlog(BlogSearchDTO blogSearchDTO);

    List<ShopDoc> searchShop(ShopSearchDTO shopSearchDTO);

    List<UserDoc> searchUser(UserSearchDTO userSearchDTO);
}
