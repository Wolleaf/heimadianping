package com.hmdp.convert;

import com.hmdp.domain.doc.ShopDoc;
import com.hmdp.domain.dto.ShopDTO;
import com.hmdp.domain.entity.Shop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShopConverter {

    Shop shopDTO2Shop(ShopDTO shopDTO);

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "location",
            expression = "java(new GeoPoint(shop.getX(), shop.getY()))")
    ShopDoc shop2ShopDoc(Shop shop);
}
