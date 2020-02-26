package com.vertexid.wms.network.handler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.vertexid.wms.network.down.INetDownListener;
import com.vertexid.wms.network.down.INetDownManager;
import com.vertexid.wms.network.down.NetDownManager;
import com.vertexid.wms.utils.VErrors;

/**
 * 파일 다운로드 네트워크 핸들러
 */
public class NetDownHandler extends Handler {
    private final int STATE_NONE		= 0;
    /** 서버와 연결 시도 */
    private final int STATE_CONNECT		= STATE_NONE + 1;
    /** 서버로부터 응답 내용 수신 */
    private final int STATE_RECV		= STATE_CONNECT + 1;
    /** 서버와의 연결 종료 */
    private final int STATE_DISCONNECT	= STATE_RECV + 1;

    private Context mContext = null;

    private INetDownManager mNetDownManager = null;
    private INetDownHandlerListener mListener = null;

    /** 연결할 서버 URL */
    private String mAddr = null;
    /** 다운로드 받은 내용을 파일로 생성할 위치 */
    private String mPath = null;

    private int mError;

    public NetDownHandler(Context context, Looper looper, String addr, String path) {
        super(looper);

        mContext = context;

        mAddr = addr;
        mPath = path;
    }

    /**
     * 서버와의 통신 시작
     * @param listener 서버와의 통신 결과 및 수신된 내용을 수신하기 위한 리스너
     */
    public void start(INetDownHandlerListener listener) {
        mListener = listener;
        goNextState(STATE_CONNECT);
    }

    @Override
    public void handleMessage(Message msg) {
        int next = STATE_NONE;

        switch (msg.what) {
            case STATE_CONNECT :
                next = open();
                break;

            case STATE_RECV :
                next = receive();
                break;

            case STATE_DISCONNECT :
                next = close();
                break;
        }

        if (next != STATE_NONE) {
            goNextState(next);
        }
    }

    /**
     * 네트워크 통신 동작 수행
     * @param next 수행해야 할 네트워크 통신 동작
     */
    private void goNextState(int next) {
        Message msg = obtainMessage(next);
        sendMessage(msg);
    }

    /**
     * 서버와의 통신 연결
     * @return 다음 수행해야할 통신 동작
     */
    private int open() {
        if (mPath == null || mPath.length() <= 0) {
            return VErrors.E_INVALID_PARAM;
        }

        // Network 연결 상태 확인
        boolean is_conn = isConnNetwork();
        if (!is_conn) {
            mError = VErrors.E_DISCONNECTED;
            return STATE_DISCONNECT;
        }

        // 통신 프로토콜에 따라 통신 방법을 달리한다.
        String sync = "DownHttps";
        if (mAddr.startsWith("http://")) {
            sync = "DownHttp";
        }
        else if (mAddr.startsWith("https://")) {
            sync = "DownHttps";
        }

        mNetDownManager = new NetDownManager(sync);
        mError = mNetDownManager.connect(mAddr);

        return mError == VErrors.E_NONE ? STATE_RECV : STATE_DISCONNECT;
    }

    /**
     * 서버로부터 응답 내용 수신
     * @return 다음 수행해야할 통신 동작
     */
    private int receive() {
        mError = mNetDownManager.recv(mPath, mSyncListener);
        return STATE_DISCONNECT;
    }

    /**
     * 연결된 서버와의 연결 종료
     * @return 다음 수행해야할 통신 동작
     */
    private int close() {
        if (mNetDownManager != null) {
            mNetDownManager.close();
        }

        if (mListener != null) {
            mListener.onResult(mError);
        }

        return STATE_NONE;
    }

    /**
     * WiFi 또는 이동통신사 네트워크 망에 연결되어 있는지 확인한다.
     * @return true : 네트워크 망에 연결, false : 네트워크 망에 미연결
     */
    private boolean isConnNetwork() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo network_info = manager.getActiveNetworkInfo();
        if (network_info == null) {
            return false;
        }

        boolean is_available = network_info.isAvailable();
        boolean is_connected = network_info.isConnected();

        if (is_available == true || is_connected == true) {
            return true;
        }

        return false;
    }

    /**
     * 서버로부터 파일 수신 상황을 받기 위한 리스너
     */
    private INetDownListener mSyncListener = new INetDownListener() {
        @Override
        public void onProgress(int progress) {
            if (mListener != null) {
                mListener.onProgress(progress);
            }
        }
    };
}
