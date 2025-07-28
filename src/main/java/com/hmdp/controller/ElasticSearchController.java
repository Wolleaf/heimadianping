package com.hmdp.controller;

import com.hmdp.domain.doc.BlogDoc;
import com.hmdp.domain.doc.ShopDoc;
import com.hmdp.domain.doc.UserDoc;
import com.hmdp.domain.dto.Result;
import com.hmdp.domain.dto.search.BlogSearchDTO;
import com.hmdp.domain.dto.search.ShopSearchDTO;
import com.hmdp.domain.dto.search.UserSearchDTO;
import com.hmdp.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class ElasticSearchController {

    private final SearchService searchService;

    @PostMapping("/blog")
    public Result searchBlog(@RequestBody BlogSearchDTO blogSearchDTO) {
        List<BlogDoc> blogDocs = searchService.searchBlog(blogSearchDTO);
        return Result.success(blogDocs);
    }

    @PostMapping("/shop")
    public Result searchShop(@RequestBody ShopSearchDTO shopSearchDTO) {
        List<ShopDoc> shopDocs = searchService.searchShop(shopSearchDTO);
        return Result.success(shopDocs);
    }

    @PostMapping("/user")
    public Result searchUser(@RequestBody UserSearchDTO userSearchDTO) {
        List<UserDoc> userDocs = searchService.searchUser(userSearchDTO);
        return Result.success(userDocs);
    }
}
