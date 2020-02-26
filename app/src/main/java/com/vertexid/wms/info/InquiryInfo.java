package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 조회관리 정보 클래스
 */
public class InquiryInfo extends Info implements Serializable {
    /** 제품 코드 */
    private String mGoodsCode = "";
    /** 제품 이름 */
    private String mGoodsName = "";
    /** 제품 위치 */
    private String mLocation = "";
    /** 제품상태 */
    private String mGoodsState = "";
    /** lot id */
    private String mLotId = "";
    /** 제조일자 */
    private String mMakeDates = "";
    /** 입수 */
    private int mAcquire;
    /** 박스 */
    private int mBoxCount;
    /** 수량 */
    private int mEaCount;
    /** 재고수량 */
    private int mInvenCount;

    /**
     * 제품코드 설정
     * @param goods_code 제품코드
     */
    public void setGoodsCode(String goods_code) {
        mGoodsCode = goods_code;
    }

    /**
     * 제품코드 반환
     * @return 제품코드
     */
    public String getGoodsCode() {
        return mGoodsCode;
    }

    /**
     * 제품명 설정
     * @param goods_name 제품명
     */
    public void setGoodsName(String goods_name) {
        mGoodsName = goods_name;
    }

    /**
     * 제품명 반환
     * @return 제품명
     */
    public String getGoodsName() {
        return mGoodsName;
    }

    /**
     * 제품코드 설정
     * @param location 제품코드
     */
    public void setLocation(String location) {
        mLocation = location;
    }

    /**
     * 제품 위치 반환
     * @return 제품 위치
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * 제품코드 설정
     * @param goods_state 제품코드
     */
    public void setGoodsState(String goods_state) {
        mGoodsState = goods_state;
    }

    /**
     * 제품상태 반환
     * @return 제품상태
     */
    public String getGoodsState() {
        return mGoodsState;
    }

    /**
     * 제품코드 설정
     * @param lot_id 제품코드
     */
    public void setLotId(String lot_id) {
        mLotId = lot_id;
    }

    /**
     * LOT ID 반환
     * @return LOT ID
     */
    public String getLotId() {
        return mLotId;
    }

    /**
     * 제조일자 설정
     * @param date 제조일자
     */
    public void setMakeDates(String date) {
        mMakeDates = date;
    }

    /**
     * 제조일자 반환
     * @return 제조일자
     */
    public String getMakeDates() {
        return mMakeDates;
    }

    /**
     * 입수 설정
     * @param acquire 입수
     */
    public void setAcquire(int acquire) {
        mAcquire = acquire;
    }

    /**
     * 입수 반환
     * @return 입수반환
     */
    public int getAcquire() {
        return mAcquire;
    }

    /**
     * BOX 개수 설정
     * @param count BOX 개수
     */
    public void setBoxCount(int count) {
        mBoxCount = count;
    }

    /**
     * BOX 개수 반환
     * @return BOX 개수
     */
    public int getBoxCount() {
        return mBoxCount;
    }

    /**
     * 수량 설정
     * @param count 수량 설정
     */
    public void setEaCount(int count) {
        mEaCount = count;
    }

    /**
     * 수량 반환
     * @return 수량 반환
     */
    public int getEaCount() {
        return mEaCount;
    }

    /**
     * 재고수량 설정
     * @param count 재고수량
     */
    public void setInvenCount(int count) {
        mInvenCount = count;
    }

    /**
     * 재고수량 반환
     * @return 재고수량
     */
    public int getInvenCount() {
        return mInvenCount;
    }
}
