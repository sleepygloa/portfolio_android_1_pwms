package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 재고조사 정보 클래스
 */
public class InvenInquiryInfo extends Info implements Serializable {
    public static final int STATE_NONE = 0;
    public static final int STATE_SELECT = STATE_NONE + 1;
    public static final int STATE_DONE = STATE_SELECT + 1;

    /** 지시번호 */
    private String mOrderNumber;
    /** 지시 상세 번호 */
    private int mOrderDetailNumber;
    /** 제품 코드 */
    private String mGoodsCode;
    /** 제품명 */
    private String mGoodsName;
    /** 위치 */
    private String mLocation;
    /** 파렛트ID */
    private String mPltId;
    /** PLT 스캔 코드 */
    private String mPltScanCode;
    /** 지시수량 */
    private int mOrderCount;
    /** 지시수량 - PLT */
    private int mOrderPltCount;
    /** 지시수량 - BOX */
    private int mOrderBoxCount;
    /** 지시수량 - EA */
    private int mOrderEaCount;
    /** 재고수량 */
    private int mInvenCount;
    /** 재고수량 - PLT */
    private int mInvenPltCount;
    /** 재고수량 - BOX */
    private int mInvenBoxCount;
    /** 재고수량 - EA */
    private int mInvenEaCount;
    /** 실사수량 */
    private int mActualityCount;
    /** 실사수량 - PLT */
    private int mActualityPltCount;
    /** 실사수량 - BOX */
    private int mActualityBoxCount;
    /** 실사수량 - EA */
    private int mActualityEaCount;
    /** 제조 LOT */
    private String mManufactureLot;
    /** LOT 속성1 */
    private String mLotAttribute1;
    /** LOT 속성2 */
    private String mLotAttribute2;
    /** LOT 속성3 */
    private String mLotAttribute3;
    /** LOT 속성4 */
    private String mLotAttribute4;
    /** LOT 속성5 */
    private String mLotAttribute5;
    /** 제조일자 */
    private String mMakeDate;
    /** 유통일자 */
    private String mDistributionDate;
    /** 실사유형 */
    private String mActualityCategory;
    /** 제품상태 */
    private String mGoodsState;
    /** 입수 */
    private int mAcquire;
    /** 수량 - 박스 */
    private int mBoxCount;
    /** 수량 - EA */
    private int mEaCount;

    private int mProcessState;

    /**
     * 위치 설정
     * @param location 위치
     */
    public void setLocation(String location) {
        mLocation = location;
    }

    /**
     * 위치 반환
     * @return 위치
     */
    public String getLocation() {
        return mLocation;
    }

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
     * 지시 상세번호 설정
     * @param number 지시 상세번호
     */
    public void setOrderDetailNumber(int number) {
        mOrderDetailNumber = number;
    }

    /**
     * 지시 상세번호 반환
     * @return 지시 상세번호
     */
    public int getOrderDetailNumber() {
        return mOrderDetailNumber;
    }

    /**
     * 상품 코드 설정
     * @param code 상품 코드
     */
    public void setGoodsCode(String code) {
        mGoodsCode = code;
    }

    /**
     * 상품 코드 반환
     * @return 상품 코드
     */
    public String getGoodsCode() {
        return mGoodsCode;
    }

    /**
     * 상품 명 설정
     * @param name 상품 명
     */
    public void setGoodsName(String name) {
        mGoodsName = name;
    }

    /**
     * 상품명 반환
     * @return 상품명
     */
    public String getGoodsName() {
        return mGoodsName;
    }

    /**
     * 주문수량 설정
     * @param count 주문수량
     */
    public void setOrderCount(int count) {
        mOrderCount = count;
    }

    /**
     * 주문수량 반환
     * @return 주문수량
     */
    public int getOrderCount() {
        return mOrderCount;
    }

    /**
     * 주문수량 설정 - PLT
     * @param count 주문수량
     */
    public void setOrderPltCount(int count) {
        mOrderPltCount = count;
    }

    /**
     * 주문수량 반환 - PLT
     * @return 주문수량
     */
    public int getOrderPltCount() {
        return mOrderPltCount;
    }

    /**
     * 주문수량 설정 - BOX
     * @param count 주문수량
     */
    public void setOrderBoxCount(int count) {
        mOrderBoxCount = count;
    }

    /**
     * 주문수량 반환 - BOX
     * @return 주문수량
     */
    public int getOrderBoxCount() {
        return mOrderBoxCount;
    }

    /**
     * 주문수량 설정 - EA
     * @param count 주문수량
     */
    public void setOrderEaCount(int count) {
        mOrderEaCount = count;
    }

    /**
     * 주문수량 반환 - EA
     * @return 주문수량
     */
    public int getOrderEaCount() {
        return mOrderEaCount;
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
     * 실사수량 설정
     * @param count 실사수량
     */
    public void setActualityCount(int count) {
        mActualityCount = count;
    }

    /**
     * 실사수량 반환
     * @return 실사수량
     */
    public int getActualityCount() {
        return mActualityCount;
    }

    /**
     * 실사수량 설정 - PLT
     * @param count 실사수량
     */
    public void setActualityPltCount(int count) {
        mActualityPltCount = count;
    }

    /**
     * 실사수량 반환
     * @return 실사수량
     */
    public int getActualityPltCount() {
        return mActualityPltCount;
    }

    /**
     * 실사수량 설정 - BOX
     * @param count 실사수량
     */
    public void setActualityBoxCount(int count) {
        mActualityBoxCount = count;
    }

    /**
     * 실사수량 반환 - BOX
     * @return 실사수량
     */
    public int getActualityBoxCount() {
        return mActualityBoxCount;
    }

    /**
     * 실사수량 설정 - EA
     * @param count 실사수량
     */
    public void setActualityEaCount(int count) {
        mActualityEaCount = count;
    }

    /**
     * 실사수량 반환 - EA
     * @return 실사수량
     */
    public int getActualityEaCount() {
        return mActualityEaCount;
    }

    /**
     * 파렛트 ID 설정
     * @param plt_id 파렛트 ID
     */
    public void setPltId(String plt_id) {
        mPltId = plt_id;
    }

    /**
     * 파렛트 ID 반환
     * @return 파렛트 ID
     */
    public String getPltId() {
        return mPltId;
    }

    /**
     * PLT 스캔 코드 설정
     * @param code PLT 스캔 코드
     */
    public void setPltScanCode(String code) {
        mPltScanCode = code;
    }

    /**
     * PLT 스캔 코드 반환
     * @return PLT 스캔 코드
     */
    public String getPltScanCode() {
        return mPltScanCode;
    }

    /**
     * 제조 lot 설정
     * @param lot 제조 lot
     */
    public void setManufactureLot(String lot) {
        mManufactureLot = lot;
    }

    /**
     * 제조 lot 반환
     * @return 제조 lot
     */
    public String getManufactureLot() {
        return mManufactureLot;
    }

    /**
     * lot 속성 설정
     * @param attribute lot 속성
     */
    public void setLotAttribute1(String attribute) {
        mLotAttribute1 = attribute;
    }

    /**
     * lot 속성 반환
     * @return lot 속성
     */
    public String getLotAttribute1() {
        return mLotAttribute1;
    }

    /**
     * lot 속성 설정
     * @param attribute lot 속성
     */
    public void setLotAttribute2(String attribute) {
        mLotAttribute2 = attribute;
    }

    /**
     * lot 속성 반환
     * @return lot 속성
     */
    public String getLotAttribute2() {
        return mLotAttribute2;
    }

    /**
     * lot 속성 설정
     * @param attribute lot 속성
     */
    public void setLotAttribute3(String attribute) {
        mLotAttribute3 = attribute;
    }

    /**
     * lot 속성 반환
     * @return lot 속성
     */
    public String getLotAttribute3() {
        return mLotAttribute3;
    }

    /**
     * lot 속성 설정
     * @param attribute lot 속성
     */
    public void setLotAttribute4(String attribute) {
        mLotAttribute4 = attribute;
    }

    /**
     * lot 속성 반환
     * @return lot 속성
     */
    public String getLotAttribute4() {
        return mLotAttribute4;
    }

    /**
     * lot 속성 설정
     * @param attribute lot 속성
     */
    public void setLotAttribute5(String attribute) {
        mLotAttribute5 = attribute;
    }

    /**
     * lot 속성 반환
     * @return lot 속성
     */
    public String getLotAttribute5() {
        return mLotAttribute5;
    }

    /**
     * 제조일자 설정
     * @param date 제조일자
     */
    public void setMakeDate(String date) {
        mMakeDate = date;
    }

    /**
     * 제조일자 반환
     * @return 제조일자
     */
    public String getMakeDate() {
        return mMakeDate;
    }

    /**
     * 유통기한 설정
     * @param date 유통기한
     */
    public void setDistributionDate(String date) {
        mDistributionDate = date;
    }

    /**
     * 유통기한 반환
     * @return 유통기한
     */
    public String getDistributionDate() {
        return mDistributionDate;
    }

    /**
     * 실사유형 설정
     * @param category 실사유형
     */
    public void setActualityCategory(String category) {
        mActualityCategory = category;
    }

    /**
     * 실사유형 반환
     * @return 실사유형
     */
    public String getActualityCategory() {
        return mActualityCategory;
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
     * 수량 설정 - BOX
     * @param count 수량
     */
    public void setBoxCount(int count) {
        mBoxCount = count;
    }

    /**
     * 수량 반환 - EA
     * @return 수량
     */
    public int getBoxCount() {
        return mBoxCount;
    }

    /**
     * 수량 설정 - EA
     * @param count 수량
     */
    public void setEaCount(int count) {
        mEaCount = count;
    }

    /**
     * 수량 반환 - EA
     * @return 수량
     */
    public int getEaCount() {
        return mEaCount;
    }

    /**
     * 진행상태 설정
     * @param state 설정
     */
    public void setProcessState(int state) {
        mProcessState = state;
    }

    /**
     * 진행상태 반환
     * @return 확정 상태
     */
    public int getProcessState() {
        return mProcessState;
    }
}
