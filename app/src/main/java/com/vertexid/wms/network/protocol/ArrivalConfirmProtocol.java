package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.WareHouseInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 입하검수 확정 요청 프로토콜 생성
 */
public class ArrivalConfirmProtocol {
    private WareHouseInfo mInfo = null;
    private JSONArray mJsonArray = null;

    public ArrivalConfirmProtocol(WareHouseInfo info, JSONArray json_array) {
        mInfo = info;
        mJsonArray = json_array;
    }

    /**
     * 입하 검수 확정 요청을 처리하는 WAS 메소드 반환
     * @return 입하 검수 확정 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/inbound/inboundExamPda/updateIbConfirmExamPda";
    }

    /**
     * 입하 검수 확정 요청 내용 반환
     * @return JSON 형식의 문자열.
     */
    public String getReqPayload() {
        if (mInfo == null) {
            return null;
        }

        JSONObject object = new JSONObject();

        try {
            object.put(NetworkParam.COMPANY_CODE, mInfo.getCompanyCode());
            object.put(NetworkParam.CENTER_CODE_PRIOORD, mInfo.getCenterCode());
            object.put(NetworkParam.CUSTOMER_CODE_PRIOORD, mInfo.getCustomerCode());
            object.put(NetworkParam.USER_ID, mInfo.getUserId());

            object.put(NetworkParam.ARRAY_LIST, mJsonArray);
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
