package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.WareHouseInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 입하 검수 요청 프로토콜 생성
 */
public class ArrivalCheckProtocol {
    private WareHouseInfo mInfo = null;

    public ArrivalCheckProtocol(WareHouseInfo info) {
        mInfo = info;
    }

    /**
     * 입하 검수 요청을 처리하는 WAS 메소드 반환
     * @return 입하 검수 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/inbound/inboundExamPda/insertInboundExamPdaD";
    }

    /**
     * 입하 검수 요청 내용 반환
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

            object.put(NetworkParam.WAREHOUSE_NUMBER, mInfo.getWareHouseNumber());
            object.put(NetworkParam.WAREHOUSE_DETAIL_NUMBER, mInfo.getWareHouseDetailNumber());
            object.put(NetworkParam.GOODS_CODE, mInfo.getGoods());

            object.put(NetworkParam.GOODS_STATE, mInfo.getGoodsState());
            object.put(NetworkParam.ACQUIRE, mInfo.getAcquireCount());
            object.put(NetworkParam.CHECK_COUNT_BOX, mInfo.getCheckBoxCount());
            object.put(NetworkParam.CHECK_COUNT_EA, mInfo.getCheckEaCount());
            object.put(NetworkParam.PLT_ID, mInfo.getPltId());
            object.put(NetworkParam.MAKE_LOT, mInfo.getManufactureLot());
            object.put(NetworkParam.MAKE_DATE, mInfo.getMakeDate());
            object.put(NetworkParam.EXPIRE_DATE, mInfo.getDistributionDate());
            object.put(NetworkParam.LOT_ATTR1, mInfo.getLotAttribute1());
            object.put(NetworkParam.LOT_ATTR2, mInfo.getLotAttribute2());
            object.put(NetworkParam.LOT_ATTR3, mInfo.getLotAttribute3());
            object.put(NetworkParam.LOT_ATTR4, mInfo.getLotAttribute4());
            object.put(NetworkParam.LOT_ATTR5, mInfo.getLotAttribute5());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
