package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 입하 검수 관련 정보 클래스
 */
public class ArrivalInfo extends Info implements Serializable {
    public static final int NONE = 0;
    public static final int SELECT = NONE + 1;
    public static final int CHECK_COMPLETE = SELECT + 1;
    public static final int CONFIRM_COMPLETE = CHECK_COMPLETE + 1;

    /** 제품상태 - 정상 */
    public static final int GOODS_STATE_NORMAL = 0;
    /** 제품상태 - 불량 */
    public static final int GOODS_STATE_BAD = GOODS_STATE_NORMAL + 1;

    /** 제품 */
    private String mGoods = "";
    /** 제품명 */
    private String mGoodsName = "";
    /** 입고 번호 */
    private String mWareHouseNumber = "";
    /** 공급처 */
    private String mSuupplier = "";
    /** 입수 */
    private int mAcquireCount;
    /** 승인수량 */
    private int mApprovalCount = 0;
    /** 검수수량 */
    private int mCheckCount = 0;
    /** 제품상태 */
    private int mGoodsState = GOODS_STATE_NORMAL;
    /** 파렛트ID */
    private String mPalletID = "";
    /** 제조 LOT */
    private String mManufactureLot = "";
    /** LOT 속성1 */
    private String mLotAttribute1 = "";
    /** LOT 속성2 */
    private String mLotAttribute2 = "";
    /** LOT 속성3 */
    private String mLotAttribute3 = "";
    /** LOT 속성4 */
    private String mLotAttribute4 = "";
    /** LOT 속성5 */
    private String mLotAttribute5 = "";
    /** 제조일자 */
    private String mMakeDate = "";
    /** 유통일자 */
    private String mDistributionDate = "";

    /** 입하 검수 상태 */
    private int mCheckState = NONE;
    /** 리스트에서 선택여부 */
    private boolean mIsSelected;
    /** 확정 여부 */
    private boolean mIsConfirmed;

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
     * 입고번호 설정
     * @param number 입고번호
     */
    public void setWareHouseNumber(String number) {
        mWareHouseNumber = number;
    }

    /**
     * 입고번호 반환
     * @return 입고번호
     */
    public String getWareHouseNumber() {
        return mWareHouseNumber;
    }

    /**
     * 공급처 설정
     * @param supplier 공급처
     */
    public void setSuupplier(String supplier) {
        mSuupplier = supplier;
    }

    /**
     * 공급처 반환
     * @return 공급처
     */
    public String getSuupplier() {
        return mSuupplier;
    }

    /**
     * 입수 설정
     * @param count 입수
     */
    public void setAcquireCount(int count) {
        mAcquireCount = count;
    }

    /**
     * 입수반환
     * @return 입수
     */
    public int getAcquireCount() {
        return mAcquireCount;
    }

    /**
     * 승인 수량 설정
     * @param approval_count 승인수량
     */
    public void setApprovalCount(int approval_count) {
        mApprovalCount = approval_count;
    }

    /**
     * 승인 수량 반환
     * @return 승인 수량
     */
    public int getApprovalCount() {
        return mApprovalCount;
    }

    /**
     * 검수 수량 설정
     * @param check_count 승인수량
     */
    public void setCheckCount(int check_count) {
        mCheckCount = check_count;
    }

    /**
     * 검수 수량 반환
     * @return 검수 수량
     */
    public int getCheckCount() {
        return mCheckCount;
    }

    /**
     * 제품상태 설정
     * @param state 제품상태
     */
    public void setGoodsState(int state) {
        mGoodsState = state;
    }

    /**
     * 제품상태 반환
     * @return 제품상태
     */
    public int getGoodsState() {
        return mGoodsState;
    }

    /**
     * 입하검수 상태 설정
     * @param state 입하검수 상태
     */
    public void setArrivalCheckState(int state) {
        mCheckState = state;
    }

    /**
     * 입하검수 상태 반환
     * @return 입하검수 상태
     */
    public int getArrivalCheckState() {
        return mCheckState;
    }

    /**
     * 파렛트 ID 설정
     * @param plt_id 파렛트 ID
     */
    public void setPltId(String plt_id) {
        mPalletID = plt_id;
    }

    /**
     * 파렛트 ID 반환
     * @return 파렛트 ID
     */
    public String getPltId() {
        return mPalletID;
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
     * 리스트에서 선택 유무 설정
     * @param is_selected true : 선택, false : 선택해제
     */
    public void setIsSelected(boolean is_selected) {
        mIsSelected = is_selected;
    }

    /**
     * 리스트에서 선택 유무 반환
     * @return true : 선택, false : 선택해제
     */
    public boolean isSelected() {
        return mIsSelected;
    }

    /**
     * 확정 여부 설정
     * @param is_confirmed true : 확정, false : 미 확정
     */
    public void setIsConfirmed(boolean is_confirmed) {
        mIsConfirmed = is_confirmed;
    }

    /**
     * 확정 여부 반환
     * @return true : 확정, false : 미 확정
     */
    public boolean isConfirmed() {
        return mIsConfirmed;
    }
}
