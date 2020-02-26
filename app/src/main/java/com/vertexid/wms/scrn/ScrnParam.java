package com.vertexid.wms.scrn;

/**
 * 액티비티 간 데이터 전달에 필요한 필드 정의
 */
public class ScrnParam {
    public static final String APK_SVR_VERSION = "apk_version";
    /** 정보클래스 목록 */
    public static final String INFO_LIST = "info_list";
    /** 정보클래스 상세 내용 */
    public static final String INFO_DETAIL = "info_detail";
    /** 리스트 상에 표시된 정보 객체 인덱스 */
    public static final String INFO_POSITION = "info_position";
    /** 공급처 */
    public static final String SUPPLIER = "supplier";
    /** 배송처 */
    public static final String DELIVERY = "delivery";
    /** 제품 */
    public static final String GOODS = "goods";
    /** 입/출고 번호 */
    public static final String NUMBER = "number";
    /** 입/출고 상세번호 */
    public static final String DETAIL_NUMBER = "detail_number";
    public static final String INVEN_ACTUALITY_CATEGORY = "actuality_category";
    public static final String WAVE_NUMBER = "wave_number";

    public static final String ACTION_CALL_DIVIDE_ACTIVITY = "com.vertexid.wms.scrn.action.call_from_total_picking";
}
