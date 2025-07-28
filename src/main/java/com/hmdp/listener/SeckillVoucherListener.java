package com.hmdp.listener;

import com.hmdp.constant.KafkaTopicConstants;
import com.hmdp.domain.dto.SeckillVoucherOrderDTO;
import com.hmdp.domain.entity.VoucherOrder;
import com.hmdp.service.IVoucherOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeckillVoucherListener {

    private final IVoucherOrderService voucherOrderService;

    @KafkaListener(topics = KafkaTopicConstants.SECKILL_VOUCHER_ORDER)
    public void seckillVoucherOrder(SeckillVoucherOrderDTO seckillVoucherOrderDTO) {
        // 构建优惠券订单对象
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(seckillVoucherOrderDTO.getId());
        voucherOrder.setUserId(seckillVoucherOrderDTO.getUserId());
        voucherOrder.setVoucherId(seckillVoucherOrderDTO.getVoucherId());
        // 保存
        voucherOrderService.saveVoucherOrder(voucherOrder);
    }
}
