package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.InvenInquiryInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 재고조사 확정 요청 프로토콜
 */
public class InvenInquiryConfirmProtocol {
    private InvenInquiryInfo mInfo = null;

    public InvenInquiryConfirmProtocol(InvenInquiryInfo info) {
        mInfo = info;
    }

    /**
     * 재고조사 확정 요청을 처리하는 WAS 메소드 반환
     * @return 재고조사 확정 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/stock/stockInspPda/updateStockInspConfirmPda";
    }

    /**
     * 재고조사 확정 요청 내용 반환
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

            object.put(NetworkParam.INVEN_ORDER_NUMBER, mInfo.getOrderNumber());
            object.put(NetworkParam.INVEN_ORDER_DETAIL_NUMBER, mInfo.getOrderDetailNumber());
            object.put(NetworkParam.INVEN_INQUIRY_BOX_COUNT, mInfo.getActualityBoxCount());
            object.put(NetworkParam.INVEN_INQUIRY_EA_COUNT, mInfo.getActualityEaCount());
            object.put(NetworkParam.ACQUIRE, mInfo.getAcquire());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
