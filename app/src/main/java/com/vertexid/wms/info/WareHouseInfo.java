package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 입고관리 정보 클래스
 */
public class WareHouseInfo extends Info implements Serializable {
    public static final int NONE = 0;
    public static final int SELECT = NONE + 1;
    public static final int CHECK_COMPLETE = SELECT + 1;
    public static final int CONFIRM_COMPLETE = CHECK_COMPLETE + 1;

    /** 제품 */
    private String mGoods;
    /** 제품명 */
    private String mGoodsName;
    /** 입고 번호 */
    private String mNumber;
    /** 상세 입고번호 */
    private int mDetailNumber;
    /** 입고 지시번호 */
    private String mWareOrderNumber;
    /** 공급처 코드 */
    private String mSupplierCode;
    /** 공급처명 */
    private String mSupplierName;
    /** 수량 */
    private int mCount;
    /** 입수 */
    private int mAcquireCount;
    /** 승인수량 */
    private int mApprovalCount;
    /** 검수수량 */
    private int mCheckCount;
    /** 검수수량 - BOX */
    private int mCheckBoxCount;
    /** 검수수량 - EA */
    private int mCheckEaCount;
    /** 주문수량 */
    private int mOrderCount;
    /** 주문수량 - PLT */
    private int mOrderPltCount;
    /** 주문수량 - BOX */
    private int mOrderBoxCount;
    /** 주문수량 - EA */
    private int mOrderEaCount;
    /** 적치수량 */
    private int mPileUpCount;
    /** 적치수량 - PLT */
    private int mPileUpPltCount;
    /** 적치수량 - BOX */
    private int mPileUpBoxCount;
    /** 적치수량 - EA */
    private int mPileUpEaCount;
    /** 제품상태 */
    private String mGoodsState;
    /** 위치 */
    private String mLocation;
    /** 위치 확인 */
    private String mLocationConfirm;
    /** 파렛트ID */
    private String mPltId;
    /** 제조일자 */
    private String mMakeDate;
    /** 유통일자 */
    private String mDistributionDate;
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

    /** 입하 검수 상태 */
    private int mCheckState = NONE;
    /** 리스트에서 선택여부 */
    private boolean mIsSelected;

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
     * 입고 상세 번호 설정
     * @param number 입고 상세 번호
     */
    public void setWareHouseDetailNumber(int number) {
        mDetailNumber = number;
    }

    /**
     * 입고 상세 번호 반환
     * @return 입고 상세 번호
     */
    public int getWareHouseDetailNumber() {
        return mDetailNumber;
    }

    /**
     * 입고 주문 번호 설정
     * @param number 입고 주문 번호
     */
    public void setWareHouseOrderNumber(String number) {
        mWareOrderNumber = number;
    }

    /**
     * 입고 주문 번호 반환
     * @return 입고 주문 번호
     */
    public String getWareOrderNumber() {
        return mWareOrderNumber;
    }

    /**
     * 공급처 코드 설정
     * @param supplier 공급처 코드
     */
    public void setSupplier(String supplier) {
        mSupplierCode = supplier;
    }

    /**
     * 공급처 코드 반환
     * @return 공급처 코드
     */
    public String getSupplier() {
        return mSupplierCode;
    }

    /**
     * 공급처 명 설정
     * @param supplier 공급처 명
     */
    public void setSupplierName(String supplier) {
        mSupplierName = supplier;
    }

    /**
     * 공급처 명 반환
     * @return 공급처 명
     */
    public String getSupplierName() {
        return mSupplierName;
    }

    /**
     * 수량설정
     * @param count 수량
     */
    public void setCount(int count) {
        mCount = count;
    }

    /**
     * 수량반환
     * @return 수량
     */
    public int getCount() {
        return mCount;
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
     * @param count 승인수량
     */
    public void setApprovalCount(int count) {
        mApprovalCount = count;
    }

    /**
     * 승인 수량 반환
     * @return 승인 수량
     */
    public int getApprovalCount() {
        return mApprovalCount;
    }

    /**
     * 검수수량 설정
     * @param count 검수수량
     */
    public void setCheckCount(int count) {
        mCheckCount = count;
    }

    /**
     * 검수 수량 반환
     * @return 검수 수량
     */
    public int getCheckCount() {
        return mCheckCount;
    }

    /**
     * 검수수량 설정 - BOX
     * @param count 검수수량
     */
    public void setCheckBoxCount(int count) {
        mCheckBoxCount = count;
    }

    /**
     * 검수 수량 반환 - BOX
     * @return 검수 수량
     */
    public int getCheckBoxCount() {
        return mCheckBoxCount;
    }

    /**
     * 검수수량 설정 - EA
     * @param count 검수수량
     */
    public void setCheckEaCount(int count) {
        mCheckEaCount = count;
    }

    /**
     * 검수 수량 반환 - EA
     * @return 검수 수량
     */
    public int getCheckEaCount() {
        return mCheckEaCount;
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
     * 주문수량 설정
     * @param count 주문수량
     */
    public void setPileUpCount(int count) {
        mPileUpCount = count;
    }

    /**
     * 적치수량 반환
     * @return 적치수량
     */
    public int getPileUpCount() {
        return mPileUpCount;
    }

    /**
     * 적치수량 설정 - PLT
     * @param count 적치수량
     */
    public void setPileUpPltCount(int count) {
        mPileUpPltCount = count;
    }

    /**
     * 적치수량 반환 - PLT
     * @return 적치수량
     */
    public int getPileUpPltCount() {
        return mPileUpPltCount;
    }

    /**
     * 적치수량 설정 - BOX
     * @param count 적치수량
     */
    public void setPileUpBoxCount(int count) {
        mPileUpBoxCount = count;
    }

    /**
     * 적치수량 반환 - BOX
     * @return 적치수량
     */
    public int getPileUpBoxCount() {
        return mPileUpBoxCount;
    }

    /**
     * 적치수량 설정 - EA
     * @param count 적치수량
     */
    public void setPileUpEaCount(int count) {
        mPileUpEaCount = count;
    }

    /**
     * 적치수량 반환 - EA
     * @return 적치수량
     */
    public int getPileUpEaCount() {
        return mPileUpEaCount;
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
     * 위치확인 설정
     * @param location 위치
     */
    public void setLocationConfirm(String location) {
        mLocationConfirm = location;
    }

    /**
     * 위치확인 반환
     * @return 위치
     */
    public String getLocationConfirm() {
        return mLocationConfirm;
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
}
