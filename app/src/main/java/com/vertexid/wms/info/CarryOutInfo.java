package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 반출 관련 정보 클래스
 */
public class CarryOutInfo extends Info implements Serializable {
    public static final int CONFIRM_STATE_NORMAL = 0;
    public static final int CONFIRM_STATE_SELECT = CONFIRM_STATE_NORMAL + 1;
    public static final int CONFRIM_STATE_COMPLETE = CONFIRM_STATE_SELECT + 1;

    /** 제품코드 */
    private String mGoodsCode;
    /** 제품명 */
    private String mGoodsName;
    /** 공급처 */
    private String mSupplier;
    /** 주문수량 */
    private int mOrderCount;
    /** 주문수량 - PLT */
    private int mOrderPltCount;
    /** 주문수량 - Box */
    private int mOrderBoxCount;
    /** 주문수량 - EA */
    private int mOrderEaCount;
    /** 검수 수량 */
    private int mCheckCount;
    /** 지시 수량 - PLT */
    private int mPickingPltCount;
    /** 지시 수량 - Box */
    private int mPickingBoxCount;
    /** 지시 수량 - EA */
    private int mPickingEaCount;
    /** 파렛트ID */
    private String mPltId;
    /** 파렛트ID 스캔 */
    private String mPltIdScan;
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
    /** 반출번호 */
    private String mCarryOutNumber;
    /** 반출상세번호 */
    private int mCarryOutDetailNumber;
    /** 반출지시번호 */
    private String mCarryOutOrderNumber;
    /** 반출 사유 */
    private String mReason;
    /** 제품위치 */
    private String mLocation;
    /** 제품위치 스캔 */
    private String mLocScan;

    /** 확정 상태 */
    private int mConfirmState;

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
     * 반출번호 설정
     * @param number 반출번호
     */
    public void setCarryOutNumber(String number) {
        mCarryOutNumber = number;
    }

    /**
     * 반출번호 반환
     * @return 반출번호
     */
    public String getCarryOutNumber() {
        return mCarryOutNumber;
    }

    /**
     * 반출상세번호 설정
     * @param number 반출상세번호
     */
    public void setCarryOutDetailNumber(int number) {
        mCarryOutDetailNumber = number;
    }

    /**
     * 반출상세번호 반환
     * @return 반출상세번호
     */
    public int getCarryOutDetailNumber() {
        return mCarryOutDetailNumber;
    }

    /**
     * 반출주문번호 설정
     * @param number 반출주문번호
     */
    public void setCarryOutOrderNumber(String number) {
        mCarryOutOrderNumber = number;
    }

    /**
     * 반출주문번호 반환
     * @return 반출주문번호
     */
    public String getCarryOutOrderNumber() {
        return mCarryOutOrderNumber;
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
     * 검수수량 설정
     * @param count 검수수량
     */
    public void setCheckCount(int count) {
        mCheckCount = count;
    }

    /**
     * 검수수량 반환
     * @return 검수수량
     */
    public int getCheckCount() {
        return mCheckCount;
    }

    /**
     * 피킹수량 설정 - PLT
     * @param count 피킹수량
     */
    public void setPickingPltCount(int count) {
        mPickingPltCount = count;
    }

    /**
     * 피킹수량 반환 - PLT
     * @return 피킹수량
     */
    public int getPickingPltCount() {
        return mPickingPltCount;
    }

    /**
     * 피킹수량 설정 - BOX
     * @param count 피킹수량
     */
    public void setPickingBoxCount(int count) {
        mPickingBoxCount = count;
    }

    /**
     * 피킹수량 반환 - BOX
     * @return 피킹수량
     */
    public int getPickingBoxCount() {
        return mPickingBoxCount;
    }

    /**
     * 피킹수량 설정 - EA
     * @param count 피킹수량
     */
    public void setPickingEaCount(int count) {
        mPickingEaCount = count;
    }

    /**
     * 피킹수량 반환 - EA
     * @return 피킹수량
     */
    public int getPickingEaCount() {
        return mPickingEaCount;
    }

    /**
     * 제품 PLT ID 설정
     * @param plt_id 제품 PLT ID
     */
    public void setPltId(String plt_id) {
        mPltId = plt_id;
    }

    /**
     * 제품 PLT ID 반환
     * @return 제품 PLT ID
     */
    public String getPltId() {
        return mPltId;
    }

    /**
     * 제품 PLT ID 스캔 설정
     * @param scan 스캔
     */
    public void setPltIdScan(String scan) {
        mPltIdScan = scan;
    }

    /**
     * 제품 PLT ID 스캔 반환
     * @return 스캔
     */
    public String getPltIdScan() {
        return mPltIdScan;
    }

    /**
     * 제품상태 설정
     * @param lot 제품상태
     */
    public void setManufactureLot(String lot) {
        mManufactureLot = lot;
    }

    /**
     * 제품상태 반환
     * @return 제품상태
     */
    public String getManufactureLot() {
        return mManufactureLot;
    }

    /**
     * 로트속성 설정
     * @param lot 로트속성
     */
    public void setLot1(String lot) {
        mLotAttribute1 = lot;
    }

    /**
     * 로트속성 반환
     * @return 로트속성
     */
    public String getLot1() {
        return mLotAttribute1;
    }

    /**
     * 로트속성 설정
     * @param lot 로트속성
     */
    public void setLot2(String lot) {
        mLotAttribute2 = lot;
    }

    /**
     * 로트속성 반환
     * @return 로트속성
     */
    public String getLot2() {
        return mLotAttribute2;
    }

    /**
     * 로트속성 설정
     * @param lot 로트속성
     */
    public void setLot3(String lot) {
        mLotAttribute3 = lot;
    }

    /**
     * 로트속성 반환
     * @return 로트속성
     */
    public String getLot3() {
        return mLotAttribute3;
    }

    /**
     * 로트속성 설정
     * @param lot 로트속성
     */
    public void setLot4(String lot) {
        mLotAttribute4 = lot;
    }

    /**
     * 로트속성 반환
     * @return 로트속성
     */
    public String getLot4() {
        return mLotAttribute4;
    }

    /**
     * 로트속성 설정
     * @param lot 로트속성
     */
    public void setLot5(String lot) {
        mLotAttribute5 = lot;
    }

    /**
     * 로트속성 반환
     * @return 로트속성
     */
    public String getLot5() {
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
     * 제조일자 설정
     * @return 제조일자
     */
    public String getMakeDate() {
        return mMakeDate;
    }

    /**
     * 유통일자 설정
     * @param date 유통일자
     */
    public void setDistributionDate(String date) {
        mDistributionDate = date;
    }

    /**
     * 유통일자 설정
     * @return 유통일자
     */
    public String getDistributionDate() {
        return mDistributionDate;
    }

    /**
     * 반출 사유 설정
     * @param reason 반출 사유
     */
    public void setCarryOutReason(String reason) {
        mReason = reason;
    }

    /**
     * 반출 사유 반환
     * @return 반출 사유
     */
    public String getCarryOutReason() {
        return mReason;
    }

    /**
     * 제품 위치 설정
     * @param location 제품위치
     */
    public void setLocation(String location) {
        mLocation = location;
    }

    /**
     * 제품 위치 반환
     * @return 제품위치
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * 제품 위치 스캔 설정
     * @param scan 스캔
     */
    public void setLocScan(String scan) {
        mLocScan = scan;
    }

    /**
     * 제품위치 스캔 반환
     * @return 스캔
     */
    public String getLocScan() {
        return mLocScan;
    }

    /**
     * 확정 상태 설정
     * @param state 확정상태
     */
    public void setConfirmState(int state) {
        mConfirmState = state;
    }

    /**
     * 확정상태 반환
     * @return 확정상태
     */
    public int getConfirmState() {
        return mConfirmState;
    }
}
