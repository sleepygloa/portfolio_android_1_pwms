package com.vertexid.wms.scrn.takeout.info;

import com.vertexid.wms.info.Info;

import java.io.Serializable;

/**
 * 출고번호 조회 관련 정보 클래스
 */
public class TakeOutNumberInfo extends Info implements Serializable {
    /** 출고번호 */
    private String mTakeOutNumber;
    /** 배송처 */
    private String mDestination;

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
     * 배송처 설정
     * @param destination 배송처
     */
    public void setDestination(String destination) {
        mDestination = destination;
    }

    /**
     * 배송처 반환
     * @return 배송처
     */
    public String getDestination() {
        return mDestination;
    }
}
