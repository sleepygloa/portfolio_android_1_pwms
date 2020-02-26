package com.vertexid.wms.network.protocol;

import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 제품조회 요청 프로토콜 생성
 */
public class GoodsSearchProtocol {
    private String mCompanyCode;
    private String mCenterCode;

    public GoodsSearchProtocol() {
    }

    /**
     * 제품조회 요청을 처리하는 WAS 메소드 반환
     * @return 제품조회 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/sign/pda_login";
    }

    /**
     * 제품조회 요청 내용 반환
     * @return JSON 형식의 문자열.
     */
    public String getReqPayload() {
        JSONObject object = new JSONObject();

        try {
            object.put(NetworkParam.COMPANY_CODE, "");
            object.put(NetworkParam.CENTER_CODE, "");
            object.put(NetworkParam.CUSTOMER_CODE, "");
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
