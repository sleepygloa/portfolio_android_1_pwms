package com.vertexid.wms.network.protocol;

import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 로그아웃 관련 정보 생성 클래스
 */
public class LogOutProtocol {
    private String mId;
    private String mDeviceId;

    public LogOutProtocol(String id, String device_id) {
        mId = id;
        mDeviceId = device_id;
    }

    /**
     * 로그인 요청을 처리하는 WAS 메소드 반환
     * @return 로그인 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/sign/pda_logout";
    }

    /**
     * 로그인 요청 내용 반환
     * @return JSON 형식의 문자열.
     */
    public String getReqPayload() {
        JSONObject object = new JSONObject();

        try {
            object.put(NetworkParam.USER_ID, mId);
            object.put(NetworkParam.DEVICE_ID, mDeviceId);
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
