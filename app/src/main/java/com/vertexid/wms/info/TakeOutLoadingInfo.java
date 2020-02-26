package com.vertexid.wms.info;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 출고상차 정보 클래스
 */
public class TakeOutLoadingInfo extends Info implements Serializable {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_SELECTED = STATE_NORMAL + 1;
    public static final int STATE_COMPLETED = STATE_SELECTED + 1;

    /** 웨이브 번호 */
    private String mWaveNumber;
    /** 출고번호 */
    private String mTakeOutNumber;
    /** 상세번호 */
    private int mDetailNumber;
    /**피킹번호 */
    private String mPickingNumber;
    /** 차량번호 */
    private String mCarNumber;
    /** 배송처 */
    private String mDelivery;
    /** 배송처 코드 */
    private String mDeliveryCode;

    /** 제품코드 */
    private String mGoodsCode;
    /** 제품이름 */
    private String mGoodsName;
    /** 피킹 수 */
    private int mPickingCount;
    /** 전체 피킹 수 - BOX */
    private int mPickingBoxTotalCount;
    /** 전체 피킹 수 - EA */
    private int mPickingEaTotalCount;
    /** 피킹 수 - BOX */
    private int mPickingBoxCount;
    /** 피킹 수 - EA */
    private int mPickingEaCount;
    /** 파렛트 ID */
    private String mPltId;
    /** 로트 ID */
    private String mLotId;

    private ArrayList<PltInfo> mArrayPallet;

    /** 상차처리 상태 */
    private int mLoadingState = STATE_NORMAL;
//    private ArrayList<InfoGoods> mArrayInfoGoods;

    /**
     * 웨이브 번호 설정
     * @param number 웨이브 번호
     */
    public void setWaveNumber(String number) {
        mWaveNumber = number;
    }

    /**
     * 웨이브 번호 반환
     * @return 웨이브 번호
     */
    public String getWaveNumber() {
        return mWaveNumber;
    }

    /**
     * 출고번호 설정
     * @param number 출고번호
     */
    public void setTakeOutNumber(String number) {
        mTakeOutNumber = number;
    }

    /**
     * 출고번호 반환
     * @return 출고번호
     */
    public String getTakeOutNumber() {
        return mTakeOutNumber;
    }

    /**
     * 상세번호 설정
     * @param number 상세번호
     */
    public void setDetailNumber(int number) {
        mDetailNumber = number;
    }

    /**
     * 상세번호 반환
     * @return 상세번호
     */
    public int getDetailNumber() {
        return mDetailNumber;
    }

    /**
     * 피킹번호 설정
     * @param number 피킹번호
     */
    public void setPickingNumber(String number) {
        mPickingNumber = number;
    }

    /**
     * 피킹번호 반환
     * @return 피킹번호
     */
    public String getPickingNumber() {
        return mPickingNumber;
    }

    /**
     * 차량번호 설정
     * @param number 차량번호
     */
    public void setCarNumber(String number) {
        mCarNumber = number;
    }

    /**
     * 차량번호 반환
     * @return 차량번호
     */
    public String getCarNumber() {
        return mCarNumber;
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
     * 배송처코드 설정
     * @param delivery_code 배송처코드
     */
    public void setDeliveryCode(String delivery_code) {
        mDeliveryCode = delivery_code;
    }

    /**
     * 배송처코드 반환
     * @return 배송처 코드
     */
    public String getDeliveryCode() {
        return mDeliveryCode;
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
     * 피킹수량 설정
     * @param count 피킹수량
     */
    public void setPickingCount(int count) {
        mPickingCount = count;
    }

    /**
     * 피킹수량 반환
     * @return 피킹수량
     */
    public int getPickingCount() {
        return mPickingCount;
    }

    /**
     * 전체 피킹수량 설정 - BOX
     * @param count 피킹수량
     */
    public void setPickingBoxTotalCount(int count) {
        mPickingBoxTotalCount = count;
    }

    /**
     * 전체 피킹수량 반환 - BOX
     * @return 피킹수량
     */
    public int getPickingBoxTotalCount() {
        return mPickingBoxTotalCount;
    }

    /**
     * 전체 피킹수량 설정 - EA
     * @param count 피킹수량
     */
    public void setPickingEaTotalCount(int count) {
        mPickingEaTotalCount = count;
    }

    /**
     * 전체 피킹수량 반환 - EA
     * @return 피킹수량
     */
    public int getPickingEaTotalCount() {
        return mPickingEaTotalCount;
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
     * 파렛트 ID 설정
     * @param id 파렛트 ID
     */
    public void setPltId(String id) {
        mPltId = id;
    }

    /**
     * 파렛트 ID 반환
     * @return 파렛트 ID
     */
    public String getPltId() {
        return mPltId;
    }

    /**
     * Lot ID 설정
     * @param id Lot ID 설정
     */
    public void setLotId(String id) {
        mLotId = id;
    }

    /**
     * Lot ID 반환
     * @return Lot ID
     */
    public String getLotId() {
        return mLotId;
    }

    public void setPalletArray(ArrayList<PltInfo> array) {
        mArrayPallet = array;
    }

    public ArrayList<PltInfo> getPalletArray() {
        return mArrayPallet;
    }

//    public void setArrayInfoGoods(ArrayList<InfoGoods> array) {
//        mArrayInfoGoods = array;
//    }
//
//    public ArrayList<InfoGoods> getArrayInfoGoods() {
//        return mArrayInfoGoods;
//    }

    /**
     * 상차 처리 상태 설정
     * @param state 상차 처리 상태
     */
    public void setLoadingState(int state) {
        mLoadingState = state;
    }

    /**
     * 상차 처리 상태 반환
     * @return 상차 처리 상태
     */
    public int getLoadingState() {
        return mLoadingState;
    }

    public class PltInfo {
        /** 피킹 파렛트 ID */
        private String mPickingPltId;
        /** 상차 파렛트 ID */
        private String mLoadingPltId;
        private boolean mIsFocus;

        /**
         * 피킹 파렛트 Id 설정
         * @param plt_id 피킹 파렛트 Id
         */
        public void setPickingPltId(String plt_id) {
            mPickingPltId = plt_id;
        }

        /**
         * 피킹 파렛트 Id 반환
         * @return 피킹 파렛트 Id
         */
        public String getPickingPltId() {
            return mPickingPltId;
        }

        /**
         * 상차 파렛트 Id 설정
         * @param plt_id 상차 파렛트 Id
         */
        public void setLoadingPltId(String plt_id) {
            mLoadingPltId = plt_id;
        }

        /**
         * 상차 파렛트 Id 반환
         * @return 상차 파렛트 Id
         */
        public String getLoadingPltId() {
            return mLoadingPltId;
        }

        /**
         * 포커스 여부 설정
         * @param is_focus true : 포커스 설정, false : 포커스 비활성
         */
        public void setFocus(boolean is_focus) {
            mIsFocus = is_focus;
        }

        /**
         * 포커스 여부 반환
         * @return true : 포커스 설정, false : 포커스 비활성
         */
        public boolean isFocus() {
            return mIsFocus;
        }
    }
}
