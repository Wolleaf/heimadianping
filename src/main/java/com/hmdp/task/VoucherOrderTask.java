package com.hmdp.task;

import com.hmdp.mapper.VoucherOrderMapper;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherOrderTask {

    private final VoucherOrderMapper voucherOrderMapper;

    @XxlJob("voucherOrderStatusUnpaid2CanceledJob")
    public void voucherOrderStatusUnpaid2CanceledJob() {
        log.info("voucherOrderStatusUnpaid2CanceledJob任务开始执行");
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.info("shardIndex: {}, shardTotal: {}", shardIndex, shardTotal);

        // 查询未支付的订单，根据uid分片，指定查出来的条数
        List<Long> unpaidVoucherOrderIds = voucherOrderMapper.getUnpaidOrders(shardIndex, shardTotal, 5);
        if (unpaidVoucherOrderIds.isEmpty()) {
            return;
        }

        // 更新未支付的订单为取消
        voucherOrderMapper.updateUnpaid2Canceled(unpaidVoucherOrderIds);
    }
}
