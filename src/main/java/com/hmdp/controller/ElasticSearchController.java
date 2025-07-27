package com.hmdp.controller;

import com.hmdp.domain.dto.BlogDTO;
import com.hmdp.domain.dto.Result;
import com.hmdp.domain.dto.UserDTO;
import com.hmdp.domain.dto.search.ShopSearchDTO;
import com.hmdp.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class ElasticSearchController {

    private final SearchService searchService;

    @PostMapping("/shop")
    public Result searchShop(@RequestBody ShopSearchDTO shopSearchDTO) {
        return null;
    }

    @PostMapping("/blog")
    public Result searchBlog(@RequestBody BlogDTO blogDTO) {
        return null;
    }

    @PostMapping("/user")
    public Result searchUser(@RequestBody UserDTO userDTO) {
        return null;
    }
}
