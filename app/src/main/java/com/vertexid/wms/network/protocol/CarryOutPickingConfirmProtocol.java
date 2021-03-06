package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.CarryOutInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 반출피킹 확정 요청 프로토콜 생성
 */
public class CarryOutPickingConfirmProtocol {
    private CarryOutInfo mInfo = null;

    public CarryOutPickingConfirmProtocol(CarryOutInfo info) {
        mInfo = info;
    }

    /**
     * 반출피킹 확정 요청을 처리하는 WAS 메소드 반환
     * @return 반출피킹 확정 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/returnoutbound/returnoutboundPickingPda/updateReturnObConfirmPickingPda";
    }

    /**
     * 반출피킹 확정 요청 내용 반환
     * @return JSON 형식의 문자열.
     */
    public String getReqPayload() {
        JSONObject object = new JSONObject();

        try {
            object.put(NetworkParam.COMPANY_CODE, mInfo.getCompanyCode());
            object.put(NetworkParam.CENTER_CODE_PRIOORD, mInfo.getCenterCode());
            object.put(NetworkParam.CUSTOMER_CODE_PRIOORD, mInfo.getCustomerCode());
            object.put(NetworkParam.USER_ID, mInfo.getUserId());

            object.put(NetworkParam.CARRY_OUT_NUMBER, mInfo.getCarryOutNumber());
            object.put(NetworkParam.CARRY_OUT_DETAIL_NUMBER, mInfo.getCarryOutDetailNumber());
            object.put(NetworkParam.CARRY_OUT_ORDER_NUMBER, mInfo.getCarryOutOrderNumber());
            object.put(NetworkParam.CARRY_OUT_REASON, mInfo.getCarryOutReason());

//            object.put(NetworkParam.GOODS_CODE, mInfo.getGoodsCode());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
