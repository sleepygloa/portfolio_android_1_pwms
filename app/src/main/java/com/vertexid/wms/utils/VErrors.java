package com.vertexid.wms.utils;

/**
 * 오류 내용 정의
 */
public class VErrors {
    /** 정상 */
    public static final int E_NONE = 0;

    /** 잘못된 인자 */
    public static final int E_INVALID_PARAM = E_NONE + 1;

    /** 네트워크 연결이 끊어지거나 연결되어 있지 않음 */
    public static final int E_DISCONNECTED = E_NONE - 1;
    /** URL 을 찾을 수 없음 */
    public static final int E_UNKNOWN_HOST = E_DISCONNECTED - 1;
    /** 서버와 연결이 되지 않음 */
    public static final int E_NOT_CONNECTED = E_UNKNOWN_HOST - 1;
    /** 응답 시간 초과. */
    public static final int E_TIME_OUT = E_NOT_CONNECTED - 1;
    /** 서버에 Packet 전달 실패 */
    public static final int E_SEND_TO_SERVER = E_TIME_OUT - 1;
    /** 서버로부터 응답 수신 실패 */
    public static final int E_RECV_FROM_SERVER = E_SEND_TO_SERVER - 1;

    /** 기타 오류 */
    public static final int E_ETC = E_RECV_FROM_SERVER - 1;
}
