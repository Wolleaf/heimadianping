package com.hmdp.transfer;

import com.hmdp.domain.dto.ShopDTO;
import com.hmdp.domain.entity.Shop;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DTO2POConverter {

    Shop shopDTO2PO(ShopDTO shopDTO);
}
