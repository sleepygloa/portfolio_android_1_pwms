package com.vertexid.wms.scrn.takeout.info;

import com.vertexid.wms.info.Info;

import java.io.Serializable;

/**
 * 웨이브 번호 관련 정보 클래스
 */
public class WaveNumberInfo extends Info implements Serializable {
    /** 웨이브 번호 */
    private String mWaveNumber;
    /** 웨이브 상태 */
    private String mWaveState;
    /** 웨이브 생성일자 */
    private String mDate;
    /** 기준번호 */
    private String mStandardNumber;
    /** 기준설명 */
    private String mStandardText;

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
     * 웨이브 상태 설정
     * @param state 웨이브 상태
     */
    public void setWaveState(String state) {
        mWaveState = state;
    }

    /**
     * 웨이브 상태 반환
     * @return 웨이브 상태
     */
    public String getWaveState() {
        return mWaveState;
    }

    /**
     * 웨이브 생성일자 설정
     * @param date 웨이브 생성일자
     */
    public void setDate(String date) {
        mDate = date;
    }

    /**
     * 웨이브 생성일자 반환
     * @return 웨이브 생성일자
     */
    public String getDate() {
        return mDate;
    }

    /**
     * 기준번호 설정
     * @param number 기준번호
     */
    public void setStandardNumber(String number) {
        mStandardNumber = number;
    }

    /**
     * 기준번호 반환
     * @return 기준번호
     */
    public String getStandardNumber() {
        return mStandardNumber;
    }

    /**
     * 기준설명 설정
     * @param text 기준설명
     */
    public void setStandardText(String text) {
        mStandardText = text;
    }

    /**
     * 기준설명 반환
     * @return 기준설명
     */
    public String getStandardText() {
        return mStandardText;
    }
}
