package com.vertexid.wms.network.protocol;

import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 로그인 관련 정보 생성 클래스
 */
public class LoginProtocol {
    private String mId = "";
    private String mPassword = "";

    public LoginProtocol(String id, String password) {
        mId = id;
        mPassword = password;
    }

    /**
     * 로그인 요청을 처리하는 WAS 메소드 반환
     * @return 로그인 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/sign/pda_login";
    }

    /**
     * 로그인 요청 내용 반환
     * @return JSON 형식의 문자열.
     */
    public String getReqPayload() {
        JSONObject object = new JSONObject();

        try {
            object.put(NetworkParam.USER_ID, mId);
            object.put(NetworkParam.USER_PASS, mPassword);
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
