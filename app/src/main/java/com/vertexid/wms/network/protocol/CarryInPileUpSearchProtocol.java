package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.CarryInInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 반입 적치 제품 상세 내용을 요청하는 프로토콜 생성
 */
public class CarryInPileUpSearchProtocol {
    private CarryInInfo mInfo = null;

    public CarryInPileUpSearchProtocol(CarryInInfo info) {
        mInfo = info;
    }

    /**
     * 반입 적치 제품 상세 내용 요청을 처리하는 WAS 메소드 반환
     * @return 반입 적치 제품 상세 내용 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/returninbound/returninboundPutwPda/listReturnInboundPutwPdaH";
    }

    /**
     * 반입 적치 제품 상세 내용 요청 내용 반환
     * @return JSON 형식의 문자열.
     */
    public String getReqPayload() {
        JSONObject object = new JSONObject();

        try {
            object.put(NetworkParam.COMPANY_CODE, mInfo.getCompanyCode());
            object.put(NetworkParam.CENTER_CODE_PRIOORD, mInfo.getCenterCode());
            object.put(NetworkParam.CUSTOMER_CODE_PRIOORD, mInfo.getCustomerCode());

            object.put(NetworkParam.PLT_ID, mInfo.getPltId());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
