package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.SerialScanInfo;
import com.vertexid.wms.info.TakeOutInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 출고시리얼스캔 확정 요청 프로토콜
 */
public class TakeOutSerialScanConfirmProtocol {
    private SerialScanInfo mInfo = null;

    public TakeOutSerialScanConfirmProtocol(SerialScanInfo info) {
        mInfo = info;
    }

    /**
     * 출고번호 목록 요청을 처리하는 WAS 메소드 반환
     * @return 출고번호 목록 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/outbound/outboundSerialPda/insertOutSerialPda";
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

            object.put(NetworkParam.ARRAY_LIST, mInfo.getDtGrid());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
