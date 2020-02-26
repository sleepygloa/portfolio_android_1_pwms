package com.vertexid.wms.network;

/**
 * NetworkManager를 통한 서버와의 통신 결과를 반환한다.
 */
public interface INetworkListener {
    /**
     * 네트워크 통신 결과를 반환한다.
     * @param error 네트워크 통신 결과 코드
     * @param payload 서버로부터 수신된 내용
     */
    public void onRespResult(int error, String payload);

    /**
     * 파일 다운로드 진행 상태를 반환한다.
     * @param progress 진행상태
     */
    public void onProgress(int progress);

    /**
     * 파일 다운로드 통신 결과를 반환한다.
     * @param error 통신 결과
     */
    public void onResult(int error);
}
