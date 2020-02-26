package com.vertexid.wms.info;

import java.io.Serializable;

/**
 * 피킹 PLT 정보
 */
public class TakeOutLoadingPickingPltInfo implements Serializable {
    /** 피킹 PLT */
    private String mPickingPltId;
    /** 상차 PLT */
    private String mLoadingPltId;

    /**
     * 피킹 PLT 설정
     * @param plt_id PLT id
     */
    public void setPickingPltId(String plt_id) {
        mPickingPltId = plt_id;
    }

    /**
     * 피킹 PLT 반환
     * @return PLT id
     */
    public String getPickingPltId() {
        return mPickingPltId;
    }

    /**
     * 상차 PLT 설정
     * @param plt_id PLT id
     */
    public void setLoadingPltId(String plt_id) {
        mLoadingPltId = plt_id;
    }

    /**
     * 상차 PLT 반환
     * @return PLT id
     */
    public String getLoadingPltId() {
        return mLoadingPltId;
    }
}
