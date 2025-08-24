package com.hmdp.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.hmdp.domain.dto.Result;
import com.hmdp.domain.entity.VoucherOrder;
import com.hmdp.service.IVoucherOrderService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/voucher-order")
@RequiredArgsConstructor
public class VoucherOrderController {

    private final IVoucherOrderService voucherOrderService;

    @ApiOperation("秒杀优惠券")
    @SentinelResource(value = "seckillVoucher", blockHandler = "seckillVoucherBlockHandler")
    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {
        Long orderId = voucherOrderService.seckillVoucher(voucherId);
        return Result.success(orderId);
    }

    /**
     * 限流降级处理方法
     */
    public Result seckillVoucherBlockHandler(@PathVariable("id") Long voucherId, BlockException ex) {
        return Result.error("系统繁忙，请稍后再试");
    }

    @ApiOperation("查询用户优惠券")
    @GetMapping("{user-id}")
    public Result queryVoucherOrderById(@PathVariable("user-id") Long userId) {
        List<VoucherOrder> list = voucherOrderService.lambdaQuery()
                .eq(VoucherOrder::getUserId, userId)
                .list();
        return Result.success(list);
    }
}
