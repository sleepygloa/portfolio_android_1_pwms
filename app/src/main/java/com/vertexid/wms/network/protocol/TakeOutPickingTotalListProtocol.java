package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.TakeOutInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 피킹(총량)목록 조회 요청 프로토콜
 */
public class TakeOutPickingTotalListProtocol {
    private TakeOutInfo mInfo = null;

    public TakeOutPickingTotalListProtocol(TakeOutInfo info) {
        mInfo = info;
    }

    /**
     * 피킹(총량)목록  요청을 처리하는 WAS 메소드 반환
     * @return 출고번호 목록 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/outbound/outboundPickingPda/listOutboundPickingTotalPda";
    }

    /**
     * 피킹(총량)목록  요청 내용 반환
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

            object.put(NetworkParam.WAVE_NUMBER, mInfo.getWaveNum());
            object.put(NetworkParam.GOODS_CODE, mInfo.getGoods());

        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
