package com.vertexid.wms.network.down;

/**
 * 서버에 접속하여 서버로부터 파일 다운로드 동작을 위한 외부 공개 API
 */
public interface INetDownManager {
    /**
     * 서버와 연결을 시도한다.
     * @param addr 연결 시도할 서버 주소
     * @return
     */
    public int connect(String addr);

    /**
     * 연결된 서버로부터 데이터를 수신한다.
     * @param path 파일을 생성하여 위치할 경로
     * @return
     */
    public int recv(String path, INetDownListener listener);

    /**
     * 서버와의 연결을 해제한다.
     * @return
     */
    public int close();
}
