package com.vertexid.wms.info;

/**
 * 각 정보 클래스의 공통 속성을 모아놓은 부모 정보 클래스
 */
public class Info {
    /** 사용자 ID */
    private String mUserId;
    /** 회사코드 */
    private String mCompanyCode;
    /** 고객사 코드 */
    private String mCustomerCode;
    /** 물류센터 코드 */
    private String mCenterCode;

    /**
     * 사용자 ID 설정
     * @param user_id 사용자 ID
     */
    public void setUserId(String user_id) {
        mUserId = user_id;
    }

    /**
     * 사용자 ID 반환
     * @return 사용자 ID
     */
    public String getUserId() {
        return mUserId;
    }

    /**
     * 회사 코드 설정
     * @param code 회사 코드
     */
    public void setCompanyCode(String code) {
        mCompanyCode = code;
    }

    /**
     * 회사 코드 반환
     * @return 회사 코드
     */
    public String getCompanyCode() {
        return mCompanyCode;
    }

    /**
     * 고객사 코드 설정
     * @param code 고객사 코드
     */
    public void setCustomerCode(String code) {
        mCustomerCode = code;
    }

    /**
     * 고객사 코드 반환
     * @return 고객사 코드
     */
    public String getCustomerCode() {
        return mCustomerCode;
    }

    /**
     * 물류센터 코드 설정
     * @param code 물류센터 코드
     */
    public void setCenterCode(String code) {
        mCenterCode = code;
    }

    /**
     * 물류센터 코드 반환
     * @return 물류센터 코드
     */
    public String getCenterCode() {
        return mCenterCode;
    }
}
