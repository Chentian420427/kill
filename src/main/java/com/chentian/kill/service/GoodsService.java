package com.chentian.kill.service;

import com.chentian.kill.dao.GoodsDao;
import com.chentian.kill.domain.Goods;
import com.chentian.kill.domain.MiaoshaGoods;
import com.chentian.kill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }


    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 减少库存
     * @param goods
     */
    public boolean reduceStock(GoodsVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());

        int ret = goodsDao.reduceStock(g);

        return ret > 0;
    }
}
