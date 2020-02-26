package com.vertexid.wms.network.handler;

/**
 * 파일 다운로드 결과 반환 리스너
 */
public interface INetDownHandlerListener {
    /**
     * 진행 상태를 반환한다.
     * @param progress 진행 상태
     */
    public void onProgress(int progress);

    /**
     * 통신 결과를 반환한다.
     * @param error 통신 결과
     */
    public void onResult(int error);
}
