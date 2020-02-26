package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.CarryNumberInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 반입 번호 목록 요청 프로토콜 생성
 */
public class CarryInNumberProtocol {
    private CarryNumberInfo mInfo = null;

    public CarryInNumberProtocol(CarryNumberInfo info) {
        mInfo = info;
    }

    /**
     * 반입 번호 목록 요청을 처리하는 WAS 메소드 반환
     * @return 반입 번호 목록 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/returninbound/returninboundExamPda/listReturnInboundNoPda";
    }

    /**
     * 반입 번호 목록 요청 내용 반환
     * @return JSON 형식의 문자열.
     */
    public String getReqPayload() {
        JSONObject object = new JSONObject();

        try {
            object.put(NetworkParam.COMPANY_CODE, mInfo.getCompanyCode());
            object.put(NetworkParam.CENTER_CODE_PRIOORD, mInfo.getCenterCode());
            object.put(NetworkParam.CUSTOMER_CODE_PRIOORD, mInfo.getCustomerCode());

            object.put(NetworkParam.CARRY_IN_DATE, mInfo.getCarryDate());
            object.put(NetworkParam.CARRY_IN_CATEGORY, mInfo.getCarryCategory());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
