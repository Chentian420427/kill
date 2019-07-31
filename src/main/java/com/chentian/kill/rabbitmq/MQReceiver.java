package com.chentian.kill.rabbitmq;

import com.chentian.kill.domain.MiaoshaOrder;
import com.chentian.kill.domain.MiaoshaUser;
import com.chentian.kill.domain.OrderInfo;
import com.chentian.kill.redis.RedisService;
import com.chentian.kill.result.CodeMsg;
import com.chentian.kill.result.Result;
import com.chentian.kill.service.GoodsService;
import com.chentian.kill.service.MiaoshaService;
import com.chentian.kill.service.OrderService;
import com.chentian.kill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message){
        log.info("receive message: " + message);
        MiaoshaMessage mm = RedisService.stringToBean(message,MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);
    }

//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        log.info("receive message: " + message);
//    }


}
