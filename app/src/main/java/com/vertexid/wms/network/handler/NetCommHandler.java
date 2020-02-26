package com.vertexid.wms.network.handler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.vertexid.wms.network.comm.CommManager;
import com.vertexid.wms.network.comm.ICommManager;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VLog;

/**
 * 서버와의 통신을 수행한다.
 */
public class NetCommHandler extends Handler {
    /** 핸들러 미동작 */
    private final int STATE_NONE		= 0;
    /** 서버에 연결 중 */
    private final int STATE_CONNECT		= STATE_NONE + 1;
    /** 연결된 서버로 데이터 전송 */
    private final int STATE_SEND		= STATE_CONNECT + 1;
    /** 서버로부터 데이터 수신 */
    private final int STATE_RECV		= STATE_SEND + 1;
    /** 서버와의 연결 해제 */
    private final int STATE_DISCONNECT	= STATE_RECV + 1;
    /** 서버와의 통신 결과를 통지 */
    private final int STATE_RESPONSE	= STATE_DISCONNECT + 1;

    private Context mContext = null;

    private ICommManager mCommManager = null;
    private INetCommHandlerListener mListener = null;

    /** 서버 Host */
    private String mAddr = null;
    /** 서버에 전송할 데이터 */
    private String mReqPayload = null;
    /** 서버로부터 수신한 데이터 */
    private String mRespPayload = null;
    /** 통신 결과 */
    private int mError;

    public NetCommHandler(Context context, Looper looper, String addr, String payload) {
        super(looper);

        mContext = context;

        mAddr = addr;
        mReqPayload = payload;
    }

    /**
     * 서버와의 통신을 시작한다.
     * @param listener 통신 결과를 수신할 리스너
     */
    public void start(INetCommHandlerListener listener) {
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

            case STATE_SEND :
                next = send();
                break;

            case STATE_RECV :
                next = receive();
                break;

            case STATE_DISCONNECT :
                next = close();
                break;

            case STATE_RESPONSE :
                next = response();
                break;
        }

        if (next != STATE_NONE) {
            goNextState(next);
        }
    }

    /**
     * 서버와의 통신 상태 설정
     * @param next 수행해야 할 상태
     */
    private void goNextState(int next) {
        Message msg = obtainMessage(next);
        sendMessage(msg);
    }

    /**
     * 서버와의 통신을 위한 소켓 생성
     * @return 수행해야할 통신 동작
     */
    private int open() {
        // Network 연결 상태 확인
        boolean is_available = isConnNetwork();
        if (!is_available) {
            mError = VErrors.E_DISCONNECTED;
            return STATE_DISCONNECT;
        }

        String comm = "CommHttps";
        if (mAddr.startsWith("http://")) {
            comm = "CommHttp";
        }
        else if (mAddr.startsWith("https://")) {
            comm = "CommHttps";
        }

        mCommManager = new CommManager(comm);
        mError = mCommManager.connect(mAddr);
        VLog.d("NetCommHandler::open - url : " + mAddr + ", error : " + mError);

        return mError == VErrors.E_NONE ? STATE_SEND : STATE_DISCONNECT;
    }

    /**
     * 연결된 서버에 데이터 전송
     * @return 수행해야할 통신 동작
     */
    private int send() {
        mError = mCommManager.send(mReqPayload);
        VLog.d("NetCommHandler::send - payload : " + mReqPayload);
        VLog.d("NetCommHandler::send - error : " + mError);
        int resp_code = mCommManager.getRespCode();
        VLog.d("NetCommHandler::send - resp code : " + resp_code);
        return mError == VErrors.E_NONE ? STATE_RECV : STATE_DISCONNECT;
    }

    /**
     * 서버로부터 데이터를 수신한다.
     * @return 수행해야할 통신 동작
     */
    private int receive() {
        mError = mCommManager.recv();
        VLog.d("NetCommHandler::receive - error : " + mError);
        if (mError == VErrors.E_NONE) {
            mRespPayload = mCommManager.getRespPayload();
//            VLog.d("resp : " + mRespPayload);
        }

        return STATE_DISCONNECT;
    }

    /**
     * 서버와의 연결을 해제한다.
     * @return 수행해야할 통신 동작
     */
    private int close() {
        if (mCommManager != null) {
            mCommManager.close();
        }

        return STATE_RESPONSE;
    }

    /**
     * 서버와의 통신 결과를 통지한다.
     * @return 수행해야할 통신 동작
     */
    private int response() {
        if (mListener != null) {
            mListener.onResponse(mError, mRespPayload);
        }

        return STATE_NONE;
    }

    /**
     * WiFi 또는 이동통신사 네트워크 망에 연결되어 있는지 확인한다.
     * @return true : 네트워크 연결, false : 네트워크 미 연결
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
}
