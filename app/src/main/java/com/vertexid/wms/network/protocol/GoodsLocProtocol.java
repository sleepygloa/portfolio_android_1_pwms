package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.InquiryInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 제품 위치 목록을 요청하는 프로토콜 생성
 */
public class GoodsLocProtocol {
    private InquiryInfo mInfo = null;

    public GoodsLocProtocol(InquiryInfo info) {
        mInfo = info;
    }

    /**
     * 제품 위치 목록 요청을 처리하는 WAS 메소드 반환
     * @return 제품 위치 목록 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/stock/stockLocSearchPda/listStockLocSearchPda";
    }

    /**
     * 제품 위치 목록 요청 내용 반환
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

            object.put(NetworkParam.LOC_CD, mInfo.getLocation());
            object.put(NetworkParam.LOT_ID, mInfo.getLotId());
            object.put(NetworkParam.MAKE_DATE, mInfo.getMakeDates());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
