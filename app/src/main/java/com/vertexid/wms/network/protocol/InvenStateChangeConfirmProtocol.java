package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.InventoryStateChangeInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 재고상태 변경 요청 프로토콜 생성
 */
public class InvenStateChangeConfirmProtocol {
    private InventoryStateChangeInfo mInfo = null;

    public InvenStateChangeConfirmProtocol(InventoryStateChangeInfo info) {
        mInfo = info;
    }

    /**
     * 재고상태 변경 요청을 처리하는 WAS 메소드 반환
     * @return 재고상태 변경 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/stock/stockStateChangePda/updateStockStateChangeConfirmPda";
    }

    /**
     * 재고상태 변경 요청 내용 반환
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

            object.put(NetworkParam.INVEN_CHANGE_NUMBER, mInfo.getOrderNumber());
            object.put(NetworkParam.INVEN_CHANGE_DETAIL_NUMBER, mInfo.getOrderDetailNumber());
            object.put(NetworkParam.INVEN_CHANGE_BOX_COUNT, mInfo.getChangeBoxCount());
            object.put(NetworkParam.INVEN_CHANGE_EA_COUNT, mInfo.getChangeEaCount());
            object.put(NetworkParam.TO_PLT, mInfo.getChangePltId());
            object.put(NetworkParam.TO_LOC, mInfo.getChangeLocation());
            object.put(NetworkParam.INVEN_CHANGE_CHANGE_STATE, mInfo.getChangeGoodState());
            object.put(NetworkParam.ACQUIRE, mInfo.getAcquire());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
