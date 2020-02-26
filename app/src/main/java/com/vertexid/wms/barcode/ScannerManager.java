package com.vertexid.wms.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.BarcodeReadEvent;
import com.vertexid.wms.utils.VLog;

import java.util.HashMap;

import co.kr.bluebird.sled.SDConsts;

/**
 * 바코드 스캔을 위한 외부 공개 API를 구현한 클래스
 */
public class ScannerManager {
    private final int SEQ_BARCODE_OPEN = 100;
    private final int SEQ_BARCODE_CLOSE = 200;

    private Context mContext = null;
    private BroadcastReceiver mReceiver = null;

    private IScannerListener mListener = null;

    private boolean mIsOpened;
    private int mBarcodeHandle;

    private BarcodeReader mBarcodeReader = null;
    private AidcManager mAidcManager = null;

    private static ScannerManager mObject = null;

    public ScannerManager(Context context) {
        mContext = context;
    }

    public static ScannerManager getInst(Context context) {
        if (context == null) {
            return null;
        }

        if (mObject == null) {
            mObject = new ScannerManager(context);
        }

        return mObject;
    }

    /**
     * 바코드 스캔 결과를 수신하기 위한 리스너 등록
     * @param listener 바코드 스캔 결과를 수신하기 위한 리스너
     */
    public void setListener(IScannerListener listener) {
        mListener = listener;
    }

    /**
     * 바코드 기능 활성화
     */
    public void resume() {
        Intent i = new Intent();
        i.setAction(Constants.ACTION_BARCODE_OPEN);
        if (mIsOpened) {
            i.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
        }
        i.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_OPEN);
        mContext.sendBroadcast(i);
    }

    /**
     * 바코드 기능 비활성화
     */
    public void pause() {
        Intent i = new Intent();
        i.setAction(Constants.ACTION_BARCODE_CLOSE);
        i.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
        i.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_CLOSE);
        mIsOpened = false;
        mContext.sendBroadcast(i);
    }

    public void init() {
        if (mContext == null) {
            return ;
        }

        if (mObject == null) {
            mObject = new ScannerManager(mContext);
        }

        if (Build.MODEL.toUpperCase().equals("EF500")) {
            setBroadcastReceiver(true);

            Intent i = new Intent();
            i.setAction(Constants.ACTION_BARCODE_OPEN);
            if (mIsOpened) {
                i.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
            }
            i.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_OPEN);
            mContext.sendBroadcast(i);
        }
        else if (Build.MODEL.toUpperCase().equals("CT50")) {
            AidcManager.create(mContext, mCreateCallbackListener);
        }
    }

    public void close() {
        if (Build.MODEL.toUpperCase().equals("EF500")) {
            setBroadcastReceiver(false);
            mIsOpened = false;

            // Barcode 해제
            Intent i = new Intent();
            i.setAction(Constants.ACTION_BARCODE_CLOSE);
            i.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
            i.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_CLOSE);
            mContext.sendBroadcast(i);
        }
        else if (Build.MODEL.toUpperCase().equals("CT50")) {
            if (mBarcodeReader != null) {
                // close BarcodeReader to clean up resources.
                mBarcodeReader.close();
                mBarcodeReader = null;
            }

            if (mAidcManager != null) {
                // close AidcManager to disconnect from the scanner service.
                // once closed, the object can no longer be used.
                mAidcManager.close();
                mAidcManager = null;
            }
        }
    }

    /**
     * onPause 시 호출
     */
    public void release() {
        if (mBarcodeReader != null) {
            mBarcodeReader.release();
        }
    }

    private void setProperties(AidcManager aidcManager) {
        mAidcManager = aidcManager;
        mBarcodeReader = mAidcManager.createBarcodeReader();

        HashMap<String, Object> properties = new HashMap<>();
        // Set Symbologies On/Off
        properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
        properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_EAN_8_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, true);

        properties.put(BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_EAN_8_CHECK_DIGIT_TRANSMIT_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_POSTAL_2D_PLANET_CHECK_DIGIT_TRANSMIT_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_POSTAL_2D_POSTNET_CHECK_DIGIT_TRANSMIT_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_UPC_A_CHECK_DIGIT_TRANSMIT_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_UPC_E_CHECK_DIGIT_TRANSMIT_ENABLED, true);

        properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);
        properties.put(BarcodeReader.PROPERTY_NOTIFICATION_GOOD_READ_ENABLED, true);

        // Apply the settings
        mBarcodeReader.setProperties(properties);

        try {
            mBarcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mBarcodeReader.claim();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mBarcodeReader.addBarcodeListener(mBarcodeListener);
    }

    /**
     * Barcode 결과 방송을 수신할 브로드캐스트
     * @param is_set true : 브로드캐스트 활성화, false : 브로드캐스트 비활성화
     */
    private void setBroadcastReceiver(boolean is_set) {
        if (is_set) {
            // 이미 리시버가 등록되어 있을 경우
            // 등록해제 후 다시 등록한다.
            if (mReceiver != null) {
                mContext.unregisterReceiver(mReceiver);
                mReceiver = null;
            }

            IntentFilter filter = new IntentFilter();
            filter.addAction(SDConsts.ACTION_SLED_ATTACHED);
            filter.addAction(SDConsts.ACTION_SLED_DETACHED);
            filter.addAction(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA);
            filter.addAction(Constants.ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS);
            filter.addAction(Constants.ACTION_BARCODE_CALLBACK_REQUEST_FAILED);
            filter.addAction(Constants.ACTION_BARCODE_CALLBACK_PARAMETER);
            filter.addAction(Constants.ACTION_BARCODE_CALLBACK_GET_STATUS);

            // 단말과 RFID 리더기와의 탈부착 관련 액션 수신 동적 리스너
            // DETACHED 액션은 수신하지 않고, ATTACHED 액션 수신 시 RFID 기능 활성화
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent == null) {
                        return ;
                    }

                    String action = intent.getAction();
                    if (action == null) {
                        return ;
                    }

                    // 바코드 데이터를 수신
                    if (action.equals(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA)) {
                        byte[] data = intent.getByteArrayExtra(Constants.EXTRA_BARCODE_DECODING_DATA);
                        VLog.d("barcode scan : " + new String(data));
                        if (data != null) {
                            if (mListener != null) {
                                mListener.onRecvScan(new String(data));
                            }
                        }
                    }
                    else if (action.equals(Constants.ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS)) {
                        mIsOpened = true;
                        mBarcodeHandle = intent.getIntExtra(Constants.EXTRA_HANDLE, 0);
                    }
                }
            };
            mContext.registerReceiver(mReceiver, filter);
        }
        else {
            if (mReceiver != null) {
                mContext.unregisterReceiver(mReceiver);
                mReceiver = null;
            }
        }
    }

    private BarcodeReader.BarcodeListener mBarcodeListener = new BarcodeReader.BarcodeListener() {
        @Override
        public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
            String barcode_data = barcodeReadEvent.getBarcodeData();
            if (barcode_data == null) {
                return;
            }

            if (mListener != null) {
                mListener.onRecvScan(barcode_data);
            }
        }

        @Override
        public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        }
    };

    private AidcManager.CreatedCallback mCreateCallbackListener = new AidcManager.CreatedCallback() {
        @Override
        public void onCreated(AidcManager aidcManager) {
            setProperties(aidcManager);
        }
    };
}
