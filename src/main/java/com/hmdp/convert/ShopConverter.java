package com.hmdp.convert;

import com.hmdp.domain.dto.ShopDTO;
import com.hmdp.domain.entity.Shop;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShopConverter {

    Shop shopDTO2Shop(ShopDTO shopDTO);
}
