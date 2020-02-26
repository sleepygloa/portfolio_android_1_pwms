package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 반입 관련 정보
 */
public class CarryInInfo extends Info implements Serializable {
    public static final int STATE_NONE = 0;
    public static final int STATE_SELECT = STATE_NONE + 1;
    public static final int STATE_CHECKED = STATE_SELECT + 1;
    public static final int STATE_CONFIRM = STATE_CHECKED + 1;

    /** 제품코드 */
    private String mGoodsCode;
    /** 제품명 */
    private String mGoodsName;
    /** 배송처 */
    private String mDelivery;
    /** 승인수량 */
    private int mApprovalCount;
    /** 입수 */
    private int mAcquire;
    /** 지시수량 */
    private int mOrderCount;
    /** 지시수량 - BOX */
    private int mOrderBoxCount;
    /** 지시수량 - EA */
    private int mOrderEaCount;
    /** 검수수량 */
    private int mCheckCount;
    /** 검수수량 - BOX */
    private int mCheckBoxCount;
    /** 검수수량 - EA */
    private int mCheckEaCount;
    /** 제품상태 */
    private String mGoodsState;
    /** 제품위치 */
    private String mLocation;
    /** 제품적치위치 */
    private String mPileUpLocation;
    /** 파렛트ID */
    private String mPltId;
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
    /** 반입번호 */
    private String mCarryInNumber;
    /** 반입상세번호 */
    private int mCarryInDetailNumber;
    /** 반입지시번호 */
    private String mCarryInOrderNumber;
    /** 배송처명 */
    private String mDeliveryName;
    private boolean mIsChecked;

    /** 진행상태 */
    private int mProcessState;

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
     * 반입번호 설정
     * @param number 반입번호
     */
    public void setCarryInNumber(String number) {
        mCarryInNumber = number;
    }

    /**
     * 반입번호 반환
     * @return 반입번호
     */
    public String getCarryInNumber() {
        return mCarryInNumber;
    }

    /**
     * 반입상세번호 설정
     * @param number 반입상세번호
     */
    public void setCarryInDetailNumber(int number) {
        mCarryInDetailNumber = number;
    }

    /**
     * 반입상세번호 반환
     * @return 반입상세번호
     */
    public int getCarryInDetailNumber() {
        return mCarryInDetailNumber;
    }

    /**
     * 반입상세번호 설정
     * @param number 반입상세번호
     */
    public void setCarryInOrderNumber(String number) {
        mCarryInOrderNumber = number;
    }

    /**
     * 반입상세번호 반환
     * @return 반입상세번호
     */
    public String getCarryInOrderNumber() {
        return mCarryInOrderNumber;
    }

    /**
     * 배송처 설정
     * @param delivery 배송처
     */
    public void setDelivery(String delivery) {
        mDelivery = delivery;
    }

    /**
     * 배송처 반환
     * @return 배송처
     */
    public String getDelivery() {
        return mDelivery;
    }

    /**
     * 배송처 설정
     * @param delivery 배송처
     */
    public void setDeliveryName(String delivery) {
        mDeliveryName = delivery;
    }

    /**
     * 배송처 반환
     * @return 배송처
     */
    public String getDeliveryName() {
        return mDeliveryName;
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
     * 검수수량 설정 - BOX
     * @param count 검수수량
     */
    public void setCheckBoxCount(int count) {
        mCheckBoxCount = count;
    }

    /**
     * 검수수량 반환 - BOX
     * @return 검수수량
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
     * 검수수량 반환 - EA
     * @return 검수수량
     */
    public int getCheckEaCount() {
        return mCheckEaCount;
    }

    /**
     * 지시수량 설정
     * @param check_count 지시수량
     */
    public void setOrderCount(int check_count) {
        mOrderCount = check_count;
    }

    /**
     * 지시수량 반환
     * @return 지시수량
     */
    public int getOrderCount() {
        return mOrderCount;
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
     * 입수설정
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
     * 제품상태 설정
     * @param plt_id 제품상태
     */
    public void setPltId(String plt_id) {
        mPltId = plt_id;
    }

    /**
     * 제품상태 반환
     * @return 제품상태
     */
    public String getPltId() {
        return mPltId;
    }

    /**
     * 제품 위치 설정
     * @param location 제품 위치
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
     * 제품적치위치 설정
     * @param location 제품적치위치
     */
    public void setPileUpLocation(String location) {
        mPileUpLocation = location;
    }

    /**
     * 제품적치위치 반환
     * @return 제품적치위치
     */
    public String getPileUpLocation() {
        return mPileUpLocation;
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
     * 진행 상태 설정
     * @param state 상태
     */
    public void setProcessState(int state) {
        mProcessState = state;
    }

    /**
     * 진행 상태 반환
     * @return 검수 상태
     */
    public int getProcessState() {
        return mProcessState;
    }

    public void setIsChecked(boolean is_checked) {
        mIsChecked = is_checked;
    }

    public boolean isChecked() {
        return mIsChecked;
    }
}
