package com.vertexid.wms.barcode;

/**
 * 바코드 스캔 결과를 반환하는 리스너
 */
public interface IScannerListener {
    /**
     * 스캐너를 통해 수신된 바코드 내용 반환
     * @param scan 스캐너를 통해 수신된 바코드 내용
     */
    public void onRecvScan(String scan);
}
