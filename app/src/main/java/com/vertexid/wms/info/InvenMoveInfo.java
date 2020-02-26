package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 재고이동 정보 클래스
 */
public class InvenMoveInfo extends Info implements Serializable {
    public static final int STATE_NONE = 0;
    public static final int STATE_SELECT = STATE_NONE + 1;
    public static final int STATE_DONE = STATE_SELECT + 1;

    /** 지시번호 */
    private String mOrderNumber;
    /** 상세 지시번호 */
    private String mOrderDetailNumber;
    /** 제품코드 */
    private String mGoodsCode;
    /** 제품명 */
    private String mGoodsName;
    /** 재고수량 */
    private int mInvenCount;
    /** 재고수량 - PLT */
    private int mInvenPltCount;
    /** 재고수량 - BOX */
    private int mInvenBoxCount;
    /** 재고수량 - EA */
    private int mInvenEaCount;
    /** 지시수량 */
    private int mOrderCount;
    /** 지시수량 - PLT */
    private int mOrderPltCount;
    /** 지시수량 - BOX */
    private int mOrderBoxCount;
    /** 지시수량 - EA */
    private int mOrderEaCount;
    /** 이동수량 */
    private int mMoveCount;
    /** 이동수량 - PLT */
    private int mMovePltCount;
    /** 이동수량 - BOX */
    private int mMoveBoxCount;
    /** 이동수량 - EA */
    private int mMoveEaCount;
    /** 대상위치 */
    private String mFromLocation;
    /** 이동 위치 */
    private String mToLocation;
    /** 대상 PLT ID */
    private String mFromPltId;
    /** 이동 PLT ID */
    private String mToPltId;
    /** 입수 */
    private int mAcquireCount;

    /** 진행 상태 */
    private int mProcessState;

    /**
     * 지시번호 설정
     * @param number 지시번호
     */
    public void setOrderNumber(String number) {
        mOrderNumber = number;
    }

    /**
     * 지시번호 반환
     * @return 지시번호
     */
    public String getOrderNumber() {
        return mOrderNumber;
    }

    /**
     * 상세 지시번호 설정
     * @param number 상세 지시번호
     */
    public void setOrderDetailNumber(String number) {
        mOrderDetailNumber = number;
    }

    /**
     * 상세 지시번호 반환
     * @return 상세 지시번호
     */
    public String getOrderDetailNumber() {
        return mOrderDetailNumber;
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
     * 제품코드 설정
     * @param code 제품코드
     */
    public void setGoodsCode(String code) {
        mGoodsCode = code;
    }

    /**
     * 제품코드 반환
     * @return 제품코드
     */
    public String getGoodsCode() {
        return mGoodsCode;
    }

    /**
     * 원 위치 설정
     * @param location 원 위치
     */
    public void setFromLocation(String location) {
        mFromLocation = location;
    }

    /**
     * 원 위치 반환
     * @return 원 위치
     */
    public String getFromLocation() {
        return mFromLocation;
    }

    /**
     * 이동 위치 설정
     * @param location 이동 위치
     */
    public void setToLocation(String location) {
        mToLocation = location;
    }

    /**
     * 이동 위치 반환
     * @return 이동 위치
     */
    public String getToLocation() {
        return mToLocation;
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

    /**
     * 재고수량 설정 - PLT
     * @param count 재고수량
     */
    public void setInvenPltCount(int count) {
        mInvenPltCount = count;
    }

    /**
     * 재고수량 반환 - PLT
     * @return 재고수량
     */
    public int getInvenPltCount() {
        return mInvenPltCount;
    }

    /**
     * 재고수량 설정 - BOX
     * @param count 재고수량
     */
    public void setInvenBoxCount(int count) {
        mInvenBoxCount = count;
    }

    /**
     * 재고수량 반환 - BOX
     * @return 재고수량
     */
    public int getInvenBoxCount() {
        return mInvenBoxCount;
    }

    /**
     * 재고수량 설정 - EA
     * @param count 재고수량
     */
    public void setInvenEaCount(int count) {
        mInvenEaCount = count;
    }

    /**
     * 재고수량 반환 - EA
     * @return 재고수량
     */
    public int getInvenEaCount() {
        return mInvenEaCount;
    }

    /**
     * 지시수량 설정
     * @param count 지시수량
     */
    public void setOrderCount(int count) {
        mOrderCount = count;
    }

    /**
     * 지시수량 반환
     * @return 지시수량
     */
    public int getOrderCount() {
        return mOrderCount;
    }

    /**
     * 지시수량 설정 - PLT
     * @param count 지시수량
     */
    public void setOrderPltCount(int count) {
        mOrderPltCount = count;
    }

    /**
     * 지시수량 반환 - PLT
     * @return 지시수량
     */
    public int getOrderPltCount() {
        return mOrderPltCount;
    }

    /**
     * 지시수량 설정 - BOX
     * @param count 지시수량
     */
    public void setOrderBoxCount(int count) {
        mOrderBoxCount = count;
    }

    /**
     * 지시수량 반환 - BOX
     * @return 지시수량
     */
    public int getOrderBoxCount() {
        return mOrderBoxCount;
    }

    /**
     * 지시수량 설정 - EA
     * @param count 지시수량
     */
    public void setOrderEaCount(int count) {
        mOrderEaCount = count;
    }

    /**
     * 지시수량 반환 - EA
     * @return 지시수량
     */
    public int getOrderEaCount() {
        return mOrderEaCount;
    }

    /**
     * 이동수량 설정
     * @param count 이동수량
     */
    public void setMoveCount(int count) {
        mMoveCount = count;
    }

    /**
     * 이동수량 반환
     * @return 이동수량
     */
    public int getMoveCount() {
        return mMoveCount;
    }

    /**
     * 이동수량 설정 - PLT
     * @param count 이동수량
     */
    public void setMovePltCount(int count) {
        mMovePltCount = count;
    }

    /**
     * 이동수량 반환 - PLT
     * @return 이동수량
     */
    public int getMovePltCount() {
        return mMovePltCount;
    }

    /**
     * 이동수량 설정 - BOX
     * @param count 이동수량
     */
    public void setMoveBoxCount(int count) {
        mMoveBoxCount = count;
    }

    /**
     * 이동수량 반환 - BOX
     * @return 이동수량
     */
    public int getMoveBoxCount() {
        return mMoveBoxCount;
    }

    /**
     * 이동수량 설정 - EA
     * @param count 이동수량
     */
    public void setMoveEaCount(int count) {
        mMoveEaCount = count;
    }

    /**
     * 이동수량 반환 - EA
     * @return 이동수량
     */
    public int getMoveEaCount() {
        return mMoveEaCount;
    }

    /**
     * 원 PLT ID 설정
     * @param plt_id 원 PLT ID
     */
    public void setFromPltId(String plt_id) {
        mFromPltId = plt_id;
    }

    /**
     * 원 PLT ID 반환
     * @return 원 PLT ID
     */
    public String getFromPltId() {
        return mFromPltId;
    }

    /**
     * 이동 PLT ID 설정
     * @param plt_id 이동 PLT ID
     */
    public void setToPltId(String plt_id) {
        mToPltId = plt_id;
    }

    /**
     * 이동 PLT ID 반환
     * @return 이동PLT ID
     */
    public String getToPltId() {
        return mToPltId;
    }

    /**
     * 진행 상태 설정
     * @param state 진행상태
     */
    public void setProcessState(int state) {
        mProcessState = state;
    }

    /**
     * 진행 상태 반환
     * @return 진행상태
     */
    public int getProcessState() {
        return mProcessState;
    }

    /**
     * 입수 설정
     * @param acquire 입수
     */
    public void setAcquireCount(int acquire) {
        mAcquireCount = acquire;
    }

    /**
     * 입수 반환
     * @return 입수
     */
    public int getAcquireCount() {
        return mAcquireCount;
    }
}
