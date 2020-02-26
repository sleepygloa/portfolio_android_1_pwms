package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.SerialScanInfo;
import com.vertexid.wms.info.TakeOutInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 출고 시리얼 스캔 목록 요청 프로토콜 생성
 */
public class TakeOutSerialScanProtocol {
    private TakeOutInfo mInfo = null;

    public TakeOutSerialScanProtocol(TakeOutInfo info) {
        mInfo = info;
    }

    /**
     * 출고 시리얼 스캔 목록 요청을 처리하는 WAS 메소드 반환
     * @return 입고 시리얼 스캔 목록 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/outbound/outboundSerialPda/listOutboundSerialPda";
    }

    /**
     * 출고 시리얼 스캔 목록 요청 내용 반환
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

            object.put(NetworkParam.TAKE_OUT_NUMBER, mInfo.getTakeOutNum());
            object.put(NetworkParam.GOODS_CODE, mInfo.getGoods());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
