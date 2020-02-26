package com.vertexid.wms.scrn.inventory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.barcode.IScannerListener;
import com.vertexid.wms.barcode.ScannerManager;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.info.InvenMoveInfo;
import com.vertexid.wms.network.INetworkListener;
import com.vertexid.wms.network.INetworkManager;
import com.vertexid.wms.network.NError;
import com.vertexid.wms.network.NetworkManager;
import com.vertexid.wms.network.NetworkParam;
import com.vertexid.wms.scrn.BaseActivity;
import com.vertexid.wms.scrn.ScrnParam;
import com.vertexid.wms.scrn.carryin.CarryInActivity;
import com.vertexid.wms.scrn.carryout.CarryOutActivity;
import com.vertexid.wms.scrn.search.SearchActivity;
import com.vertexid.wms.scrn.setting.SettingActivity;
import com.vertexid.wms.scrn.takeout.TakeOutActivity;
import com.vertexid.wms.scrn.warehouse.WareHouseActivity;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VLog;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 임의재고이동 화면
 */
public class InventoryTempMoveActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_SEARCH_RESULT = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_LOC_SEARCH_RESULT = HANDLER_SHOW_SEARCH_RESULT + 1;
    private final int HANDLER_SHOW_CONFIRM_RESULT = HANDLER_SHOW_LOC_SEARCH_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CONFIRM_RESULT + 1;

    private final int REQ_LIST = 100;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;

    private EditText mEditFromPltId = null;
    private EditText mEditFromLoc = null;
    private EditText mEditMoveLoc = null;

    private ProgressDialog mPrgDlg = null;

    private InvenMoveInfo mInfo = null;
    private ArrayList<InvenMoveInfo> mArrayLoc = null;

    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_temp_move_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.inventory_temp_move_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.inventory_temp_move_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.inventory_temp_move_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.inventory_temp_move_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);

        mEditFromPltId = (EditText) findViewById(R.id.inventory_temp_move_edit_from_plt_id);
        mEditFromLoc = (EditText) findViewById(R.id.inventory_temp_move_edit_from_loc);
        mEditMoveLoc = (EditText) findViewById(R.id.inventory_temp_move_edit_to_loc);

        mEditFromPltId.setOnEditorActionListener(mEditorActionListener);
        mEditFromLoc.setOnEditorActionListener(mEditorActionListener);
        mEditMoveLoc.setOnEditorActionListener(mEditorActionListener);

        mEditFromPltId.requestFocus();

        Button btn_loc_search = (Button) findViewById(R.id.inventory_temp_move_btn_loc_search);
        btn_loc_search.setOnClickListener(mClickListener);

        Button btn_search = (Button) findViewById(R.id.inventory_temp_move_btn_search);
        btn_search.setOnClickListener(mClickListener);
        Button btn_confirm = (Button) findViewById(R.id.inventory_temp_move_btn_confirm);
        btn_confirm.setOnClickListener(mClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQ_LIST) {
            return ;
        }

        if (resultCode != Activity.RESULT_OK) {
            return ;
        }

        InvenMoveInfo info = (InvenMoveInfo) data.getSerializableExtra(ScrnParam.INFO_DETAIL);
        if (info == null) {
            return ;
        }

        String plt_id = info.getFromPltId();
        String location = info.getFromLocation();

        mEditFromPltId.setText(plt_id);
        mEditFromLoc.setText(location);

        mEditMoveLoc.requestFocus();
    }

    @Override
    public void closeActivity() {
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
     * 사용자에게 보여줄 알림 팝업
     * @param text 팝업에 표시할 문구
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

    /**
     * 스캐너로 스캔한 내용 처리
     * @param scan 스캔 내용
     */
    private void showScanResult(String scan) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (mEditFromPltId.isFocused()) {
            mEditFromPltId.setText(scan);
            imm.hideSoftInputFromWindow(mEditFromPltId.getWindowToken(), 0);
            reqTempMoveSearch();
        }
        else if (mEditFromLoc.isFocused()) {
            mEditFromLoc.setText(scan);
            imm.hideSoftInputFromWindow(mEditFromLoc.getWindowToken(), 0);

            String plt_id = mEditFromPltId.getText().toString();
            if (plt_id == null || plt_id.length() == 0) {
                reqTempMoveLocSearch();
            }
        }
        else if (mEditMoveLoc.isFocused()) {
            mEditMoveLoc.setText(scan);
            imm.hideSoftInputFromWindow(mEditMoveLoc.getWindowToken(), 0);
            reqTempMoveConfirm();
        }
    }

    /**
     * 로그아웃 결과 처리
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
     * 파렛트 현재 위치 요청 결과 처리
     * @param json 파렛트 현재 위치 요청 결과
     */
    private void showLocSearchResult(JSONObject json) {
        if (json == null) {
            String text = getString(R.string.net_error_invalid_payload);
            showPopup(text);
            return ;
        }

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

        if (mArrayLoc == null) {
            mArrayLoc = new ArrayList<>();
        }

        mArrayLoc.clear();

        if (json_array.length() == 1) {
            try {
                JSONObject json_obj = json_array.getJSONObject(0);
                Object obj1 = VUtil.getValue(json_obj, NetworkParam.PLT_ID);
                String plt_id = "";
                if (obj1 != null || !obj1.toString().equals("null")) {
                    plt_id = obj1.toString();
                }

                mEditFromPltId.setText(plt_id);
            }
            catch (JSONException e) {
                e.printStackTrace();
                return ;
            }
        }
        else {
            try {
                for (int i = 0 ; i < json_array.length() ; i++) {
                    JSONObject json_obj = json_array.getJSONObject(i);
                    if (json_obj == null || json_obj.length() == 0) {
                        continue;
                    }

                    Object obj1 = VUtil.getValue(json_obj, NetworkParam.LOC_CD);
                    String loc = "";
                    if (obj1 != null && !obj1.toString().equals("null")) {
                        loc = obj1.toString();
                    }

                    Object obj2 = VUtil.getValue(json_obj, NetworkParam.PLT_ID);
                    String plt_id = "";
                    if (obj2 != null && !obj2.toString().equals("null")) {
                        plt_id = obj2.toString();
                    }

                    Object obj3 = VUtil.getValue(json_obj, NetworkParam.INVEN_MOVE_BOX_COUNT);
                    int box_count = 0;
                    if (obj3 != null && !obj3.toString().equals("null")) {
                        box_count = Integer.parseInt(obj3.toString());
                    }

                    Object obj4 = VUtil.getValue(json_obj, NetworkParam.INVEN_MOVE_EA_COUNT);
                    int ea_count = 0;
                    if (obj4 != null && !obj4.toString().equals("null")) {
                        ea_count = Integer.parseInt(obj4.toString());
                    }

                    InvenMoveInfo info = new InvenMoveInfo();
                    info.setFromLocation(loc);
                    info.setFromPltId(plt_id);
                    info.setInvenBoxCount(box_count);
                    info.setInvenEaCount(ea_count);

                    mArrayLoc.add(info);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            if (mArrayLoc != null && mArrayLoc.size() > 0) {
                moveListActivity();
            }
        }
    }

    /**
     * 임시이동 지시 요청 결과 처리
     * @param json 확인 요청 결과
     */
    private void showSearchResult(JSONObject json) {
        if (json == null) {
            String text = getString(R.string.net_error_invalid_payload);
            showPopup(text);
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        Object obj = VUtil.getValue(json, NetworkParam.LOC_CD);
        if (obj != null && !obj.toString().equals("null")) {
            mEditFromLoc.setText(obj.toString());
            mEditMoveLoc.requestFocus();
        }
//        else {
//            JSONArray json_array = (JSONArray) VUtil.getValue(json, NetworkParam.ARRAY_LIST);
//            if (json_array == null || json_array.length() == 0) {
//                return ;
//            }
//
//            if (json_array.length() == 1) {
//            }
//            else {
//            }
//        }

//        mBtnSearch.setBackgroundResource(R.drawable.search_middle_btn_selector);
//        mLayoutButtons.setVisibility(View.VISIBLE);
    }

    /**
     * 확인 요청 결과 처리
     * @param json 확인 요청 결과
     */
    private void showConfirmResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            showPopup((String) VUtil.getValue(json, NetworkParam.RESULT_MSG));
            return ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_complete));
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mEditFromPltId.setText("");
                mEditFromLoc.setText("");
                mEditMoveLoc.setText("");
            }
        });
        builder.show();
    }

    /**
     * 다음 화면으로 이동
     * @param cls 이동할 activity 클래스
     */
    private void moveNextActivity(Class<?> cls) {
        Intent i = new Intent();
        i.setClass(mContext, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    /**
     * 조회 화면으로 이동
     */
    private void moveListActivity() {
        Intent i = new Intent();
        i.setClass(mContext, InventoryTempMoveLocActivity.class);
        i.putExtra(ScrnParam.INFO_LIST, mArrayLoc);
        startActivityForResult(i, REQ_LIST);
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
     * 임시이동 지시 요청
     */
    private void reqTempMoveSearch() {
        String plt_id = mEditFromPltId.getText().toString();
        if (plt_id == null || plt_id.length() == 0) {
            showPopup(getString(R.string.not_exist_plt_id));
            return ;
        }

        String from_loc = mEditFromLoc.getText().toString();
        String to_loc = mEditMoveLoc.getText().toString();

        IConfigManager config = new ConfigManager(mContext);
        String user_id = config.getUserId();
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        mInfo = new InvenMoveInfo();
        mInfo.setUserId(user_id);
        mInfo.setCompanyCode(company.getCode());
        mInfo.setCenterCode(center.getCode());
        mInfo.setCustomerCode(customer.getCode());

        mInfo.setFromPltId(plt_id);
        mInfo.setFromLocation(from_loc);
        mInfo.setToLocation(to_loc);

        showProgress(true, getString(R.string.progress_search));

        INetworkManager network = new NetworkManager(mContext, mSearchNetworkListener);
        network.reqInvenTempMoveSearch(mInfo);
    }

    /**
     * 대상 PLT의 위치 조회 요청
     */
    private void reqTempMoveLocSearch() {
        String loc = mEditFromLoc.getText().toString();
        if (loc == null || loc.length() == 0) {
            showPopup(getString(R.string.not_exist_from_loc));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        String user_id = config.getUserId();
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        InvenMoveInfo info = new InvenMoveInfo();
        info.setUserId(user_id);
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());

        info.setFromLocation(loc);

        INetworkManager network = new NetworkManager(mContext, mLocSearchNetworkListener);
        network.reqInvenTempMoveLocSearch(info);
    }

    /**
     * 임시이동 확정 요청
     */
    private void reqTempMoveConfirm() {
        String from_loc = mEditFromLoc.getText().toString();
        if (from_loc == null || from_loc.length() == 0) {
            showPopup(getString(R.string.not_exist_from_loc));
            return ;
        }

        String from_plt_id = mEditFromPltId.getText().toString();
        if (from_plt_id == null || from_plt_id.length() == 0) {
            showPopup(getString(R.string.not_exist_from_plt_id));
            return ;
        }

        String move_loc = mEditMoveLoc.getText().toString();
        if (move_loc == null || move_loc.length() == 0) {
            showPopup(getString(R.string.not_exist_to_loc));
            return ;
        }

        mInfo.setToLocation(move_loc);

        showProgress(true, getString(R.string.progress_confirm));
        INetworkManager network = new NetworkManager(mContext, mTempMoveConfirmNetworkListener);
        network.reqInvenTempMoveConfirm(mInfo);
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
     * 임시이동 내용 요청 결과 수신 리스너
     */
    private INetworkListener mSearchNetworkListener = new INetworkListener() {
        @Override
        public void onRespResult(int error, String payload) {
            showProgress(false, "");

            if (error != VErrors.E_NONE) {
                String text = "";
                if (error == VErrors.E_DISCONNECTED) {
                    text = mContext.getString(R.string.net_error_not_connect);
                }
                else if (error == VErrors.E_UNKNOWN_HOST) {
                    text = mContext.getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_NOT_CONNECTED) {
                    text = mContext.getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_SEND_TO_SERVER) {
                    text = mContext.getString(R.string.net_error_fail_send);
                }
                else if (error == VErrors.E_RECV_FROM_SERVER) {
                    text = mContext.getString(R.string.net_error_fail_recv);
                }

                nextState(HANDLER_SHOW_NET_ERROR, text);
                return ;
            }

            VLog.d("resp : " + payload);

            // 서버로부터 받은 데이터가 없다.
            if (payload == null || payload.length() <= 0) {
                nextState(HANDLER_SHOW_NET_ERROR, mContext.getString(R.string.net_error_fail_recv));
                return ;
            }

            JSONObject json = VUtil.convertToJSON(payload);
            if (json == null) {
                nextState(HANDLER_SHOW_NET_ERROR, mContext.getString(R.string.net_error_invalid_payload));
                return ;
            }

            nextState(HANDLER_SHOW_SEARCH_RESULT, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

    /**
     * 임시이동 내용 요청 결과 수신 리스너
     */
    private INetworkListener mLocSearchNetworkListener = new INetworkListener() {
        @Override
        public void onRespResult(int error, String payload) {
            showProgress(false, "");

            if (error != VErrors.E_NONE) {
                String text = "";
                if (error == VErrors.E_DISCONNECTED) {
                    text = mContext.getString(R.string.net_error_not_connect);
                }
                else if (error == VErrors.E_UNKNOWN_HOST) {
                    text = mContext.getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_NOT_CONNECTED) {
                    text = mContext.getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_SEND_TO_SERVER) {
                    text = mContext.getString(R.string.net_error_fail_send);
                }
                else if (error == VErrors.E_RECV_FROM_SERVER) {
                    text = mContext.getString(R.string.net_error_fail_recv);
                }

                nextState(HANDLER_SHOW_NET_ERROR, text);
                return ;
            }

            VLog.d("resp : " + payload);

            // 서버로부터 받은 데이터가 없다.
            if (payload == null || payload.length() <= 0) {
                nextState(HANDLER_SHOW_NET_ERROR, mContext.getString(R.string.net_error_fail_recv));
                return ;
            }

            JSONObject json = VUtil.convertToJSON(payload);
            if (json == null) {
                nextState(HANDLER_SHOW_NET_ERROR, mContext.getString(R.string.net_error_invalid_payload));
                return ;
            }

            nextState(HANDLER_SHOW_LOC_SEARCH_RESULT, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

    /**
     * 임시이동 확정 요청 결과 수신 리스너
     */
    private INetworkListener mTempMoveConfirmNetworkListener = new INetworkListener() {
        @Override
        public void onRespResult(int error, String payload) {
            showProgress(false, "");

            if (error != VErrors.E_NONE) {
                String text = "";
                if (error == VErrors.E_DISCONNECTED) {
                    text = mContext.getString(R.string.net_error_not_connect);
                }
                else if (error == VErrors.E_UNKNOWN_HOST) {
                    text = mContext.getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_NOT_CONNECTED) {
                    text = mContext.getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_SEND_TO_SERVER) {
                    text = mContext.getString(R.string.net_error_fail_send);
                }
                else if (error == VErrors.E_RECV_FROM_SERVER) {
                    text = mContext.getString(R.string.net_error_fail_recv);
                }

                nextState(HANDLER_SHOW_NET_ERROR, text);
                return ;
            }

            // 서버로부터 받은 데이터가 없다.
            if (payload == null || payload.length() <= 0) {
                nextState(HANDLER_SHOW_NET_ERROR, mContext.getString(R.string.net_error_fail_recv));
                return ;
            }

            JSONObject json = VUtil.convertToJSON(payload);
            if (json == null) {
                nextState(HANDLER_SHOW_NET_ERROR, mContext.getString(R.string.net_error_invalid_payload));
                return ;
            }

            nextState(HANDLER_SHOW_CONFIRM_RESULT, json);
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
                    text = mContext.getString(R.string.net_error_not_connect);
                }
                else if (error == VErrors.E_UNKNOWN_HOST) {
                    text = mContext.getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_NOT_CONNECTED) {
                    text = mContext.getString(R.string.net_error_not_svr);
                }
                else if (error == VErrors.E_SEND_TO_SERVER) {
                    text = mContext.getString(R.string.net_error_fail_send);
                }
                else if (error == VErrors.E_RECV_FROM_SERVER) {
                    text = mContext.getString(R.string.net_error_fail_recv);
                }

                nextState(HANDLER_SHOW_NET_ERROR, text);
                return ;
            }

            // 서버로부터 받은 데이터가 없다.
            if (payload == null || payload.length() <= 0) {
                nextState(HANDLER_SHOW_NET_ERROR, mContext.getString(R.string.net_error_fail_recv));
                return ;
            }

            JSONObject json = VUtil.convertToJSON(payload);
            if (json == null) {
                nextState(HANDLER_SHOW_NET_ERROR, mContext.getString(R.string.net_error_invalid_payload));
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

    /**
     * 바코드 스캔 내용 수신 리스너
     */
    private IScannerListener mScannerListener = new IScannerListener() {
        @Override
        public void onRecvScan(String scan) {
            if (scan == null || scan.length() == 0) {
                return ;
            }

            nextState(HANDLER_SHOW_SCAN_RESULT, scan);
        }
    };

    private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mEditFromPltId.isFocused()) {
                    mEditFromLoc.requestFocus();
                    imm.hideSoftInputFromWindow(mEditFromPltId.getWindowToken(), 0);
                }
                else if (mEditFromLoc.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditFromLoc.getWindowToken(), 0);
                }
                else if (mEditMoveLoc.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditMoveLoc.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditFromPltId.isFocused()) {
                        mEditFromLoc.requestFocus();
                        imm.hideSoftInputFromWindow(mEditFromPltId.getWindowToken(), 0);
                    }
                    else if (mEditFromLoc.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditFromLoc.getWindowToken(), 0);
                    }
                    else if (mEditMoveLoc.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditMoveLoc.getWindowToken(), 0);
                    }
                }
            }

            return false;
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
            if (id == R.id.inventory_temp_move_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.inventory_temp_move_btn_loc_search) {
                reqTempMoveLocSearch();
            }
            else if (id == R.id.inventory_temp_move_btn_search) {
                reqTempMoveSearch();
            }
            else if (id == R.id.inventory_temp_move_btn_confirm) {
                reqTempMoveConfirm();
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

                case HANDLER_SHOW_SEARCH_RESULT :
                    showSearchResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_LOC_SEARCH_RESULT :
                    showLocSearchResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_CONFIRM_RESULT :
                    showConfirmResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_SCAN_RESULT :
                    showScanResult((String) msg.obj);
                    break;
            }
        }
    };
}
