package com.hmdp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoucherOrderStatusEnum {
    UNPAID(1, "未支付"),
    PAID(2, "已支付"),
    USED(3, "已核销"),
    CANCELED(4, "已取消"),
    REFUNDING(5, "退款中"),
    REFUNDED(6, "已退款"),
    ;

    private final int code;
    private final String description;

    public static VoucherOrderStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (VoucherOrderStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public boolean isFinalStatus() {
        return this == USED || this == CANCELED || this == REFUNDED;
    }

    public boolean isRefundRelated() {
        return this == REFUNDING || this == REFUNDED;
    }
}


