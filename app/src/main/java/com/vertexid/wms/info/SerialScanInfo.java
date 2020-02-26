package com.vertexid.wms.info;

import org.json.JSONArray;

import java.io.Serializable;

/**
 * 시리얼 스캔 정보 클래스
 */
public class SerialScanInfo extends Info implements Serializable {
    public static final int NONE = 0;
    public static final int SELECT = NONE + 1;
    public static final int CONFIRM_COMPLETE = SELECT + 1;

    /** 입/출고번호 */
    private String mNumber;
    /** 제품 */
    private String mGoods;
    /** 제품명 */
    private String mGoodsName;
    /** 공급처 */
    private String mSupplier;
    /** 배송처 */
    private String mDelivery;
    /** 입/출고유형 */
    private String mCategory;
    /** 파렛트 ID */
    private String mPltId;
    /** 검수수량 */
    private int mCheckCount;
    /** 스캔수량 */
    private int mScanCount;
    /** 시리얼번호 */
    private String mSerialNumber;
    /** 입/출고일자 */
    private String mDate;
    /** 입/출고 상세번호*/
    private String mDetailNumber;
    /** LOTID */
    private String mLotId;

    /** 선택여부 */
    private boolean mIsChecked;
    /** 확정 상태 */
    private int mConfirmState;

    /** 스캔저장목록 */
    private JSONArray mDtGrid = null;

    /**
     * 스캔저장목록 설정
     * @param dt_grid
     */
    public void setDtGrid(JSONArray dt_grid) {
        mDtGrid = dt_grid;
    }

    /**
     * 스캔저장목록 반환
     * @return mDtGrid
     */
    public JSONArray getDtGrid() {
        return mDtGrid;
    }

    /**
     * PLT ID 설정
     * @param plt_id plt id
     */
    public void setPltId(String plt_id) {
        mPltId = plt_id;
    }

    /**
     * PLT ID 반환
     * @return plt id
     */
    public String getPltId() {
        return mPltId;
    }

    /**
     * 검수 수량 설정
     * @param count 검수 수량
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
     * 스캔 수량 설정
     * @param count 스캔 수량
     */
    public void setScanCount(int count) {
        mScanCount = count;
    }

    /**
     * 스캔 수량 반환
     * @return 스캔 수량
     */
    public int getScanCount() {
        return mScanCount;
    }

    /**
     * 시리얼 번호 설정
     * @param serial_number 시리얼 번호
     */
    public void setSerialNumber(String serial_number) {
        mSerialNumber = serial_number;
    }

    /**
     * 시리얼 번호 반환
     * @return 시리얼 번호
     */
    public String getSerialNumber() {
        return mSerialNumber;
    }

    /**
     * 입/출고일자 설정
     * @param date 입/출고일자
     */
    public void setDate(String date) {
        mDate = date;
    }

    /**
     * 입/출고일자 반환
     * @return 입/출고일자
     */
    public String getDate() {
        return mDate;
    }

    /**
     * 입/출고 분류 설정
     * @param category 입/출고분류
     */
    public void setCategory(String category) {
        mCategory = category;
    }

    /**
     * 입/출고분류 반환
     * @return 입/출고분류
     */
    public String getCategory() {
        return mCategory;
    }

    /**
     * 입/출고번호 설정
     * @param number 입/출고번호
     */
    public void setNumber(String number) {
        mNumber = number;
    }

    /**
     * 입/출고번호 반환
     * @return 입/출고번호
     */
    public String getNumber() {
        return mNumber;
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
     * 배송지 설정
     * @param delivery 배송지
     */
    public void setDelivery(String delivery) {
        mDelivery = delivery;
    }

    /**
     * 배송지 반환
     * @return 배송지
     */
    public String getDelivery() {
        return mDelivery;
    }

    /**
     * 제품 설정
     * @param goods 제품 설정
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
     * 입/출고 상세번호 설정
     * @param detail_number
     */
    public void setDetailNumber(String detail_number) {
        mDetailNumber = detail_number;
    }

    /**
     * 입/출고 상세번호 반환
     * @return mDetailNumber 입/출고 상세번호
     */
    public String getDetailNumber() {
        return mDetailNumber;
    }

    /**
     * LOTID 설정
     * @param lotId
     */
    public void setLotId(String lotId) {
        mLotId = lotId;
    }

    /**
     * LOTID 반환
     * @return mLotId LOTID
     */
    public String getLotId() {
        return mLotId;
    }

    /**
     * 삭제 체크 박스 선택 유무 설정
     * @param is_checked true : 체크박스 체크 상태, false : 체크박스 체크 해제 상태
     */
    public void setRemoveChecked(boolean is_checked) {
        mIsChecked = is_checked;
    }

    /**
     * 삭제 체크 박스 선택 유무 반환
     * @return true : 체크박스 체크 상태, false : 체크박스 체크 해제 상태
     */
    public boolean isRemoveChecked() {
        return mIsChecked;
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
