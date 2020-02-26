package com.vertexid.wms.network.handler;

/**
 * 서버와의 네트워크 통신 결과를 알려주는 리스너
 */
public interface INetCommHandlerListener {
    /**
     * 서버와의 통신 결과를 알려준다.
     * @param result 통신 결과
     * @param payload 서버로부터 수신된 데이터
     */
    public void onResponse(int result, String payload);
}
