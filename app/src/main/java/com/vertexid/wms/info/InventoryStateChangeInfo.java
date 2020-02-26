package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 재고상태변경 정보 클래스
 */
public class InventoryStateChangeInfo extends Info implements Serializable {
    public static final int NONE = 0;
    public static final int SELECT = NONE + 1;
    public static final int CONFIRM_COMPLETE = SELECT + 1;

    /** 지시번호 */
    private String mOrderNumber;
    /** 지시 상세 번호 */
    private int mOrderDetailNumber;
    /** 제품코드 */
    private String mGoodsCode;
    /** 제품명 */
    private String mGoodsName;
    /** 입수 */
    private int mAcquire;
    /** 원 제품상태 */
    private String mFromGoodsState;
    /** 변경될 제품상태 */
    private String mChangeGoodsState;
    /** 제품 원위치 */
    private String mFromLocation;
    /** 변경될 제품위치 */
    private String mChangeLocation;
    /** 원 제품 파렛트 ID */
    private String mFromPltId;
    /** 변경될 제품 파렛트 ID */
    private String mChangePltId;
    /** 재고수량 */
    private int mInvenCount;
    /** 재고수량 - BOX */
    private int mInvenBoxCount;
    /** 재고수량 - EA */
    private int mInvenEaCount;
    /** 변경수량 - BOX */
    private int mChangeBoxCount;
    /** 변경수량 - EA */
    private int mChangeEaCount;

    /** 확정상태 */
    private int mConfirmState;

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
     * 지시상세번호 설정
     * @param number 지시상세번호
     */
    public void setOrderDetailNumber(int number) {
        mOrderDetailNumber = number;
    }

    /**
     * 지시상세번호 반환
     * @return 지시상세번호
     */
    public int getOrderDetailNumber() {
        return mOrderDetailNumber;
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
     * @return 제품 코드
     */
    public String getGoodsCode() {
        return mGoodsCode;
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
     * 입수 설정
     * @param acquire 입수
     */
    public void setAcquire(int acquire) {
        mAcquire = acquire;
    }

    /**
     * 입수 반환
     * @return 입수
     */
    public int getAcquire() {
        return mAcquire;
    }

    /**
     * 제품 원상태 설정
     * @param state 제품 원상태
     */
    public void setFromGoodsState(String state) {
        mFromGoodsState = state;
    }

    /**
     * 제품 원상태 반환
     * @return 제품 원상태
     */
    public String getFromGoodState() {
        return mFromGoodsState;
    }

    /**
     * 제품 변경상태 설정
     * @param state 제품 변경상태
     */
    public void setChangeGoodsState(String state) {
        mChangeGoodsState = state;
    }

    /**
     * 제품 변경상태 반환
     * @return 제품 변경상태
     */
    public String getChangeGoodState() {
        return mChangeGoodsState;
    }

    /**
     * 제품 원위치 설정
     * @param location 제품 원위치
     */
    public void setFromLocation(String location) {
        mFromLocation = location;
    }

    /**
     * 제품 원위치 반환
     * @return 제품 원위치
     */
    public String getFromLocation() {
        return mFromLocation;
    }

    /**
     * 제품 변경위치 설정
     * @param location 제품 변경위치
     */
    public void setChangeLocation(String location) {
        mChangeLocation = location;
    }

    /**
     * 제품 변경위치 반환
     * @return 제품 변경위치
     */
    public String getChangeLocation() {
        return mChangeLocation;
    }

    /**
     * 제품 원 파렛트 설정
     * @param plt_id 제품 원 파렛트
     */
    public void setFromPltId(String plt_id) {
        mFromPltId = plt_id;
    }

    /**
     * 제품 원 파렛트 반환
     * @return 제품 원 파렛트
     */
    public String getFromPltId() {
        return mFromPltId;
    }

    /**
     * 제품 변경 파렛트 설정
     * @param plt_id 제품 변경 파렛트
     */
    public void setChangePltId(String plt_id) {
        mChangePltId = plt_id;
    }

    /**
     * 제품 변경 파렛트 반환
     * @return 제품 변경 파렛트
     */
    public String getChangePltId() {
        return mChangePltId;
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
     * 변경수량 설정 - BOX
     * @param count 변경수량
     */
    public void setChangeBoxCount(int count) {
        mChangeBoxCount = count;
    }

    /**
     * 변경수량 반환 - BOX
     * @return 변경수량
     */
    public int getChangeBoxCount() {
        return mChangeBoxCount;
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
     * 변경수량 설정 - EA
     * @param count 변경수량
     */
    public void setChangeEaCount(int count) {
        mChangeEaCount = count;
    }

    /**
     * 변경수량 반환 - EA
     * @return 변경수량
     */
    public int getChangeEaCount() {
        return mChangeEaCount;
    }

    /**
     * 확정 상태 설정
     * @param state 상태
     */
    public void setConfirmState(int state) {
        mConfirmState = state;
    }

    /**
     * 확정 상태 반환
     * @return 상태
     */
    public int getConfirmState() {
        return mConfirmState;
    }
}
