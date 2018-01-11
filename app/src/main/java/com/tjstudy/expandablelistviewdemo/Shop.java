package com.tjstudy.expandablelistviewdemo;

import java.util.List;

/**
 * @author maohl
 * @date 2017/12/15
 * @description
 */

public class Shop {
    private String shopId;//店铺id
    private String shopName;//店铺名称
    private List<Goods> goods;
    private boolean isShopSelect;//该店铺是否被选中

    public boolean isShopSelect() {
        return isShopSelect;
    }

    public void setShopSelect(boolean shopSelect) {
        isShopSelect = shopSelect;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public List<Goods> getGoods() {
        return goods;
    }

    public void setGoods(List<Goods> goods) {
        this.goods = goods;
    }

    static class Goods{
        private String goodsId;//商品id
        private String goodsName;//商品名称
        private String goodsImgUrl;//商品图片地址
        private String goodsPrice;//商品价格
        private int goodsCount;//商品数量
        private boolean isGoodsSelect;//该商品是否被选中

        public boolean isGoodsSelect() {
            return isGoodsSelect;
        }

        public void setGoodsSelect(boolean goodsSelect) {
            isGoodsSelect = goodsSelect;
        }

        public int getGoodsCount() {
            return goodsCount;
        }

        public void setGoodsCount(int goodsCount) {
            this.goodsCount = goodsCount;
        }

        public String getGoodsPrice() {
            return goodsPrice;
        }

        public void setGoodsPrice(String goodsPrice) {
            this.goodsPrice = goodsPrice;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getGoodsImgUrl() {
            return goodsImgUrl;
        }

        public void setGoodsImgUrl(String goodsImgUrl) {
            this.goodsImgUrl = goodsImgUrl;
        }
    }
}
