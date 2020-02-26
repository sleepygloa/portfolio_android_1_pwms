package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 사용자의 회사, 소속 센터, 고객사의 코드 및 이름을 가진 클래스
 */
public class CodeInfo implements Serializable {
    public static final int CODE_TYPE_NONE = 0;
    /** 사용자가 속한 회사 코드 및 회사명 */
    public static final int CODE_TYPE_COMPANY = CODE_TYPE_NONE + 1;
    /** 사용자가 속한 물류센터 코드 및 물류센터 명 */
    public static final int CODE_TYPE_CENTER = CODE_TYPE_COMPANY + 1;
    /** 고객사 코드 및 고객사명 */
    public static final int CODE_TYPE_CUSTOMER = CODE_TYPE_CENTER + 1;
    /** 제품상태 코드 및 제품상태 명 */
    public static final int CODE_TYPE_GOODS_STATE = CODE_TYPE_CUSTOMER + 1;
    /** 입고구분 코드 및 입고구분 명 */
    public static final int CODE_TYPE_WAREHOUSE = CODE_TYPE_GOODS_STATE + 1;
    /** 출고구분 코드 및 출고구분 명 */
    public static final int CODE_TYPE_TAKE_OUT = CODE_TYPE_WAREHOUSE + 1;
    /** 미출고사유 코드 및 미출고사유 명 */
    public static final int CODE_TYPE_NOT_TAKE_OUT_REASON = CODE_TYPE_TAKE_OUT + 1;
    /** 웨이브상태 코드 및 웨이브상태 명 */
    public static final int CODE_TYPE_WAVE_STATE = CODE_TYPE_NOT_TAKE_OUT_REASON + 1;
    /** 반입구분 코드 및 반입구분 명 */
    public static final int CODE_TYPE_CARRY_IN = CODE_TYPE_WAVE_STATE + 1;
    /** 반출구분 코드 및 반출구분 명 */
    public static final int CODE_TYPE_CARRY_OUT = CODE_TYPE_CARRY_IN + 1;
    /** 실사유형 코드 및 실사유형 명 */
    public static final int CODE_TYPE_EXILE = CODE_TYPE_CARRY_OUT + 1;
    /** 반출 사유 */
    public static final int CODE_TYPE_CARRY_OUT_REASON = CODE_TYPE_EXILE + 1;

    private int mType;
    /** 사용자의 회사, 소속 센터, 고객사, 제품상태의 코드 */
    private String mCode;
    /** 사용자의 회사, 소속 센터, 고객사, 제품상태의 이름 */
    private String mText;

    /**
     * 코드 정보 유형 설정
     * @param type 코드 정보 유형
     */
    public void setCodeInfoType(int type) {
        mType = type;
    }

    /**
     * 코드 정보 유형 반환
     * @return 코드 정보 유형
     */
    public int getCodeInfoType() {
        return mType;
    }

    /**
     * 사용자의 회사, 소속 센터, 고객사의 코드 설정
     * @param code 사용자의 회사, 소속 센터, 고객사의 코드
     */
    public void setCode(String code) {
        mCode = code;
    }

    /**
     * 사용자의 회사, 소속 센터, 고객사의 코드 반환
     * @return 사용자의 회사, 소속 센터, 고객사의 코드
     */
    public String getCode() {
        return mCode;
    }

    /**
     * 사용자의 회사, 소속 센터, 고객사의 이름 설정
     * @param text 사용자의 회사, 소속 센터, 고객사의 이름
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * 사용자의 회사, 소속 센터, 고객사의 이름 반환
     * @return 사용자의 회사, 소속 센터, 고객사의 이름
     */
    public String getText() {
        return mText;
    }
}
