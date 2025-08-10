package com.hmdp.listener;

import com.hmdp.convert.BlogConverter;
import com.hmdp.convert.ShopConverter;
import com.hmdp.convert.UserConverter;
import com.hmdp.domain.doc.BlogDoc;
import com.hmdp.domain.doc.ShopDoc;
import com.hmdp.domain.doc.UserDoc;
import com.hmdp.domain.dto.BinLog;
import com.hmdp.domain.entity.Blog;
import com.hmdp.domain.entity.Shop;
import com.hmdp.domain.entity.ShopType;
import com.hmdp.domain.entity.User;
import com.hmdp.enums.OpEnum;
import com.hmdp.repository.es.BlogESRepository;
import com.hmdp.repository.es.ShopESRepository;
import com.hmdp.repository.es.UserESRepository;
import com.hmdp.service.IShopService;
import com.hmdp.service.IShopTypeService;
import com.hmdp.util.BinlogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 监听mysql的binlog，解析并同步到es中
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MysqlBinlogListener {

    private final BlogESRepository blogESRepository;
    private final ShopESRepository shopESRepository;
    private final UserESRepository userESRepository;
    private final BlogConverter blogConverter;
    private final ShopConverter shopConverter;
    private final UserConverter userConverter;
    private final IShopService shopService;
    private final IShopTypeService shopTypeService;

    /**
     * blog binlog同步到es中
     * @param message Binlog的json格式
     */
    @KafkaListener(topics = "binlog-blog")
    public void blogBinlog2ES(String message) {
        log.info("binlog-blog同步内容:{}",message);
        // 解析binlog
        BinLog binLog = BinlogUtils.parseJsonString2Binlog(message);
        // 如果是删除，直接跳过
        if (OpEnum.isDelete(binLog.getOp())) {
            // 拿到id删除es中的数据
            Blog blog = binLog.getBefore().toJavaObject(Blog.class);
            blogESRepository.deleteById(blog.getId());
            return;
        }
        // after肯定有值，先解析after
        Blog afterBlog = binLog.getAfter().toJavaObject(Blog.class);
        // 获取id
        Long id = afterBlog.getId();
        // 如果是更新，则多一步删除
        if (OpEnum.isReadOrUpdate(binLog.getOp())) {
            blogESRepository.deleteById(id);
        }
        // 转换为doc
        BlogDoc blogDoc = blogConverter.blog2BlogDoc(afterBlog);
        // 补充店铺名称
        Shop shop = shopService.getById(afterBlog.getShopId());
        blogDoc.setShopName(shop.getName());
        blogESRepository.save(blogDoc);
    }

    /**
     * shop binlog同步到es中
     * @param message Binlog的json格式
     */
    @KafkaListener(topics = "binlog-shop")
    public void shopBinlog2ES(String message) {
        log.info("binlog-shop同步内容:{}",message);
        // 解析binlog
        BinLog binLog = BinlogUtils.parseJsonString2Binlog(message);
        // 如果是删除，直接跳过
        if (OpEnum.isDelete(binLog.getOp())) {
            return;
        }
        // after肯定有值，先解析after
        Shop afterShop = binLog.getAfter().toJavaObject(Shop.class);
        // 获取id
        Long id = afterShop.getId();
        // 如果是更新，则多一步删除
        if (OpEnum.isReadOrUpdate(binLog.getOp())) {
            shopESRepository.deleteById(id);
        }
        // 转换为doc
        ShopDoc shopDoc = shopConverter.shop2ShopDoc(afterShop);
        // 补充店铺类型名称
        ShopType shopType = shopTypeService.getById(afterShop.getTypeId());
        shopDoc.setType(shopType.getName());
        shopESRepository.save(shopDoc);
    }

    /**
     * user binlog同步到es中
     * @param message Binlog的json格式
     */
    @KafkaListener(topics = "binlog-user")
    public void userBinlog2ES(String message) {
        log.info("binlog-user同步内容:{}",message);
        // 解析binlog
        BinLog binLog = BinlogUtils.parseJsonString2Binlog(message);
        // 如果是删除，直接跳过
        if (OpEnum.isDelete(binLog.getOp())) {
            return;
        }
        // after肯定有值，先解析after
        User afterUser = binLog.getAfter().toJavaObject(User.class);
        // 获取id
        Long id = afterUser.getId();
        // 如果是更新，则多一步删除
        if (OpEnum.isReadOrUpdate(binLog.getOp())) {
            userESRepository.deleteById(id);
        }
        UserDoc userDoc = userConverter.user2UserDoc(afterUser);
        userESRepository.save(userDoc);
    }
}
