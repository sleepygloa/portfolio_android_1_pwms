package com.vertexid.wms.network.protocol;

import com.vertexid.wms.network.NetworkParam;
import com.vertexid.wms.scrn.takeout.info.WaveNumberInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 웨이브 번호 요청 프로토콜 생성
 */
public class WaveNumberProtocol {
    private WaveNumberInfo mInfo = null;

    public WaveNumberProtocol(WaveNumberInfo info) {
        mInfo = info;
    }

    /**
     * 웨이브 번호 요청을 처리하는 WAS 메소드 반환
     * @return 웨이브 번호 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/outbound/outboundPickingPda/listOutboundWaveNoPda";
    }

    /**
     * 웨이브 번호 요청 내용 반환
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

            object.put(NetworkParam.WAVE_DATE, mInfo.getDate());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
