package com.chentian.kill.service;

import com.chentian.kill.dao.GoodsDao;
import com.chentian.kill.domain.MiaoshaOrder;
import com.chentian.kill.domain.MiaoshaUser;
import com.chentian.kill.domain.OrderInfo;
import com.chentian.kill.redis.MiaoshaKey;
import com.chentian.kill.redis.RedisService;
import com.chentian.kill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减少库存 下订单 写入秒杀订单
        boolean success = goodsService.reduceStock(goods);
        if (success){
            return orderService.createOrder(user,goods);
        }else {
            //下单失败，设置 已经卖完状态
            setGoodsOver(goods.getId());
            return null;
        }

    }

    /**
     * 获取秒杀结果
     * @param userId
     * @param goodsId
     * @return
     */
    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        if (order != null){
            return order.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver){//卖完了 返回-1 抢购失败
                return -1;
            }else { //没有卖完，则返回0 排队中
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver,""+goodsId,true);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);
    }
}
