package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 입고번호 정보 클래스
 */
public class WareHouseNumberInfo extends Info implements Serializable {
    /** 입고번호 */
    private String mNumber;
    /** 공급처 */
    private String mSupplier;
    /** 제품 */
    private String mGoods;
    /** 제품명 */
    private String mGoodsName;
    /** 입고유형 */
    private String mCategory;
    /** 입고일자 */
    private String mDate;

    /**
     * 입고번호 설정
     * @param number 입고번호
     */
    public void setWareHouseNumber(String number) {
        mNumber = number;
    }

    /**
     * 입고번호 반환
     * @return 입고번호
     */
    public String getWareHouseNumber() {
        return mNumber;
    }

    /**
     * 공급처 설정
     * @param supplier 공급처
     */
    public void setSupplier(String supplier) {
        mSupplier = supplier;
    }

    /**
     * 공급처 반환
     * @return 공급처
     */
    public String getSupplier() {
        return mSupplier;
    }

    /**
     * 제품 설정
     * @param goods 제품
     */
    public void setGoods(String goods) {
        mGoods = goods;
    }

    /**
     * 제품 반환
     * @return 제품
     */
    public String getGoods() {
        return mGoods;
    }

    /**
     * 제품명 설정
     * @param name 제품명
     */
    public void setGoodsName(String name) {
        mGoodsName = name;
    }

    /**
     * 제품명 반환
     * @return 제품명
     */
    public String getGoodsName() {
        return mGoodsName;
    }

    /**
     * 입고일자 설정
     * @param date 입고일자
     */
    public void setDate(String date) {
        mDate = date;
    }

    /**
     * 입고일자 반환
     * @return 입고일자
     */
    public String getDate() {
        return mDate;
    }

    /**
     * 입고유형 설정
     * @param category 입고유형
     */
    public void setCategory(String category) {
        mCategory = category;
    }

    /**
     * 입고유형 반환
     * @return 입고유형
     */
    public String getCategory() {
        return mCategory;
    }
}
