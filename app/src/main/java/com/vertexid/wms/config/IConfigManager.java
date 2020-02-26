package com.vertexid.wms.config;

import com.vertexid.wms.info.CodeInfo;

import java.util.ArrayList;

/**
 * 동작에 필요한 내용 설정을 위한 외부 공개 API
 */
public interface IConfigManager {
    /**
     * 모든 설정들을 초기화한다
     */
    public void clear();

    /**
     * 사용자 ID를 설정한다.
     * @param user_id 사용자 ID
     */
    public void setUserId(String user_id);

    /**
     * 사용자 ID를 반환한다.
     * @return 사용자 ID
     */
    public String getUserId();

    /**
     * 사용자 비밀번호를 설정한다.
     * @param password 사용자 비밀번호
     */
    public void setPassword(String password);

    /**
     * 사용자 비밀번호를 반환한다.
     * @return 사용자 비밀번호
     */
    public String getPassword();

    /**
     * 사용자 이름을 설정한다.
     * @param name 사용자 이름
     */
    public void setUserName(String name);

    /**
     * 사용자 이름을 반환한다.
     * @return 사용자 이름
     */
    public String getUserName();

    /**
     * 사용자가 속한 회사 정보를 설정한다.
     * @param info 사용자가 속한 회사 정보
     */
    public void setCompanyInfo(CodeInfo info);

    /**
     * 사용자가 속한 회사 정보를 반환한다.
     * @return 사용자가 속한 회사 정보
     */
    public CodeInfo getCompanyInfo();

    /**
     * 사용자가 속한 센터 정보를 설정한다.
     * @param info 사용자가 속한 정보
     */
    public void setCenterInfo(CodeInfo info);

    /**
     * 사용자가 속한 센터 정보를 반환한다.
     * @return 사용자가 속한 정보
     */
    public CodeInfo getCenterInfo();

    /**
     * 사용자의 고객사 정보를 설정한다.
     * @param info 고객사 정보
     */
    public void setCustomerInfo(CodeInfo info);

    /**
     * 사용자의 고객사 정보를 반환한다.
     * @return 고객사 정보
     */
    public CodeInfo getCustomerInfo();

    /**
     * 회사정보, 고객정보, 물류센터 정보와 같은<br>
     * 코드와 그 코드와 매핑된 정보들의 목록을 설정
     * @param code_type 코드 목록 타입
     * @param list 코드와 그 코드와 매핑된 정보 목록
     */
    public void setCodeList(int code_type, ArrayList<CodeInfo> list);

    /**
     * 코드와 그 코드와 매핑된 정보들의 목록을 반환<br>
     *     회사정보, 고객정보, 물류센터 정보, 재고 실사유형, 제품상태, 출고상태, 웨이브 상태, 반입/반출 유형,
     * @param code_type 코드 목록 타입
     * @return 코드와 그 코드와 매핑된 정보 목록
     */
    public ArrayList<CodeInfo> getCodeList(int code_type);
}
