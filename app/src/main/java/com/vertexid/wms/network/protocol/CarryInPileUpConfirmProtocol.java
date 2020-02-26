package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.CarryInInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 2017-05-16.
 */
public class CarryInPileUpConfirmProtocol {
    private CarryInInfo mInfo = null;

    public CarryInPileUpConfirmProtocol(CarryInInfo info) {
        mInfo = info;
    }

    /**
     * 반입 적치 제품 상세 내용 요청을 처리하는 WAS 메소드 반환
     * @return 반입 적치 제품 상세 내용 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/returninbound/returninboundPutwPda/updateReturnIbConfirmPutwPda";
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
            object.put(NetworkParam.USER_ID, mInfo.getUserId());

            object.put(NetworkParam.CARRY_IN_NUMBER, mInfo.getCarryInNumber());
            object.put(NetworkParam.CARRY_IN_DETAIL_NUMBER, mInfo.getCarryInDetailNumber());
            object.put(NetworkParam.CARRY_IN_ORDER_NUMBER, mInfo.getCarryInOrderNumber());
            object.put(NetworkParam.GOODS_CODE, mInfo.getGoodsCode());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
