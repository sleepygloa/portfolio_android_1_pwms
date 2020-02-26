package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 출고번호 조회 관련 정보 클래스
 */

public class TakeOutNumSearchInfo implements Serializable{
    public static final int NONE = 0;
    public static final int SELECT = NONE + 1;
    public static final int CHECK_COMPLETE = SELECT + 1;

    /** 출고번호*/
    private String mTakeOutNum = "";

    /** 출고번호*/
    private String mDeliveryAddress =  "";

    public TakeOutNumSearchInfo(){

    }

    /**
     * 출고번호 설정
     * @param mTakeOutNum 출고번호
     */
    public void setTakeOutNum(String mTakeOutNum){
        this.mTakeOutNum = mTakeOutNum;
    }

    /**
     * 출고번호 반환
     * @return 출고번호
     */
    public String getTakeOutNum(){
        return mTakeOutNum;
    }

    /**
     * 배송처 설정
     * @param mDeliveryAddress 배송처
     */
    public void setDeliveryAddress(String mDeliveryAddress){
        this.mDeliveryAddress = mDeliveryAddress;
    }

    /**
     * 배송처 반환
     * @return 배송처
     */
    public String getDeliveryAddress(){
        return mDeliveryAddress;
    }

}
