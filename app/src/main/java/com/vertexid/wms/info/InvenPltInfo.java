package com.vertexid.wms.info;

import org.json.JSONArray;

import java.io.Serializable;

/**
 * 재고관리 파렛트 분할/병합 정보 클래스
 */
public class InvenPltInfo extends Info implements Serializable {
    public static final int STATE_NONE = 0;
    public static final int STATE_SELECT = STATE_NONE + 1;
    public static final int STATE_DONE = STATE_SELECT + 1;

    /** 제품코드 */
    private String mGoodsCode;
    /** 제품명 */
    private String mGoodsName;
    /** 제품상태 */
    private String mGoodsState;
    /** 대상 LOC */
    private String mFromLocation;
    /** 목표 LOC */
    private String mToLocation;
    /** 대상 파렛트 ID */
    private String mFromPltId;
    /** 목표 파렛트 ID */
    private String mToPltId;
    /** 입수 */
    private int mAcquire;
    /** 재고수량 - PLT */
    private int mInvenPltCount;
    /** 재고수량 - BOX */
    private int mInvenBoxCount;
    /** 재고수량 - EA */
    private int mInvenEaCount;
    /** 분할/병합 수량 */
    private int mToCount;
    /** 분할/병합 수량 - PLT */
    private int mToPltCount;
    /** 분할/병합 수량 - BOX */
    private int mToBoxCount;
    /** 분할/병합 수량 - EA */
    private int mToEaCount;
    /** 파렛트 변경번호 */
    private String mPltChangeNumber;
    /** LOT ID */
    private String mLotId;
    private JSONArray mPltArray;

    private int mState;

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
     * 제품상태 설정
     * @param state 제품상태
     */
    public void setGoodsState(String state) {
        mGoodsState = state;
    }

    /**
     * 제품상태 반환
     * @return 제품상태
     */
    public String getGoodsState() {
        return mGoodsState;
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
     * 제품 이동위치 설정
     * @param location 제품 이동위치
     */
    public void setToLocation(String location) {
        mToLocation = location;
    }

    /**
     * 제품 이동위치 반환
     * @return 제품 이동위치
     */
    public String getToLocation() {
        return mToLocation;
    }

    /**
     * 제품 원 PLT ID 설정
     * @param plt_id 제품 원 PLT ID
     */
    public void setFromPltId(String plt_id) {
        mFromPltId = plt_id;
    }

    /**
     * 제품 원 PLT ID 반환
     * @return 제품 원 PLT ID
     */
    public String getFromPltId() {
        return mFromPltId;
    }

    /**
     * 제품 이동 PLT ID 설정
     * @param plt_id 제품 이동 PLT ID
     */
    public void setToPltId(String plt_id) {
        mToPltId = plt_id;
    }

    /**
     * 제품 이동 PLT ID 반환
     * @return 제품 이동 PLT ID
     */
    public String getToPltId() {
        return mToPltId;
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
     * 분할/병합수량 설정
     * @param count 분할/병합수량
     */
    public void setToCount(int count) {
        mToCount = count;
    }

    /**
     * 분할/병합수량 반환
     * @return 분할/병합수량
     */
    public int getToCount() {
        return mToCount;
    }

    /**
     * 분할/병합 수량 설정 - PLT
     * @param count 분할/병합 수량
     */
    public void setToPltCount(int count) {
        mToPltCount = count;
    }

    /**
     * 분할/병합 수량 반환 - PLT
     * @return 분할/병합 수량
     */
    public int getToPltCount() {
        return mToPltCount;
    }

    /**
     * 분할/병합 수량 설정 - BOX
     * @param count 분할/병합 수량
     */
    public void setToBoxCount(int count) {
        mToBoxCount = count;
    }

    /**
     * 분할/병합 수량 반환 - BOX
     * @return 분할/병합 수량
     */
    public int getToBoxCount() {
        return mToBoxCount;
    }

    /**
     * 분할/병합 수량 설정 - EA
     * @param count 분할/병합 수량
     */
    public void setToEaCount(int count) {
        mToEaCount = count;
    }

    /**
     * 분할/병합 수량 반환 - EA
     * @return 분할/병합 수량
     */
    public int getToEaCount() {
        return mToEaCount;
    }

    /**
     * PLT 변경 번호 설정
     * @param number PLT 변경 번호
     */
    public void setPltChangeNumber(String number) {
        mPltChangeNumber = number;
    }

    /**
     * PLT 변경 번호 반환
     * @return PLT 변경 번호
     */
    public String getPltChangeNumber() {
        return mPltChangeNumber;
    }

    /**
     * LOT ID 설정
     * @param id LOT ID
     */
    public void setLotId(String id) {
        mLotId = id;
    }

    /**
     * LOT ID 반환
     * @return LOT ID
     */
    public String getLotId() {
        return mLotId;
    }

    public void setPltArray(JSONArray array) {
        mPltArray = array;
    }

    public JSONArray getPltArray() {
        return mPltArray;
    }

    /**
     * 분할/병합 상태 설정
     * @param state 분할/병합 상태
     */
    public void setState(int state) {
        mState = state;
    }

    /**
     * 분할/병합 상태 반환
     * @return 분할/병합 상태
     */
    public int getState() {
        return mState;
    }
}
