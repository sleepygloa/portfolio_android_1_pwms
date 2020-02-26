package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.InvenPltInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 재고관리 파렛트 분할 저장 요청 프로토콜
 */
public class InvenPltDivideSaveProtocol {
    private InvenPltInfo mInfo = null;

    public InvenPltDivideSaveProtocol(InvenPltInfo info) {
        mInfo = info;
    }

    /**
     * 재고관리 파렛트 분할 저장 요청을 처리하는 WAS 메소드 반환
     * @return 재고관리 파렛트 분할 저장 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/stock/stockPltDivisionPda/insertStockPltDivisionPda";
    }

    /**
     * 재고관리 파렛트 분할 저장 요청 내용 반환
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

            object.put(NetworkParam.ACQUIRE, mInfo.getAcquire());
            object.put(NetworkParam.FROM_LOCATION, mInfo.getFromLocation());
            object.put(NetworkParam.TO_LOCATION, mInfo.getFromLocation());
            object.put(NetworkParam.PLT_COUNT, mInfo.getToPltCount());
            object.put(NetworkParam.BOX_COUNT, mInfo.getToBoxCount());
            object.put(NetworkParam.EA_COUNT, mInfo.getToEaCount());
            object.put(NetworkParam.GOODS_CODE, mInfo.getGoodsCode());
            object.put(NetworkParam.GOODS_STATE, mInfo.getGoodsState());
//            object.put(NetworkParam, mInfo);
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
