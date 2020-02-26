package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 반입/반출 번호 조회 정보 클래스
 */
public class CarryNumberInfo extends Info implements Serializable {
    /** 반입/반출번호 */
    private String mCarryNumber;
    /** 공급처/배송처 */
    private String mSource;
    /** 반입/반출일자 */
    private String mCarryDate;
    /** 반입/반출구분 */
    private String mCategory;

    /**
     * 반입/반출번호 설정
     * @param number 반입/반출번호
     */
    public void setCarryNumber(String number) {
        mCarryNumber = number;
    }

    /**
     * 반입/반출번호 반환
     * @return 반입/반출번호
     */
    public String getCarryNumber() {
        return mCarryNumber;
    }

    /**
     * 공급처/배송처 설정
     * @param source 공급처/배송처
     */
    public void setSource(String source) {
        mSource = source;
    }

    /**
     * 공급처/배송처 반환
     * @return 공급처/배송처
     */
    public String getSource() {
        return mSource;
    }

    /**
     * 반입/반출일자 설정
     * @param date 반입/반출일자
     */
    public void setCarryDate(String date) {
        mCarryDate = date;
    }

    /**
     * 반입/반출일자 반환
     * @return 반입/반출일자
     */
    public String getCarryDate() {
        return mCarryDate;
    }

    /**
     * 반입/반출 구분 설정
     * @param category 반입/반출 구분
     */
    public void setCarryCategory(String category) {
        mCategory = category;
    }

    /**
     * 반입/반출 구분 반환
     * @return 반입/반출 구분
     */
    public String getCarryCategory() {
        return mCategory;
    }
}
