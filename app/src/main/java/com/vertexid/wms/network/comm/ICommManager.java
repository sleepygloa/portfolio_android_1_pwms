package com.vertexid.wms.network.comm;

/**
 * 서버와의 접속 동작을 위한 외부 공개 API
 */
public interface ICommManager {
    /**
     * 서버와 연결을 시도한다.
     * @param addr 서버 URL
     * @return
     */
    public int connect(String addr);
    /**
     * 연결된 서버에 데이터를 전달한다.
     * @param payload 서버에 전달할 데이터
     * @return
     */
    public int send(String payload);
    /**
     * 연결된 서버로부터 데이터를 수신한다.
     * @return
     */
    public int recv();
    /**
     * 서버와의 연결을 해제한다.
     * @return
     */
    public int close();
    /**
     * 서버에 정상적으로 접속이 되었는지 확인하기 위한 코드 반환
     * @return
     */
    public int getRespCode();
    /**
     * 데이터를 반환한다.
     * @return
     */
    public String getRespPayload();
}
