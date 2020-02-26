package com.vertexid.wms.network;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

import com.vertexid.wms.R;
import com.vertexid.wms.info.CarryInInfo;
import com.vertexid.wms.info.CarryNumberInfo;
import com.vertexid.wms.info.CarryOutInfo;
import com.vertexid.wms.info.InquiryInfo;
import com.vertexid.wms.info.InvenInquiryInfo;
import com.vertexid.wms.info.InvenMoveInfo;
import com.vertexid.wms.info.InvenPltInfo;
import com.vertexid.wms.info.InventoryStateChangeInfo;
import com.vertexid.wms.info.SerialScanInfo;
import com.vertexid.wms.info.TakeOutInfo;
import com.vertexid.wms.info.TakeOutLoadingInfo;
import com.vertexid.wms.info.WareHouseInfo;
import com.vertexid.wms.info.WareHouseNumberInfo;
import com.vertexid.wms.network.handler.INetCommHandlerListener;
import com.vertexid.wms.network.handler.INetDownHandlerListener;
import com.vertexid.wms.network.handler.NetCommHandler;
import com.vertexid.wms.network.handler.NetDownHandler;
import com.vertexid.wms.network.protocol.ArrivalCheckProtocol;
import com.vertexid.wms.network.protocol.ArrivalConfirmProtocol;
import com.vertexid.wms.network.protocol.ArrivalListProtocol;
import com.vertexid.wms.network.protocol.CarLoadingProtocol;
import com.vertexid.wms.network.protocol.CarryInCheckProtocol;
import com.vertexid.wms.network.protocol.CarryInConfirmProtocol;
import com.vertexid.wms.network.protocol.CarryInListProtocol;
import com.vertexid.wms.network.protocol.CarryInNumberProtocol;
import com.vertexid.wms.network.protocol.CarryInPileUpConfirmProtocol;
import com.vertexid.wms.network.protocol.CarryInPileUpGoodsListProtocol;
import com.vertexid.wms.network.protocol.CarryInPileUpSearchProtocol;
import com.vertexid.wms.network.protocol.CarryOutListProtocol;
import com.vertexid.wms.network.protocol.CarryOutNumberProtocol;
import com.vertexid.wms.network.protocol.CarryOutPickingConfirmProtocol;
import com.vertexid.wms.network.protocol.GoodsInquiryProtocol;
import com.vertexid.wms.network.protocol.GoodsLocProtocol;
import com.vertexid.wms.network.protocol.InvenCreateProtocol;
import com.vertexid.wms.network.protocol.InvenCreateSearchProtocol;
import com.vertexid.wms.network.protocol.InvenInquiryConfirmProtocol;
import com.vertexid.wms.network.protocol.InvenInquiryListProtocol;
import com.vertexid.wms.network.protocol.InvenMoveConfirmProtocol;
import com.vertexid.wms.network.protocol.InvenMoveListProtocol;
import com.vertexid.wms.network.protocol.InvenPltDivideProtocol;
import com.vertexid.wms.network.protocol.InvenPltDivideSearchProtocol;
import com.vertexid.wms.network.protocol.InvenPltMergeProtocol;
import com.vertexid.wms.network.protocol.InvenStateChangeConfirmProtocol;
import com.vertexid.wms.network.protocol.InvenStateChangeListProtocol;
import com.vertexid.wms.network.protocol.InvenTempMoveConfirmProtocol;
import com.vertexid.wms.network.protocol.InvenTempMoveLocSearchProtocol;
import com.vertexid.wms.network.protocol.InvenTempMoveProtocol;
import com.vertexid.wms.network.protocol.LogOutProtocol;
import com.vertexid.wms.network.protocol.LoginProtocol;
import com.vertexid.wms.network.protocol.TakeOutDivideDetailListProtocol;
import com.vertexid.wms.network.protocol.TakeOutDivideListProtocol;
import com.vertexid.wms.network.protocol.TakeOutListProtocol;
import com.vertexid.wms.network.protocol.TakeOutLoadingDetailProtocol;
import com.vertexid.wms.network.protocol.TakeOutLoadingListProtocol;
import com.vertexid.wms.network.protocol.TakeOutLoadingPltProtocol;
import com.vertexid.wms.network.protocol.TakeOutNumberProtocol;
import com.vertexid.wms.network.protocol.TakeOutPickingListConfirmProtocol;
import com.vertexid.wms.network.protocol.TakeOutPickingTotalConfirmProtocol;
import com.vertexid.wms.network.protocol.TakeOutPickingTotalListProtocol;
import com.vertexid.wms.network.protocol.TakeOutSerialScanConfirmProtocol;
import com.vertexid.wms.network.protocol.TakeOutSerialScanProtocol;
import com.vertexid.wms.network.protocol.WareHouseNumberProtocol;
import com.vertexid.wms.network.protocol.WarePileUpConfirmProtocol;
import com.vertexid.wms.network.protocol.WarePileUpProtocol;
import com.vertexid.wms.network.protocol.WarePileUpSearchProtocol;
import com.vertexid.wms.network.protocol.WareSerialScanConfirmProtocol;
import com.vertexid.wms.network.protocol.WareSerialScanProtocol;
import com.vertexid.wms.network.protocol.WaveNumberProtocol;
import com.vertexid.wms.scrn.takeout.info.WaveNumberInfo;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONArray;

/**
 * 서버와의 통신을 위한 외부 공개 API 구현
 */
public class NetworkManager implements INetworkManager {
    private Context mContext = null;
    private Looper mLooper = null;

    private INetworkListener mListener = null;

    public NetworkManager(Context context, INetworkListener listener) {
        mContext = context;
        mListener = listener;
        mLooper = createHandlerThread(context.getPackageName() + "_thread");
    }

    private Looper createHandlerThread(String name) {
        HandlerThread thread = new HandlerThread(name, Process.THREAD_PRIORITY_BACKGROUND);
        thread.setDaemon(true);
        thread.start();
        return thread.getLooper();
    }

    private INetCommHandlerListener mHandlerListener = new INetCommHandlerListener() {
        @Override
        public void onResponse(int error, String payload) {
            if (mListener != null) {
                mListener.onRespResult(error, payload);
            }
        }
    };

    private INetDownHandlerListener mNetDownHandlerListener = new INetDownHandlerListener() {
        @Override
        public void onProgress(int progress) {
            if (mListener != null) {
                mListener.onProgress(progress);
            }
        }

        @Override
        public void onResult(int error) {
            if (mListener != null) {
                mListener.onResult(error);
            }
        }
    };

    @Override
    public int reqLogIn(String id, String password) {
        if (id == null || id.length() <= 0) {
            return VErrors.E_INVALID_PARAM;
        }

        if (password == null || password.length() <= 0) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        LoginProtocol protocol = new LoginProtocol(id, password);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqLogOut(String id) {
        if (id == null || id.length() <= 0) {
            return VErrors.E_INVALID_PARAM;
        }

        String device_id = VUtil.getDeviceId(mContext);

        // 1. Protocol 생성
        LogOutProtocol protocol = new LogOutProtocol(id, device_id);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqApkDownLoad(String path) {
        if (path == null || path.length() <= 0) {
            return VErrors.E_INVALID_PARAM;
        }

        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), mContext.getString(R.string.apk_path));

        NetDownHandler handler = new NetDownHandler(mContext, mLooper, svr_url, path);
        handler.start(mNetDownHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqArrivalList(WareHouseInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        ArrivalListProtocol protocol = new ArrivalListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqWareHouseNumberList(WareHouseNumberInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        WareHouseNumberProtocol protocol = new WareHouseNumberProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqArrivalCheck(WareHouseInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        ArrivalCheckProtocol protocol = new ArrivalCheckProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqArrivalConfirm(WareHouseInfo info, JSONArray json_array) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        ArrivalConfirmProtocol protocol = new ArrivalConfirmProtocol(info, json_array);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqWarePileUpSearch(WareHouseInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        WarePileUpProtocol protocol = new WarePileUpProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqWarePileUpGoodsSearch(WareHouseInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        WarePileUpSearchProtocol protocol = new WarePileUpSearchProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqWarePileUpConfirm(WareHouseInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        WarePileUpConfirmProtocol protocol = new WarePileUpConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqWareSerialScanList(SerialScanInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        WareSerialScanProtocol protocol = new WareSerialScanProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqWareSerialScanConfirm(SerialScanInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        WareSerialScanConfirmProtocol protocol = new WareSerialScanConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutNumberList(TakeOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutNumberProtocol protocol = new TakeOutNumberProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutPickingList(TakeOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutListProtocol protocol = new TakeOutListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutPickingListConfirm(TakeOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutPickingListConfirmProtocol protocol = new TakeOutPickingListConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqWaveNumberList(WaveNumberInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        WaveNumberProtocol protocol = new WaveNumberProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutPickingTotalList(TakeOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutPickingTotalListProtocol protocol = new TakeOutPickingTotalListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutPickingTotalConfirm(TakeOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutPickingTotalConfirmProtocol protocol = new TakeOutPickingTotalConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutDivideList(TakeOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutDivideListProtocol protocol = new TakeOutDivideListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutDivideDetailList(TakeOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutDivideDetailListProtocol protocol = new TakeOutDivideDetailListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutSerialScanlList(TakeOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutSerialScanProtocol protocol = new TakeOutSerialScanProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutSerialScanConfirm(SerialScanInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutSerialScanConfirmProtocol protocol = new TakeOutSerialScanConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutLoadingList(TakeOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutLoadingListProtocol protocol = new TakeOutLoadingListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutLoadingDetailList(TakeOutLoadingInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutLoadingDetailProtocol protocol = new TakeOutLoadingDetailProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutLoadingPltList(TakeOutLoadingInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        TakeOutLoadingPltProtocol protocol = new TakeOutLoadingPltProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqTakeOutCarLoading(TakeOutLoadingInfo info, JSONArray array) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarLoadingProtocol protocol = new CarLoadingProtocol(info, array);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqCarryInList(CarryInInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarryInListProtocol protocol = new CarryInListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqCarryInCheck(CarryInInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarryInCheckProtocol protocol = new CarryInCheckProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqCarryInConfirm(CarryInInfo info, JSONArray array) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarryInConfirmProtocol protocol = new CarryInConfirmProtocol(info, array);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqCarryInNumberList(CarryNumberInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarryInNumberProtocol protocol = new CarryInNumberProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqCarryInPileUpSearch(CarryInInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarryInPileUpSearchProtocol protocol = new CarryInPileUpSearchProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqCarryInPileUpGoodsList(CarryInInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarryInPileUpGoodsListProtocol protocol = new CarryInPileUpGoodsListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqCarryInPileUpConfirm(CarryInInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarryInPileUpConfirmProtocol protocol = new CarryInPileUpConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqCarryOutNumberList(CarryNumberInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarryOutNumberProtocol protocol = new CarryOutNumberProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqCarryOutList(CarryOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarryOutListProtocol protocol = new CarryOutListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqCarryOutPickingConfirm(CarryOutInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        CarryOutPickingConfirmProtocol protocol = new CarryOutPickingConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenMoveList(InvenMoveInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenMoveListProtocol protocol = new InvenMoveListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenMoveConfirm(InvenMoveInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenMoveConfirmProtocol protocol = new InvenMoveConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenTempMoveSearch(InvenMoveInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenTempMoveProtocol protocol = new InvenTempMoveProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenTempMoveLocSearch(InvenMoveInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenTempMoveLocSearchProtocol protocol = new InvenTempMoveLocSearchProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenTempMoveConfirm(InvenMoveInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenTempMoveConfirmProtocol protocol = new InvenTempMoveConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenInquiryList(InvenInquiryInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenInquiryListProtocol protocol = new InvenInquiryListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenInquiryConfirm(InvenInquiryInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenInquiryConfirmProtocol protocol = new InvenInquiryConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenCreate(InvenInquiryInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenCreateProtocol protocol = new InvenCreateProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenCreateSearch(InvenInquiryInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenCreateSearchProtocol protocol = new InvenCreateSearchProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenStateChangeList(InventoryStateChangeInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenStateChangeListProtocol protocol = new InvenStateChangeListProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenStateChangeConfirm(InventoryStateChangeInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenStateChangeConfirmProtocol protocol = new InvenStateChangeConfirmProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenPalletDivideList(InvenPltInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenPltDivideSearchProtocol protocol = new InvenPltDivideSearchProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenPalletDivide(InvenPltInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenPltDivideProtocol protocol = new InvenPltDivideProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqInvenPalletMerge(InvenPltInfo info) {
        if (info == null) {
            return VErrors.E_INVALID_PARAM;
        }

        // 1. Protocol 생성
        InvenPltMergeProtocol protocol = new InvenPltMergeProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqGoodsInquiry(InquiryInfo info) {
        // 1. Protocol 생성
        GoodsInquiryProtocol protocol = new GoodsInquiryProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }

    @Override
    public int reqGoodsLocInquiry(InquiryInfo info) {
        // 1. Protocol 생성
        GoodsLocProtocol protocol = new GoodsLocProtocol(info);
        String payload = protocol.getReqPayload();

        // 2. URL 생성
        String method = protocol.getReqMethod();
        String svr_url = String.format("%s%s", mContext.getString(R.string.svr_addr), method);

        // 3. 서버와 실제 통신 시작
        NetCommHandler handler = new NetCommHandler(mContext, mLooper, svr_url, payload);
        handler.start(mHandlerListener);

        return VErrors.E_NONE;
    }
}
