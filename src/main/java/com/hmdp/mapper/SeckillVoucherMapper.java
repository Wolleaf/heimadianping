package com.hmdp.mapper;

import com.hmdp.domain.entity.SeckillVoucher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系 Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2022-01-04
 */
public interface SeckillVoucherMapper extends BaseMapper<SeckillVoucher> {

    /**
     * 扣减库存
     * @param voucherId
     * @return
     */
    @Update("update tb_seckill_voucher set stock=stock-1 where voucher_id=#{voucherId} and stock>0")
    int updateStock(Long voucherId);
}
