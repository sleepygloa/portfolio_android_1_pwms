package com.vertexid.wms.network.down;

/**
 * 다운로드 진행상황
 */
public interface INetDownListener {
    /**
     * 진행 상태를 반환한다.
     * @param progress 진행 상태
     */
    public void onProgress(int progress);
}
