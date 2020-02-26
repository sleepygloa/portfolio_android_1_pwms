package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.WareHouseInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 입고적치 내용 프로토콜 생성
 */
public class WarePileUpProtocol {
    private WareHouseInfo mInfo = null;

    public WarePileUpProtocol(WareHouseInfo info) {
        mInfo = info;
    }

    /**
     * 로그인 요청을 처리하는 WAS 메소드 반환
     * @return 로그인 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/inbound/inboundPutwPda/listInboundPutwPdaH";
    }

    /**
     * 로그인 요청 내용 반환
     * @return JSON 형식의 문자열.
     */
    public String getReqPayload() {
        JSONObject object = new JSONObject();

        try {
            object.put(NetworkParam.COMPANY_CODE, mInfo.getCompanyCode());
            object.put(NetworkParam.CENTER_CODE_PRIOORD, mInfo.getCenterCode());
            object.put(NetworkParam.CUSTOMER_CODE_PRIOORD, mInfo.getCustomerCode());

            object.put(NetworkParam.GOODS_CODE, mInfo.getGoods());
            object.put(NetworkParam.PLT_ID, mInfo.getPltId());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
