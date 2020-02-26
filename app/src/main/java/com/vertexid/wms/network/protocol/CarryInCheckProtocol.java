package com.vertexid.wms.network.protocol;

import com.vertexid.wms.info.CarryInInfo;
import com.vertexid.wms.network.NetworkParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 반입검수 요청 프로토콜 생성
 */
public class CarryInCheckProtocol {
    private CarryInInfo mInfo = null;

    public CarryInCheckProtocol(CarryInInfo info) {
        mInfo = info;
    }

    /**
     * 반입검수 요청을 처리하는 WAS 메소드 반환
     * @return 반입검수 요청을 처리하는 WAS 메소드
     */
    public String getReqMethod() {
        return "/ctrl/returninbound/returninboundExamPda/insertReturnInboundExamPdaD";
    }

    /**
     * 반입검수 요청 내용 반환
     * @return JSON 형식의 문자열.
     */
    public String getReqPayload() {
        JSONObject object = new JSONObject();

        try {
            object.put(NetworkParam.COMPANY_CODE, mInfo.getCompanyCode());
            object.put(NetworkParam.CENTER_CODE_PRIOORD, mInfo.getCenterCode());
            object.put(NetworkParam.CUSTOMER_CODE_PRIOORD, mInfo.getCustomerCode());
            object.put(NetworkParam.USER_ID, mInfo.getUserId());

            object.put(NetworkParam.CARRY_IN_NUMBER, mInfo.getCarryInNumber());
            object.put(NetworkParam.CARRY_IN_DETAIL_NUMBER, mInfo.getCarryInDetailNumber());
            object.put(NetworkParam.GOODS_CODE, mInfo.getGoodsCode());
            object.put(NetworkParam.GOODS_STATE, mInfo.getGoodsState());
            object.put(NetworkParam.CHECK_COUNT_BOX, mInfo.getCheckBoxCount());
            object.put(NetworkParam.CHECK_COUNT_EA, mInfo.getCheckEaCount());
            object.put(NetworkParam.PLT_ID, mInfo.getPltId());
            object.put(NetworkParam.ACQUIRE, mInfo.getAcquire());
            object.put(NetworkParam.MAKE_LOT, mInfo.getManufactureLot());
            object.put(NetworkParam.MAKE_DATE, mInfo.getMakeDate());
            object.put(NetworkParam.EXPIRE_DATE, mInfo.getDistributionDate());
            object.put(NetworkParam.LOT_ATTR1, mInfo.getLot1());
            object.put(NetworkParam.LOT_ATTR2, mInfo.getLot2());
            object.put(NetworkParam.LOT_ATTR3, mInfo.getLot3());
            object.put(NetworkParam.LOT_ATTR4, mInfo.getLot4());
            object.put(NetworkParam.LOT_ATTR5, mInfo.getLot5());
        }
        catch (JSONException e) {
            return null;
        }

        return object.toString();
    }
}
