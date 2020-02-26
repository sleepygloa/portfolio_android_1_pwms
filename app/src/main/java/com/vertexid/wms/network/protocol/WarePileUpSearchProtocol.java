package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.WareHouseInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 입고 적치 제품 조회 요청 프로토콜 생성
 */
public class WarePileUpSearchProtocol {
    private WareHouseInfo mInfo = null;

    public WarePileUpSearchProtocol(WareHouseInfo info) {
        mInfo = info;
    }

    /**
     * 입고적치 제품 조회 요청을 처리하는 WAS 메소드 반환
     * @return 입고적치 제품 조회 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/inbound/inboundPutwPda/listInboundPutwItemPda";
    }

    /**
     * 입고적치 제품 조회 요청 내용 반환
     * @return JSON 형식의 문자열.
     */
    public String getReqPayload() {
        JSONObject object = new JSONObject();

        try {
            object.put(NetworkParam.COMPANY_CODE, mInfo.getCompanyCode());
            object.put(NetworkParam.CENTER_CODE_PRIOORD, mInfo.getCenterCode());
            object.put(NetworkParam.CUSTOMER_CODE_PRIOORD, mInfo.getCustomerCode());

            object.put(NetworkParam.GOODS_CODE, mInfo.getGoods());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
