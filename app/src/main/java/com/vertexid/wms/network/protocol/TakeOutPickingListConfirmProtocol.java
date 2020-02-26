package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.TakeOutInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 피킹리스트 목록 확정 요청 프로토콜
 */
public class TakeOutPickingListConfirmProtocol {
    private TakeOutInfo mInfo = null;

    public TakeOutPickingListConfirmProtocol(TakeOutInfo info) {
        mInfo = info;
    }

    /**
     * 출고번호 목록 요청을 처리하는 WAS 메소드 반환
     * @return 출고번호 목록 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/outbound/outboundPickingPda/updateObConfirmPickingPda";
    }

    /**
     * 출고번호 목록 요청 내용 반환
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

            object.put(NetworkParam.TAKE_OUT_NUMBER, mInfo.getTakeOutNum());
            object.put(NetworkParam.TAKE_OUT_ORDER_NUMBER, mInfo.getOrderNumber());
            object.put(NetworkParam.TAKE_OUT_DETAIL_NUMBER, mInfo.getTakeOutDetailNum());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
