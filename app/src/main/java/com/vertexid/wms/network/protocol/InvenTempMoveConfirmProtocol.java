package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.InvenMoveInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 임의재고 이동 지시 확정 요청 프로토콜 생성
 */
public class InvenTempMoveConfirmProtocol {
    private InvenMoveInfo mInfo;

    public InvenTempMoveConfirmProtocol(InvenMoveInfo info) {
        mInfo = info;
    }

    /**
     * 임의 재고이동 지시 내용 요청을 처리하는 WAS 메소드 반환
     * @return 재고이동 목록 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/stock/stockMovePda/updateOptionalStockMoveConfirmPda";
    }

    /**
     * 임의 재고이동 지시 내용 요청 내용 반환
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

            object.put(NetworkParam.PLT_ID, mInfo.getFromPltId());
            object.put(NetworkParam.FROM_LOCATION, mInfo.getFromLocation());
            object.put(NetworkParam.TO_LOCATION, mInfo.getToLocation());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
