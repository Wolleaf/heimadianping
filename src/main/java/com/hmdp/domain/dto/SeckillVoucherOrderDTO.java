package com.hmdp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeckillVoucherOrderDTO {
    private Long id;
    private Long voucherId;
    private Long userId;
}
