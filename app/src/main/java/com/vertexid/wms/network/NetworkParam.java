package com.vertexid.wms.network;

/**
 * 서버와의 통신에 필요한 인자 필드 정의
 */
public class NetworkParam {
    /** 결과코드 */
    public static final String RESULT_CODE = "stsCd";
//    /** 결과 메시지 코드 */
//    public static final String RESULT_MSG_CODE = "msgCd";
    /** 결과 메시지 */
    public static final String RESULT_MSG = "msgTxt";

    /** 사용자 ID */
    public static final String USER_ID = "userId";
    /** 사용자 비밀번호 */
    public static final String USER_PASS = "userPwd";
    /** 버전 */
    public static final String VERSION = "apkVersion";
    /** 디바이스 ID */
    public static final String DEVICE_ID = "device_id";

    /** 회사코드 */
    public static final String COMPANY_CODE = "companyCd";
    /** 회사명 */
    public static final String COMPANY_NAME = "companyNm";
    /** 사용자 물류센터 코드 */
    public static final String CENTER_CODE = "dcCd";
    /** 사용자 물류센터명 */
    public static final String CENTER_NAME = "dcNm";
    /** 사용자 물류센터 권한코드 */
    public static final String CENTER_CODE_PRIOORD = "dcCdPrioord";
    /** 사용자 물류센터 권한명 */
    public static final String CENTER_NAME_PRIOORD = "dcNmPrioord";
    /** 고객사 코드 */
    public static final String CUSTOMER_CODE = "clientCd";
    /** 고객사 명 */
    public static final String CUSTOMER_NAME = "clientNm";
    /** 고객사 권한코드 */
    public static final String CUSTOMER_CODE_PRIOORD = "clientCdPrioord";
    /** 고객사 권한명 */
    public static final String CUSTOMER_NAME_PRIOORD = "clientNmPrioord";

    /** 입고일자 */
    public static final String WAREHOUSE_DATE = "ibPlanYmd";
    /** 입고구분 */
    public static final String WAREHOUSE_CATEGORY = "ibGbnCd";

    /** 총건수 */
    public static final String TOTAL_COUNT = "cnt";
    /** 입하검수 목록 - JSONArray 필드 값 */
    public static final String ARRAY_LIST = "dt_grid";
    /** 물류센터 목록 - JSONArray 필드 값 */
    public static final String CENTER_LIST = "dt_Dc";
    /** 고객사 목록 - JSONArray 필드 값 */
    public static final String CUSTOMER_LIST = "dt_Client";
    /** 제품상태 목록 - JSONArray 필드 값 */
    public static final String GOODS_STATE_LIST = "dt_ItemStCd";
    /** 미출고사유 목록 - JSONArray 필드 값 */
    public static final String NOT_TAKE_OUT_LIST = "dt_NobRsCd";
    /** 입고구분코드 목록 - JSONArray 필드 값 */
    public static final String WARE_CATEGORY = "dt_IbGbnCd";
    /** 출고구분코드 목록 - JSONArray 필드 값 */
    public static final String TAKEOUT_CATEGORY = "dt_ObGbnCd";
    /** 반입구분코드 목록 - JSONArray 필드 값 */
    public static final String CARRY_IN_CATEGORY_LIST = "dt_RiGbnCd";
    /** 반출구분코드 목록 - JSONArray 필드 값 */
    public static final String CARRY_OUT_CATEGORY_LIST = "dt_RoGbnCd";
    /** 반출 사유 코드 */
    public static final String CARRY_OUT_REASON_LIST = "dt_RoRsCd";

    /** 카테고리 - 코드 */
    public static final String CATEGORY_CODE = "VALUE";
    /** 카테고리 - 이름 */
    public static final String CATEGORY_NAME = "NAME";

    /** 입고 번호 */
    public static final String WAREHOUSE_NUMBER = "ibNo";
    /** 입고 상세 번호 */
    public static final String WAREHOUSE_DETAIL_NUMBER = "ibDetailSeq";
    /** 입고 지시번호 */
    public static final String WAREHOUSE_ORDER_NUMBER = "ibInstNo";
    /** 제품코드 */
    public static final String GOODS_CODE = "itemCd";
    /** 제품명 */
    public static final String GOODS_NAME = "itemNm";
    /** 공급처 이름 */
    public static final String SUPPLIER_NAME = "supplierNm";
    /** 입수 */
    public static final String ACQUIRE = "pkQty";
    /** 수량 - PLT */
    public static final String PLT_COUNT = "pltQty";
    /** 수량 - BOX */
    public static final String BOX_COUNT = "boxQty";
    /** 수량 - EA */
    public static final String EA_COUNT = "eaQty";
    /** 제품상태 */
    public static final String GOODS_STATE = "itemStCd";
    /** 주문수량 */
    public static final String ORDER_COUNT = "instQty";
    /** 주문수량 - BOX */
    public static final String ORDER_COUNT_BOX = "instBoxQty";
    /** 주문수량 - EA */
    public static final String ORDER_COUNT_EA = "instEaQty";
    /** 승인수량 */
    public static final String APPROVAL_COUNT = "apprQty";
    /** 검수수량 */
    public static final String CHECK_COUNT = "examQty";
    /** 검수수량 - BOX */
    public static final String CHECK_COUNT_BOX = "examBoxQty";
    /** 검수수량 - EA */
    public static final String CHECK_COUNT_EA = "examEaQty";
    /** 파렛트ID */
    public static final String PLT_ID = "pltId";
    /** 제조 LOT */
    public static final String MAKE_LOT = "makeLot";
    /** 제조일자 */
    public static final String MAKE_DATE = "makeYmd";
    /** 유통일자 */
    public static final String EXPIRE_DATE = "distExpiryYmd";
    /** LOT 속성1 */
    public static final String LOT_ATTR1 = "lotAttr1";
    /** LOT 속성2 */
    public static final String LOT_ATTR2 = "lotAttr2";
    /** LOT 속성3 */
    public static final String LOT_ATTR3 = "lotAttr3";
    /** LOT 속성4 */
    public static final String LOT_ATTR4 = "lotAttr4";
    /** LOT 속성5 */
    public static final String LOT_ATTR5 = "lotAttr5";
    /** 적치수량 - BOX */
    public static final String PILE_UP_COUNT_BOX = "planBoxQty";
    /** 적치수량 - EA */
    public static final String PILE_UP_COUNT_EA = "planEaQty";
    /** 적치위치확인 */
    public static final String PILE_UP_LOCATION = "instLocCd";
    /** 입고일자 */
    public static final String WARE_DATE = "ibYmd";
    /** 시리얼 ID */
    public static final String SERIAL_ID = "serialId";
    /** 시리얼 검수수량 */
    public static final String SERIAL_CHECK_COUNT = "confQty";
    /** 대상 로케이션 */
    public static final String FROM_LOCATION = "frLocCd";
    /** 이동 로케이션 */
    public static final String TO_LOCATION = "toLocCd";
    public static final String TO_LOC = "toLoc";
    /** 대상 파렛트 */
    public static final String FROM_PLT_ID = "frPltId";
    /** 이동 파렛트 */
    public static final String TO_PLT_ID = "toPltId";
    public static final String TO_PLT = "toPlt";

    /** 츨고번호 */
    public static final String TAKE_OUT_NUMBER = "obNo";
    /** 출고일자 */
    public static final String TAKE_OUT_DATE = "obPlanYmd";
    /** 출고구분 */
    public static final String TAKE_OUT_CATEGORY = "obGbnCd";
    /** 웨이브번호생성일자*/
    public static final String WAVE_DATE = "inDt";
    /** 웨이브번호*/
    public static final String WAVE_NUMBER = "waveNo";
    /** 웨이브기준번호 */
    public static final String WAVE_STANDARD_NUMBER = "waveStdNo";
    /** 웨이브기준설명 */
    public static final String WAVE_STANDARD_EXPLAIN = "waveStdDesc";
    /** 출고 상세번호 */
    public static final String TAKE_OUT_DETAIL_NUMBER = "obDetailSeq";
    /** 출고 지시번호 */
    public static final String TAKE_OUT_ORDER_NUMBER = "obInstNo";
    public static final String TAKE_OUT_CHECK_COUNT = "allotQty";
    public static final String CAR_PLT_ID = "carIdPltId";

    /** 로케이션 */
    public static final String LOC_CD = "locCd";
    /** LOT ID */
    public static final String LOT_ID = "lotId";
    /** 피킹번호 */
    public static final String PICKING_NUMBER = "pickNo";
    /** 피킹수량 */
    public static final String PICKING_COUNT = "pickQty";
    /**피킹수량 - PLT */
    public static final String PICKING_PLT_COUNT = "pickPltQty";
    /**피킹수량 - BOX */
    public static final String PICKING_BOX_COUNT = "pickBoxQty";
    /**피킹수량 - EA */
    public static final String PICKING_EA_COUNT = "pickEaQty";
    /**피킹수량 - BOX */
    public static final String PICKING_BOX_TOTAL_COUNT = "pickBoxQty";
    /**피킹수량 - EA */
    public static final String PICKING_EA_TOTAL_COUNT = "pickEaQty";
    /**미출고 사유 */
    public static final String NOT_TAKE_OUT_REASON = "nobRsCd";
    /**차량 번호 */
    public static final String CAR_NUMBER = "carNo";
    /** 배송처명 */
    public static final String DELIVERY_NAME = "storeNm";
    /** 배송처코드 */
    public static final String DELIVERY_CODE = "storeCd";
    /** 지시 위치 */
    public static final String ORDER_LOC_CD = "instLocCd";

    /** 반입번호 */
    public static final String CARRY_IN_NUMBER = "riNo";
    /** 반입상세번호 */
    public static final String CARRY_IN_DETAIL_NUMBER = "riDetailSeq";
    /** 반입지시번호 */
    public static final String CARRY_IN_ORDER_NUMBER = "riInstNo";
    /** 반입구분 */
    public static final String CARRY_IN_CATEGORY = "riGbnCd";
    /** 반입일자 */
    public static final String CARRY_IN_DATE = "riPlanYmd";

    /** 반출번호 */
    public static final String CARRY_OUT_NUMBER = "roNo";
    /** 반출상세번호 */
    public static final String CARRY_OUT_DETAIL_NUMBER = "roDetailSeq";
    /** 반출지시번호 */
    public static final String CARRY_OUT_ORDER_NUMBER = "roWorkNo";
    /** 반출구분 */
    public static final String CARRY_OUT_CATEGORY = "roGbnCd";
    /** 반출일자 */
    public static final String CARRY_OUT_DATE = "roPlanYmd";
    public static final String CARRY_OUT_LOC = "instLocCd";
    /**  */
    public static final String CARRY_OUT_REASON = "roRsCd";

    /** 재고이동 번호 */
    public static final String MOVE_NUMBER = "moveNo";
    public static final String MOVE_DETAIL_NUMBER = "moveDetailSeq";
    public static final String MOVE_BOX_COUNT = "moveBoxQty";
    public static final String MOVE_EA_COUNT = "moveEaQty";
    public static final String MOVE_COUNT = "moveQty";

    public static final String INVEN_ORDER_NUMBER = "stockInspNo";
    public static final String INVEN_ORDER_DETAIL_NUMBER = "stockInspDetailSeq";
    public static final String INVEN_INQUIRY_BOX_COUNT = "inspBoxQty";
    public static final String INVEN_INQUIRY_EA_COUNT = "inspEaQty";
    public static final String INVEN_COUNT = "stockQty";
    public static final String INVEN_ACTUALITY_CATEGORY = "stockInspGbnCd";

    public static final String INVEN_CHANGE_NUMBER = "itemStChgNo";
    public static final String INVEN_CHANGE_DETAIL_NUMBER = "itemStChgDetailSeq";
    public static final String INVEN_CHANGE_BOX_COUNT = "itemStChgBoxQty";
    public static final String INVEN_CHANGE_EA_COUNT = "itemStChgEaQty";
    public static final String INVEN_CHANGE_STATE = "srcItemStCd";
    public static final String INVEN_CHANGE_CHANGE_STATE = "toItemStCd";

    public static final String INVEN_PLT_COUNT = "stockPltQty";
    public static final String INVEN_MOVE_BOX_COUNT = "stockBoxQty";
    public static final String INVEN_MOVE_EA_COUNT = "stockBoxQty";
    /** 파렛트 변경 번호 */
    public static final String PLT_CHANGE_NUMBER = "pltChgNo";
    public static final String CHANGE_BOX_COUNT = "chgBoxQty";
    public static final String CHANGE_EA_COUNT = "chgEaQty";
}
