package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 출고피킹 관련 정보 클래스
 */

public class TakeOutInfo extends Info implements Serializable {
    public static final int NONE = 0;
    public static final int SELECT = NONE + 1;
    public static final int CHECK_COMPLETE = SELECT + 1;
    public static final int CONFIRM_COMPLETE = CHECK_COMPLETE + 1;

    /** 피킹 LOC*/
    private String mPickingLoc;
    /** 제품 */
    private String mGoods;
    /** 제품명 */
    private String mGoodsName;
    /** 파렛트ID */
    private String mPalletID;
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
    /**지시 수량 */
    private int mOrderCount;
    /**지시수량 PLT */
    private int mOrderPltCount;
    /**지시수량 BOX */
    private int mOrderBoxCount;
    /**지시수량 EA */
    private int mOrderEaCount;
    /**피킹 수량*/
    private int mPickingCount;
    /**피킹 수량 PLT */
    private int mPickingPltCount;
    /**피킹 수량 BOX */
    private int mPickingBoxCount;
    /**피킹 수량 EA */
    private int mPickingEaCount;
    /**미출고 사유 */
    private String mReason;
    /**배송처 */
    private String mDeliveryAddress;
    /**배송처 코드*/
    private String mDeliveryAddressCode;
    /** 출고번호*/
    private String mTakeOutNumber;
    /** 출고지시번호 */
    private String mOrderNumber;
    /** 차량번호*/
    private String mCarNum;
    /** 상차파렛트*/
    private String mLoadingPlt;
    /** 웨이브번호*/
    private String mWaveNumber;
    /** 기준번호*/
    private String mStandardNum = "";
    /** 기준설명*/
    private String mStandardDesc = "";

    /** 출고일자*/
    private String mTakeOutDate = "";
    /** 출고구분*/
    private String mCategory = "";
    /**출고상세번호*/
    private int mTakeOutDetailNum;

    /**피킹파렛트*/
    private String mPickingPltId ="";

    private int mProcessState = NONE;

    /**
     * 출고상세번호 반환
     * @return 출고상세번호
     */
    public int getTakeOutDetailNum() {
        return mTakeOutDetailNum;
    }

    /**
     * 출고상세번호 설정
     * @param number 출고상세번호
     */
    public void setTakeOutDetailNum(int number) {
        mTakeOutDetailNum = number;
    }


    /**
     * 출고일자 반환
     * @return 출고일자
     */
    public String getTakeoutDate() {
        return mTakeOutDate;
    }

    /**
     * 출고일자 설정
     * @param date 출고일자
     */
    public void setTakeOutDate(String date) {
        mTakeOutDate = date;
    }

    /**
     * 출고구분 반환
     * @return 출고구분
     */
    public String getTakeOutCategory() {
        return mCategory;
    }

    /**
     * 출고구분 설정
     * @param category 출고구분
     */
    public void setTakeOutCategory(String category) {
        mCategory = category;
    }

    /**
     * 피킹LOC 반환
     * @return 피킹LOC
     */
    public String getPickingLoc() {
        return mPickingLoc;
    }

    /**
     * 피킹LOC 설정
     * @param location 피킹LOC
     */
    public void setPickingLoc(String location) {
        mPickingLoc = location;
    }

    /**
     * 제품 반환
     * @return 제품
     */
    public String getGoods() {
        return mGoods;
    }

    /**
     * 제품코드 제품
     * @param goods 제품
     */
    public void setGoods(String goods) {
        mGoods = goods;
    }

    /**
     * 제품명 반환
     * @return 제품명
     */
    public String getGoodsName() {
        return mGoodsName;
    }

    /**
     * 제품명 설정
     * @param name 제품명
     */
    public void setGoodsName(String name) {
        mGoodsName = name;
    }

    /**
     * 파레트ID 반환
     * @return 파레트ID
     */
    public String getPalletID() {
        return mPalletID;
    }

    /**
     * 파렛트ID 설정
     * @param plt_id 파렛트ID
     */
    public void setPalletID(String plt_id) {
        mPalletID = plt_id;
    }

    /**
     * 제조LOT 반환
     * @return 제조LOT
     */
    public String getManufactureLot() {
        return mManufactureLot;
    }

    /**
     * 제조LOT 설정
     * @param lot 제조LOT
     */
    public void setManufactureLot(String lot) {
        mManufactureLot = lot;
    }

    /**
     * 제조속성1 반환
     * @return 제조속성1
     */
    public String getLotAttribute1() {
        return mLotAttribute1;
    }

    /**
     * 제조속성1 설정
     * @param lot 제조속성1
     */
    public void setLotAttribute1(String lot) {
        mLotAttribute1 = lot;
    }

    /**
     * 제조속성2 반환
     * @return 제조속성2
     */
    public String getLotAttribute2() {
        return mLotAttribute2;
    }

    /**
     * 제조속성2 설정
     * @param lot 제조속성2
     */
    public void setLotAttribute2(String lot) {
        mLotAttribute2 = lot;
    }

    /**
     * 제조속성3 반환
     * @return 제조속성3
     */
    public String getLotAttribute3() {
        return mLotAttribute3;
    }

    /**
     * 제조속성3 설정
     * @param lot 제조속성3
     */
    public void setLotAttribute3(String lot) {
        mLotAttribute3 = lot;
    }

    /**
     * 제조속성4 반환
     * @return 제조속성4
     */
    public String getLotAttribute4() {
        return mLotAttribute4;
    }

    /**
     * 제조속성4 설정
     * @param lot 제조속성4
     */
    public void setLotAttribute4(String lot) {
        mLotAttribute4 = lot;
    }

    /**
     * 제조속성5 반환
     * @return 제조속성5
     */
    public String getLotAttribute5() {
        return mLotAttribute5;
    }

    /**
     * 제조속성5 설정
     * @param lot 제조속성5
     */
    public void setLotAttribute5(String lot) {
        mLotAttribute5 = lot;
    }

    /**
     * 제조일자 반환
     * @return 제조일자
     */
    public String getMakeDate() {
        return mMakeDate;
    }

    /**
     * 제조일자 설정
     * @param date 제조일자
     */
    public void setMakeDate(String date) {
        mMakeDate = date;
    }

    /**
     * 유통일자 반환
     * @return 유통일자
     */
    public String getDistributionDate() {
        return mDistributionDate;
    }

    /**
     * 유통일자 설정
     * @param date 유통일자
     */
    public void setDistributionDate(String date) {
        mDistributionDate = date;
    }

    /**
     * 지시수량 PLT 반환
     * @return 지시수량 PLT
     */
    public int getDirectionPlt() {
        return mOrderPltCount;
    }

    /**
     * 지시수량 PLT 설정
     * @param count 지시수량 PLT
     */
    public void setDirectionPlt(int count) {
        mOrderPltCount = count;
    }

    /**
     * 지시수량 BOX 반환
     * @return 지시수량 BOX
     */
    public int getDirectionBox() {
        return mOrderBoxCount;
    }

    /**
     * 지시수량 BOX 설정
     * @param count 지시수량 BOX
     */
    public void setDirectionBox(int count) {
        mOrderBoxCount = count;
    }

    /**
     * 지시수량 EA 반환
     * @return 지시수량 EA
     */
    public int getDirectionEa() {
        return mOrderEaCount;
    }

    /**
     * 지시수량 EA 설정
     * @param count 지시수량 EA
     */
    public void setDirectionEa(int count) {
        mOrderEaCount = count;
    }

    /**
     * 피킹수량 PLT 반환
     * @return 피킹수량 PLT
     */
    public int getPickingPlt() {
        return mPickingPltCount;
    }

    /**
     * 피킹수량 PLT 설정
     * @param count 피킹수량 PLT
     */
    public void setPickingPlt(int count) {
        mPickingPltCount = count;
    }

    /**
     * 피킹수량 PLT 반환
     * @return 피킹수량 PLT
     */
    public String getPickingPltId() {
        return mPickingPltId;
    }

    /**
     * 피킹수량 PLT 설정
     * @param plt_id 피킹수량 PLT
     */
    public void setPickingPltId(String plt_id) {
        mPickingPltId = plt_id;
    }

    /**
     * 피킹수량 BOX 반환
     * @return 피킹수량 BOX
     */
    public int getPickingBox() {
        return mPickingBoxCount;
    }

    /**
     * 피킹수량 BOX 설정
     * @param count 피킹수량 BOX
     */
    public void setPickingBox(int count) {
        mPickingBoxCount = count;
    }

    /**
     * 피킹수량 EA 반환
     * @return 피킹수량 EA
     */
    public int getPickingEa() {
        return mPickingEaCount;
    }

    /**
     * 피킹수량 EA 설정
     * @param count 피킹수량 EA
     */
    public void setPickingEa(int count) {
        mPickingEaCount = count;
    }

    /**
     * 사유 반환
     * @return 사유
     */
    public String getReason() {
        return mReason;
    }

    /**
     * 사유 설정
     * @param reason 사유
     */
    public void setReason(String reason) {
        mReason = reason;
    }

    /**
     * 지시수량 반환
     * @return 지시수량
     */
    public int getDirectionCount() {
        return mOrderCount;
    }

    /**
     * 지시수량 설정
     * @param count 지시수량
     */
    public void setDirectionCount(int count) {
        mOrderCount = count;
    }

    /**
     * 피킹수량 반환
     * @return 피킹수량
     */
    public int getPickingCount() {
        return mPickingCount;
    }

    /**
     * 피킹수량 설정
     * @param count 피킹수량
     */
    public void setPickingCount(int count) {
        mPickingCount = count;
    }

    /**
     * 배송처 반환
     * @return 배송처
     */
    public String getDeliveryAddress() {
        return mDeliveryAddress;
    }

    /**
     * 배송처 설정
     * @param addr 배송처
     */
    public void setDeliveryAddress(String addr) {
        mDeliveryAddress = addr;
    }

    /**
     * 배송처 코드 반환
     * @return 배송처 코드
     */
    public String getDeliveryAddressCode() {
        return mDeliveryAddressCode;
    }

    /**
     * 배송처 코드 설정
     * @param addr_code 배송처 코드
     */
    public void setDeliveryAddressCode(String addr_code) {
        mDeliveryAddressCode = addr_code;
    }

    /**
     * 출고번호 설정
     * @param number 출고번호
     */
    public void setTakeOutNum(String number) {
        mTakeOutNumber = number;
    }

    /**
     * 출고번호 반환
     * @return 출고번호
     */
    public String getTakeOutNum() {
        return mTakeOutNumber;
    }

    /**
     * 출고지시번호 설정
     * @param number 출고지시번호
     */
    public void setOrderNumber(String number) {
        mOrderNumber = number;
    }

    /**
     * 출고지시번호 반환
     * @return 출고지시번호
     */
    public String getOrderNumber() {
        return mOrderNumber;
    }

    /**
     * 차량번호 설정
     * @param number 차량번호
     */
    public void setCarNum(String number) {
        mCarNum = number;
    }

    /**
     * 차량번호 반환
     * @return 차량번호
     */
    public String getCarNum() {
        return mCarNum;
    }

    /**
     * 상차 설정
     * @param loading_plt 상차
     */
    public void setLoadingPlt(String loading_plt) {
        mLoadingPlt = loading_plt;
    }

    /**
     * 상차 반환
     * @return 상차
     */
    public String getLoadingPlt() {
        return mLoadingPlt;
    }

    /**
     * 웨이브번호 설정
     * @param number 웨이브번호
     */
    public void setWaveNum(String number) {
        mWaveNumber = number;
    }

    /**
     * 웨이브번호 반환
     * @return 웨이브번호
     */
    public String getWaveNum() {
        return mWaveNumber;
    }

    /**
     * 기준번호 설정
     * @param number 기준번호
     */
    public void setStandardNum(String number) {
        mStandardNum = number;
    }

    /**
     * 기준번호 반환
     * @return 기준번호
     */
    public String getStandardNum() {
        return mStandardNum;
    }

    /**
     * 기준설명 설정
     * @param desc 기준설명
     */
    public void setStandardDesc(String desc) {
        mStandardDesc = desc;
    }

    /**
     * 기준설명 반환
     * @return 기준설명
     */
    public String getStandardDesc() {
        return mStandardDesc;
    }

    public void setProcessState(int state) {
        mProcessState = state;
    }

    public int getProcessState() {
        return mProcessState;
    }
}
