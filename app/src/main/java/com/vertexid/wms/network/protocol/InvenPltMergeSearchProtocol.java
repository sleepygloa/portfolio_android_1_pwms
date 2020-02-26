package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.InvenPltInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 파렛트 병합 목록 요청 프로토콜 생성
 */
public class InvenPltMergeSearchProtocol {
    private InvenPltInfo mInfo = null;

    public InvenPltMergeSearchProtocol(InvenPltInfo info) {
        mInfo = info;
    }

    /**
     * 재고관리 파렛트 병합 목록 요청을 처리하는 WAS 메소드 반환
     * @return 재고관리 파렛트 분할 저장 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/stock/stockPltMergePda/listStockPltMergePda";
    }

    /**
     * 재고관리 파렛트 병합 목록 요청 내용 반환
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

            object.put(NetworkParam.ARRAY_LIST, mInfo.getPltArray());
            object.put(NetworkParam.TO_PLT_ID, mInfo.getToPltId());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
