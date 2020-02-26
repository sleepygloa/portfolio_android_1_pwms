package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.InvenMoveInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 임의재고이동 LOC 조회
 */
public class InvenTempMoveLocSearchProtocol {
    private InvenMoveInfo mInfo = null;

    public InvenTempMoveLocSearchProtocol(InvenMoveInfo info) {
        mInfo = info;
    }

    /**
     * 임의재고이동 LOC 조회 요청을 처리하는 WAS 메소드 반환
     * @return 임의재고이동 LOC 조회 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/stock/stockMovePda/listOptionalStockMoveLocPda";
    }

    /**
     * 임의재고이동 LOC 조회 요청 내용 반환
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

            object.put(NetworkParam.LOC_CD, mInfo.getFromLocation());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
