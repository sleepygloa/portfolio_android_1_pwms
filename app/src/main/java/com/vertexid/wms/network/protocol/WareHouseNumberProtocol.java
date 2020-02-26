package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.WareHouseNumberInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 입고번호 목록 요청 프로토콜
 */
public class WareHouseNumberProtocol {
    private WareHouseNumberInfo mInfo = null;

    public WareHouseNumberProtocol(WareHouseNumberInfo info) {
        mInfo = info;
    }

    /**
     * 입고번호 목록 요청을 처리하는 WAS 메소드 반환
     * @return 입고번호 목록 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/inbound/inboundExamPda/listInboundNoPda";
    }

    /**
     * 입고번호 목록 요청 내용 반환
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

            object.put(NetworkParam.WAREHOUSE_DATE, mInfo.getDate());
            object.put(NetworkParam.WAREHOUSE_CATEGORY, mInfo.getCategory());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
