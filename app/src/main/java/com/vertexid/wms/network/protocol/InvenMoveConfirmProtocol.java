package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.InvenMoveInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 재고이동 확정 요청 프로토콜 생성
 */
public class InvenMoveConfirmProtocol {
    private InvenMoveInfo mInfo = null;

    public InvenMoveConfirmProtocol(InvenMoveInfo info) {
        mInfo = info;
    }

    /**
     * 재고이동 확정 요청을 처리하는 WAS 메소드 반환
     * @return 재고이동 확정 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/stock/stockMovePda/updateStockMoveConfirmPda";
    }

    /**
     * 재고이동 확정 요청 내용 반환
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

            object.put(NetworkParam.MOVE_NUMBER, mInfo.getOrderNumber());
            object.put(NetworkParam.MOVE_DETAIL_NUMBER, mInfo.getOrderDetailNumber());
            object.put(NetworkParam.MOVE_BOX_COUNT, mInfo.getMoveBoxCount());
            object.put(NetworkParam.MOVE_EA_COUNT, mInfo.getMoveEaCount());
            object.put(NetworkParam.PLT_ID, mInfo.getToPltId());
            object.put(NetworkParam.LOC_CD, mInfo.getToLocation());
            object.put(NetworkParam.ACQUIRE, mInfo.getAcquireCount());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
