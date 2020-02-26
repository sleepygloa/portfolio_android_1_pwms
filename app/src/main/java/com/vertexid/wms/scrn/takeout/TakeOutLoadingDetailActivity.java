package com.vertexid.wms.scrn.takeout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.barcode.IScannerListener;
import com.vertexid.wms.barcode.ScannerManager;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.info.TakeOutLoadingInfo;
import com.vertexid.wms.network.INetworkListener;
import com.vertexid.wms.network.INetworkManager;
import com.vertexid.wms.network.NError;
import com.vertexid.wms.network.NetworkManager;
import com.vertexid.wms.network.NetworkParam;
import com.vertexid.wms.scrn.BaseActivity;
import com.vertexid.wms.scrn.ScrnParam;
import com.vertexid.wms.scrn.carryin.CarryInActivity;
import com.vertexid.wms.scrn.carryout.CarryOutActivity;
import com.vertexid.wms.scrn.inventory.InventoryActivity;
import com.vertexid.wms.scrn.search.SearchActivity;
import com.vertexid.wms.scrn.setting.SettingActivity;
import com.vertexid.wms.scrn.takeout.adapter.LoadingDetailGoodsItemAdapter;
import com.vertexid.wms.scrn.takeout.adapter.LoadingDetailPltItemAdapter;
import com.vertexid.wms.scrn.warehouse.WareHouseActivity;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VLog;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 출고상차 상세화면
 */
public class TakeOutLoadingDetailActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_PLT_LIST_RESULT = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_LOADING_RESULT = HANDLER_SHOW_PLT_LIST_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_LOADING_RESULT + 1;

    private DrawerLayout mDrawerLayout = null;
    private Context mContext = null;

    private EditText mEditDelivery;
    private EditText mEditPickingBox;
    private EditText mEditPickingEa;

    private ProgressDialog mPrgDlg = null;

    private ArrayList<TakeOutLoadingInfo> mArray = null;
    private ArrayList<TakeOutLoadingInfo.PltInfo> mSubArray = null;

    private TakeOutLoadingInfo mInfo = null;

    private LoadingDetailPltItemAdapter mSubAdapter = null;

    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_out_loading_list_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        Intent intent = getIntent();
        if (intent != null) {
//            mPosition = intent.getIntExtra(ScrnParam.INFO_POSITION, 0);
            mArray = (ArrayList<TakeOutLoadingInfo>) intent.getSerializableExtra(ScrnParam.INFO_LIST);
        }

        if (mArray == null) {
            mArray = new ArrayList<>();
            mArray.clear();
        }

        if (mSubArray == null) {
            mSubArray = new ArrayList<>();
            mSubArray.clear();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.take_out_loading_list_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.take_out_loading_list_navi_view);
        navi_view.setNavigationItemSelectedListener(mNavigationListener);
        View nav_header_view = navi_view.getHeaderView(0);

        IConfigManager config = new ConfigManager(mContext);
        String user_id = config.getUserId();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        TextView text_menu_user = (TextView) nav_header_view.findViewById(R.id.menu_text_user);
        TextView text_menu_center = (TextView) nav_header_view.findViewById(R.id.menu_text_center);
        TextView text_menu_customer = (TextView) nav_header_view.findViewById(R.id.menu_text_customer);
        text_menu_user.setText(user_id);
        text_menu_center.setText(center.getText());
        text_menu_customer.setText(customer.getText());

        TextView text_user = (TextView) findViewById(R.id.take_out_loading_list_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        Button btn_menu = (Button) findViewById(R.id.take_out_loading_list_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        Button btn_loading = (Button) findViewById(R.id.take_out_loading_list_btn_loading);
        btn_loading.setOnClickListener(mClickListener);

        mEditDelivery = (EditText) findViewById(R.id.take_out_loading_list_edit_delivery);
        mEditPickingBox = (EditText) findViewById(R.id.take_out_loading_list_edit_picking_count_box);
        mEditPickingEa = (EditText) findViewById(R.id.take_out_loading_list_edit_picking_count_ea);

        String delivery = mArray.get(0).getDelivery();
        int picking_box = mArray.get(0).getPickingBoxCount();
        int picking_ea = mArray.get(0).getPickingEaCount();

        mEditDelivery.setText(delivery);
        mEditPickingBox.setText(String.valueOf(picking_box));
        mEditPickingEa.setText(String.valueOf(picking_ea));

        TextView text_total_count = (TextView) findViewById(R.id.take_out_loading_list_text_total_count);
        text_total_count.setText("총 " + mArray.size() + "건");

        LoadingDetailGoodsItemAdapter adapter = new LoadingDetailGoodsItemAdapter(mContext, mArray);
        ListView list_view = (ListView) findViewById(R.id.take_out_loading_list_info_list);
        list_view.setOnItemClickListener(mItemClickListener);
        list_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mSubAdapter = new LoadingDetailPltItemAdapter(mContext, mSubArray);
        ListView list_view_sub = (ListView) findViewById(R.id.take_out_loading_list_plt_list);
        list_view_sub.setAdapter(mSubAdapter);
        list_view_sub.setItemsCanFocus(true);
        mSubAdapter.notifyDataSetChanged();

        reqPalletList(0);
    }

    @Override
    protected void closeActivity() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mScan == null) {
            mScan = ScannerManager.getInst(mContext);
        }

        mScan.setListener(mScannerListener);
    }

    @Override
    public void onBackPressed() {
        boolean is_open = mDrawerLayout.isDrawerOpen(GravityCompat.START);
        if (is_open) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    /**
     * 사용자에게 알림
     * @param text 사용자에게 알릴 내용
     */
    private void showPopup(String text) {
        if (text == null || text.length() <= 0) {
            return ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text);
        builder.setPositiveButton("확인", null);
        builder.show();
    }

    /**
     * 통신 시 사용자에게 통신 중임을 알리는 팝업
     * @param is_show true : 프로그레스 팝업 사용, false : 프로그레스 팝업 삭제
     */
    private void showProgress(boolean is_show, String text) {
        if (is_show) {
            if (mPrgDlg != null) {
                if (mPrgDlg.isShowing()) {
                    mPrgDlg.dismiss();
                }

                mPrgDlg = null;
            }

            mPrgDlg = new ProgressDialog(mContext);
            mPrgDlg.setMessage(text);
            mPrgDlg.setCanceledOnTouchOutside(false);
            mPrgDlg.setCancelable(false);
            mPrgDlg.show();
        }
        else {
            if (mPrgDlg != null) {
                if (mPrgDlg.isShowing()) {
                    mPrgDlg.dismiss();
                }

                mPrgDlg = null;
            }
        }
    }

    private void showScanResult(String scan) {
        for (int i = 0 ; i < mSubArray.size() ; i++) {
            TakeOutLoadingInfo.PltInfo info = mSubArray.get(i);
            String plt_id = info.getPickingPltId();

            if (scan.equals(plt_id)) {
                info.setLoadingPltId(plt_id);
                mSubArray.set(i, info);
                break;
            }
        }

        mSubAdapter.notifyDataSetChanged();
    }

    private void showInfo(int position) {
        String delivery = mArray.get(position).getDelivery();
        int picking_box = mArray.get(position).getPickingBoxCount();
        int picking_ea = mArray.get(position).getPickingEaCount();

        mEditDelivery.setText(delivery);
        mEditPickingBox.setText(String.valueOf(picking_box));
        mEditPickingEa.setText(String.valueOf(picking_ea));

        reqPalletList(position);
    }

    /**
     * 제품 목록에서 선택한 제품의 파렛트 목록 요청 결과를 처리
     * @param json 제품 목록에서 선택한 제품의 파렛트 목록 요청 결과
     */
    private void showPalletListResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        if (mSubArray == null) {
            mSubArray = new ArrayList<>();
        }

        mSubArray.clear();

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        JSONArray json_array = (JSONArray) VUtil.getValue(json, NetworkParam.ARRAY_LIST);
        if (json_array == null || json_array.length() == 0) {
            showPopup(getString(R.string.not_exist_list));
            return ;
        }

        try {
            for (int i = 0 ; i < json_array.length() ; i++) {
                JSONObject json_obj = json_array.getJSONObject(i);
                if (json_obj == null || json_obj.length() == 0) {
                    continue;
                }

                Object obj = VUtil.getValue(json_obj, NetworkParam.PLT_ID);
                String plt_id = "";
                if (obj != null && !obj.toString().equals("null")) {
                    plt_id = obj.toString();
                }

                TakeOutLoadingInfo loading = new TakeOutLoadingInfo();
                TakeOutLoadingInfo.PltInfo info = loading.new PltInfo();
                info.setPickingPltId(plt_id);

                mSubArray.add(info);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // 기존 HashMap 내용 제거
        mSubAdapter.clear();
        // 리스트뷰 갱신
        mSubAdapter.notifyDataSetChanged();
    }

    /**
     * 상차요청 결과를 처리
     * @param json 상차요청 결과
     */
    private void showLoadingResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        showPopup(getString(R.string.loading_complete));
    }

    /**
     * 로그 아웃 결과를 처리
     * @param json 로그아웃 결과
     */
    private void showLogOutResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        Intent closeActivity = new Intent(ACTION_ACTIVITY_CLOSE);
        closeActivity.setPackage(getPackageName());
        sendBroadcast(closeActivity);
    }

    /**
     * 수행해야 할 동작으로 이동
     * @param state 수행해야 할 동작
     * @param obj 전달해야 할 내용
     */
    private void nextState(int state, Object obj) {
        Message msg = new Message();
        msg.what = state;
        msg.obj = obj;

        mHandler.sendMessage(msg);
    }

    /**
     * 다음 화면으로 이동
     *
     * @param cls 이동할 activity 클래스
     */
    private void moveNextActivity(Class<?> cls) {
        Intent i = new Intent();
        i.setClass(mContext, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    /**
     * 제품 목록에서 선택한 제품의 파렛트 목록 요청
     * @param position 선택한 제품의 인덱스
     */
    private void reqPalletList(int position) {
        mInfo = mArray.get(position);
        if (mInfo == null) {
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        TakeOutLoadingInfo info = mArray.get(position);
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setUserId(user_id);

        showProgress(true, getString(R.string.progress_req_list));

        INetworkManager network = new NetworkManager(mContext, mPltListNetworkListener);
        network.reqTakeOutLoadingPltList(info);
    }

    /**
     * 상차 요청
     */
    private void reqCarLoading() {
        HashMap<Integer, String> hash = mSubAdapter.getLoadingInfo();
        if (hash == null) {
            return ;
        }

        Iterator iterator = hash.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Integer key = (Integer) entry.getKey();
            String value = hash.get(key);

            TakeOutLoadingInfo.PltInfo info = mSubArray.get(key);
            info.setLoadingPltId(value);
            mSubArray.set(key, info);
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        mInfo.setCompanyCode(company.getCode());
        mInfo.setCenterCode(center.getCode());
        mInfo.setCustomerCode(customer.getCode());
        mInfo.setUserId(user_id);

        String take_out_number = mInfo.getTakeOutNumber();
        int detail_number = mInfo.getDetailNumber();
        String pick_number = mInfo.getPickingNumber();

        JSONArray json_array = new JSONArray();
        try {
            for (int i = 0 ; i < mSubArray.size() ; i++) {
                TakeOutLoadingInfo.PltInfo info = mSubArray.get(i);
                String loading_plt_id = info.getLoadingPltId();
                if (loading_plt_id == null || loading_plt_id.length() == 0) {
                    showPopup(getString(R.string.not_exist_loading_plt_id));
                    return;
                }

                String picking_plt_id = info.getPickingPltId();
                if (!loading_plt_id.equals(picking_plt_id)) {
                    showPopup(getString(R.string.not_equal_picking_plt_id_scan));
                    return ;
                }

                JSONObject json_obj = new JSONObject();
                json_obj.put(NetworkParam.TAKE_OUT_NUMBER, take_out_number);
                json_obj.put(NetworkParam.TAKE_OUT_DETAIL_NUMBER, detail_number);
                json_obj.put(NetworkParam.PICKING_NUMBER, pick_number);
                json_obj.put(NetworkParam.CAR_PLT_ID, loading_plt_id);
                json_obj.put(NetworkParam.USER_ID, user_id);

                json_array.put(json_obj);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        showProgress(true, getString(R.string.progress_loading));

        INetworkManager network = new NetworkManager(mContext, mLoadingNetworkListener);
        network.reqTakeOutCarLoading(mInfo, json_array);
    }

    /**
     * 로그아웃 요청
     */
    private void reqLogOut() {
        IConfigManager config = new ConfigManager(mContext);
        String user_id = config.getUserId();

        showProgress(true, getString(R.string.progress_logout));

        INetworkManager network = new NetworkManager(mContext, mLogOutNetworkListener);
        network.reqLogOut(user_id);
    }

    /**
     * 파렛트 목록 요청 결과 수신 리스너
     */
    private INetworkListener mPltListNetworkListener = new INetworkListener() {
        @Override
        public void onRespResult(int error, String payload) {
            showProgress(false, "");

            if (error != VErrors.E_NONE) {
                String text = "";
                if (error == VErrors.E_DISCONNECTED) {
                    text = getString(R.string.net_error_not_connect);
                }
                else if (error == VErrors.E_UNKNOWN_HOST) {
                    text = getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_NOT_CONNECTED) {
                    text = getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_TIME_OUT) {
                    text = getString(R.string.net_error_time_out);
                }
                else if (error == VErrors.E_SEND_TO_SERVER) {
                    text = getString(R.string.net_error_fail_send);
                }
                else if (error == VErrors.E_RECV_FROM_SERVER) {
                    text = getString(R.string.net_error_fail_recv);
                }
                else if (error == VErrors.E_ETC) {
                    text = getString(R.string.net_error_etc);
                }

                nextState(HANDLER_SHOW_NET_ERROR, text);
                return ;
            }

            VLog.d("resp : " + payload);

            if (payload == null || payload.length() <= 0) {
                nextState(HANDLER_SHOW_NET_ERROR, getString(R.string.net_error_fail_recv));
                return ;
            }

            JSONObject json = VUtil.convertToJSON(payload);
            if (json == null) {
                nextState(HANDLER_SHOW_NET_ERROR, getString(R.string.net_error_invalid_payload));
                return ;
            }

            nextState(HANDLER_SHOW_PLT_LIST_RESULT, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

    /**
     * 상차 요청 결과 수신 리스너
     */
    private INetworkListener mLoadingNetworkListener = new INetworkListener() {
        @Override
        public void onRespResult(int error, String payload) {
            showProgress(false, "");

            if (error != VErrors.E_NONE) {
                String text = "";
                if (error == VErrors.E_DISCONNECTED) {
                    text = getString(R.string.net_error_not_connect);
                }
                else if (error == VErrors.E_UNKNOWN_HOST) {
                    text = getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_NOT_CONNECTED) {
                    text = getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_TIME_OUT) {
                    text = getString(R.string.net_error_time_out);
                }
                else if (error == VErrors.E_SEND_TO_SERVER) {
                    text = getString(R.string.net_error_fail_send);
                }
                else if (error == VErrors.E_RECV_FROM_SERVER) {
                    text = getString(R.string.net_error_fail_recv);
                }
                else if (error == VErrors.E_ETC) {
                    text = getString(R.string.net_error_etc);
                }

                nextState(HANDLER_SHOW_NET_ERROR, text);
                return ;
            }

            VLog.d("resp : " + payload);

            if (payload == null || payload.length() <= 0) {
                nextState(HANDLER_SHOW_NET_ERROR, getString(R.string.net_error_fail_recv));
                return ;
            }

            JSONObject json = VUtil.convertToJSON(payload);
            if (json == null) {
                nextState(HANDLER_SHOW_NET_ERROR, getString(R.string.net_error_invalid_payload));
                return ;
            }

            nextState(HANDLER_SHOW_LOADING_RESULT, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

    /**
     * 로그아웃 요청 결과 수신 리스너
     */
    private INetworkListener mLogOutNetworkListener = new INetworkListener() {
        @Override
        public void onRespResult(int error, String payload) {
            showProgress(false, "");

            if (error != VErrors.E_NONE) {
                String text = "";
                if (error == VErrors.E_DISCONNECTED) {
                    text = getString(R.string.net_error_not_connect);
                }
                else if (error == VErrors.E_UNKNOWN_HOST) {
                    text = getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_NOT_CONNECTED) {
                    text = getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_TIME_OUT) {
                    text = getString(R.string.net_error_time_out);
                }
                else if (error == VErrors.E_SEND_TO_SERVER) {
                    text = getString(R.string.net_error_fail_send);
                }
                else if (error == VErrors.E_RECV_FROM_SERVER) {
                    text = getString(R.string.net_error_fail_recv);
                }
                else if (error == VErrors.E_ETC) {
                    text = getString(R.string.net_error_etc);
                }

                nextState(HANDLER_SHOW_NET_ERROR, text);
                return ;
            }

            VLog.d("resp : " + payload);

            if (payload == null || payload.length() <= 0) {
                nextState(HANDLER_SHOW_NET_ERROR, getString(R.string.net_error_fail_recv));
                return ;
            }

            JSONObject json = VUtil.convertToJSON(payload);
            if (json == null) {
                nextState(HANDLER_SHOW_NET_ERROR, getString(R.string.net_error_invalid_payload));
                return ;
            }

            nextState(HANDLER_SHOW_LOGOUT_RESULT, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

    private IScannerListener mScannerListener = new IScannerListener() {
        @Override
        public void onRecvScan(String scan) {
            if (scan == null || scan.length() == 0) {
                return ;
            }

            nextState(HANDLER_SHOW_SCAN_RESULT, scan);
        }
    };

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showInfo(position);
        }
    };

    private NavigationView.OnNavigationItemSelectedListener mNavigationListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mDrawerLayout.closeDrawer(GravityCompat.START);

            int id = item.getItemId();
            if (id == R.id.menu_warehouse) {
                moveNextActivity(WareHouseActivity.class);
            }
            else if (id == R.id.menu_takeout) {
                moveNextActivity(TakeOutActivity.class);
            }
            else if (id == R.id.menu_carry_in) {
                moveNextActivity(CarryInActivity.class);
            }
            else if (id == R.id.menu_carry_out) {
                moveNextActivity(CarryOutActivity.class);
            }
            else if (id == R.id.menu_inventory) {
                moveNextActivity(InventoryActivity.class);
            }
            else if (id == R.id.menu_search) {
                moveNextActivity(SearchActivity.class);
            }
            else if (id == R.id.menu_setting) {
                moveNextActivity(SettingActivity.class);
            }
            else if (id == R.id.menu_logout) {
                reqLogOut();
            }

            return true;
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.take_out_loading_list_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.take_out_loading_list_btn_loading) {
                reqCarLoading();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SHOW_NET_ERROR :
                    showPopup((String) msg.obj);
                    break;

                case HANDLER_SHOW_LOGOUT_RESULT :
                    showLogOutResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_PLT_LIST_RESULT :
                    showPalletListResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_LOADING_RESULT :
                    showLoadingResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_SCAN_RESULT :
                    showScanResult((String) msg.obj);
                    break;
            }
        }
    };
}
