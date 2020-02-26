package com.vertexid.wms.scrn.inventory;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.barcode.IScannerListener;
import com.vertexid.wms.barcode.ScannerManager;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.info.InvenPltInfo;
import com.vertexid.wms.network.INetworkListener;
import com.vertexid.wms.network.INetworkManager;
import com.vertexid.wms.network.NError;
import com.vertexid.wms.network.NetworkManager;
import com.vertexid.wms.network.NetworkParam;
import com.vertexid.wms.scrn.BaseActivity;
import com.vertexid.wms.scrn.carryin.CarryInActivity;
import com.vertexid.wms.scrn.carryout.CarryOutActivity;
import com.vertexid.wms.scrn.inventory.adapter.InventoryPltDivideItemAdapter;
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
 * PLT 분할 화면
 */
public class InventoryPltDivideActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_PLT_LIST = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_DIVIDE_RESULT = HANDLER_SHOW_PLT_LIST + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_DIVIDE_RESULT + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;

    private ProgressDialog mPrgDlg = null;

    private EditText mEditFromPltId = null;
    private EditText mEditGoodsCode = null;
    private EditText mEditGoodsName = null;
    private EditText mEditLotId = null;
    private EditText mEditDivideBox = null;
    private EditText mEditDivideEa = null;
    private EditText mEditDividePltId = null;

    private ArrayList<InvenPltInfo> mArray = null;
    private InventoryPltDivideItemAdapter mAdapter = null;

    private ScannerManager mScan = null;
    private InvenPltInfo mInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_plt_divide_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        mArray = new ArrayList<>();
        mArray.clear();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.inventory_plt_divide_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.inventory_plt_divide_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.inventory_plt_divide_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        mEditFromPltId = (EditText) findViewById(R.id.inventory_plt_divide_edit_from_plt_id);
        mEditGoodsCode = (EditText) findViewById(R.id.inventory_plt_divide_edit_goods_code);
        mEditGoodsName = (EditText) findViewById(R.id.inventory_plt_divide_edit_goods_name);
        mEditLotId = (EditText) findViewById(R.id.inventory_plt_divide_edit_lot_id);
        mEditDivideBox = (EditText) findViewById(R.id.inventory_plt_divide_edit_divide_box);
        mEditDivideEa = (EditText) findViewById(R.id.inventory_plt_divide_edit_divide_ea);
        mEditDividePltId = (EditText) findViewById(R.id.inventory_plt_divide_edit_divide_plt_id);

        mEditFromPltId.setOnEditorActionListener(mEditorActionListener);
        mEditDivideBox.setOnEditorActionListener(mEditorActionListener);
        mEditDivideEa.setOnEditorActionListener(mEditorActionListener);
        mEditDividePltId.setOnEditorActionListener(mEditorActionListener);

        mEditFromPltId.requestFocus();

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.inventory_plt_divide_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);

        View btn_search = findViewById(R.id.inventory_plt_divide_btn_search);
        btn_search.setOnClickListener(mClickListener);

        View btn_divide = findViewById(R.id.inventory_plt_divide_btn_divide);
        btn_divide.setOnClickListener(mClickListener);

        mAdapter = new InventoryPltDivideItemAdapter(mContext, mArray);
        ListView list_view = (ListView) findViewById(R.id.inventory_plt_divide_list);
        list_view.setOnItemClickListener(mItemClickListener);
        list_view.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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
     * 리스트에서 선택한 항목의 정보를 보여준다.
     * @param position 리스트에서 선택한 항목의 인덱스
     */
    private void showInfo(int position) {
        mInfo = mArray.get(position);

        mEditGoodsCode.setText(mInfo.getGoodsCode());
        mEditGoodsName.setText(mInfo.getGoodsName());
        mEditLotId.setText(mInfo.getLotId());

        mEditDivideBox.setText(String.valueOf(mInfo.getInvenBoxCount()));
        mEditDivideEa.setText(String.valueOf(mInfo.getInvenEaCount()));
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

            reqPltSearch();
        }
        else if (mEditDividePltId.isFocused()) {
            mEditDividePltId.setText(scan);
            imm.hideSoftInputFromWindow(mEditDividePltId.getWindowToken(), 0);
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
     * PLT 목록요청 결과 처리
     * @param json PLT 목록요청 결과
     */
    private void showPltList(JSONObject json) {
        if (json == null) {
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
            String text = getString(R.string.not_exist_list);
            showPopup(text);
            return ;
        }

        if (mArray == null) {
            mArray = new ArrayList<>();
        }

        mArray.clear();

        String from_plt_id = mEditFromPltId.getText().toString();

        try {
            for (int i = 0 ; i < json_array.length() ; i++) {
                JSONObject json_obj = json_array.getJSONObject(i);
                if (json_obj == null || json_obj.length() == 0) {
                    continue;
                }

                Object obj1 = VUtil.getValue(json_obj, NetworkParam.GOODS_CODE);
                String code = "";
                if (obj1 != null && !obj1.toString().equals("null")) {
                    code = obj1.toString();
                }

                Object obj2 = VUtil.getValue(json_obj, NetworkParam.GOODS_NAME);
                String name = "";
                if (obj2 != null && !obj2.toString().equals("null")) {
                    name = obj2.toString();
                }

                Object obj3 = VUtil.getValue(json_obj, NetworkParam.LOT_ID);
                String lot_id = "";
                if (obj3 != null && !obj3.toString().equals("null")) {
                    lot_id = obj3.toString();
                }

                Object obj4 = VUtil.getValue(json_obj, NetworkParam.INVEN_MOVE_BOX_COUNT);
                int box_count = 0;
                if (obj4 != null && !obj4.toString().equals("null")) {
                    box_count = Integer.parseInt(obj4.toString());
                }

                Object obj5 = VUtil.getValue(json_obj, NetworkParam.INVEN_MOVE_EA_COUNT);
                int ea_count = 0;
                if (obj5 != null && !obj5.toString().equals("null")) {
                    ea_count = Integer.parseInt(obj5.toString());
                }

                Object obj6 = VUtil.getValue(json_obj, NetworkParam.GOODS_STATE);
                String state = "";
                if (obj6 != null && !obj6.toString().equals("null")) {
                    state = obj6.toString();
                }

                InvenPltInfo info = new InvenPltInfo();
                info.setFromPltId(from_plt_id);
                info.setGoodsCode(code);
                info.setGoodsName(name);
                info.setLotId(lot_id);
                info.setInvenBoxCount(box_count);
                info.setInvenEaCount(ea_count);
                info.setGoodsState(state);

                mArray.add(info);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter.notifyDataSetChanged();
        mEditDivideBox.requestFocus();
        showInfo(0);
    }

    /**
     * 분할 요청 결과 처리
     * @param json 분할 요청 결과
     */
    private void showDivideResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        final int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        String text = getString(R.string.divide_complete);
        if (error != NError.SUCCESS) {
            text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(text);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (error == NError.SUCCESS) {
                    mEditDividePltId.setText("");
                    reqPltSearch();
//
                }
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
     * PLT 조회
     */
    private void reqPltSearch() {
        String from_plt_id = mEditFromPltId.getText().toString();
        if (from_plt_id == null || from_plt_id.length() <= 0) {
            showPopup(getString(R.string.not_exist_from_plt_id));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        InvenPltInfo info = new InvenPltInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setUserId(user_id);

        info.setFromPltId(from_plt_id);

        showProgress(true, getString(R.string.progress_search));
        INetworkManager network = new NetworkManager(mContext, mSearchNetworkListener);
        network.reqInvenPalletDivideList(info);
    }

    /**
     * 분할 요청
     */
    private void reqDivide() {
        if (mInfo == null) {
            return ;
        }

        String plt_id = mEditDividePltId.getText().toString();
        if (plt_id == null || plt_id.length() == 0) {
            showPopup(getString(R.string.not_exist_divide_plt_id));
            return ;
        }

        String box = mEditDivideBox.getText().toString();
        int box_count = 0;
        if (box != null && box.length() > 0) {
            box_count = Integer.parseInt(box);
        }

        String ea = mEditDivideEa.getText().toString();
        int ea_count = 0;
        if (ea != null || ea.length() > 0) {
            ea_count = Integer.parseInt(ea);
        }

        if (box_count == 0 && ea_count == 0) {
            showPopup(getString(R.string.not_exist_divide_count));
            return ;
        }

        int inven_box = mInfo.getInvenBoxCount();
        if (inven_box < box_count) {
            showPopup(getString(R.string.more_than_inven_count));
            return ;
        }

        int inven_ea = mInfo.getInvenEaCount();
        if (inven_ea < ea_count) {
            showPopup(getString(R.string.more_than_inven_count));
            return ;
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

        mInfo.setToBoxCount(box_count);
        mInfo.setToEaCount(ea_count);
        mInfo.setToPltId(plt_id);

        showProgress(true, getString(R.string.progress_plt_divide));

        INetworkManager network = new NetworkManager(mContext, mDivideNetworkListener);
        network.reqInvenPalletDivide(mInfo);
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
     * 조회 요청 결과 수신 리스너
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

            nextState(HANDLER_SHOW_PLT_LIST, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

    /**
     * 분할 요청 결과 수신 리스너
     */
    private INetworkListener mDivideNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_DIVIDE_RESULT, json);
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
                    imm.hideSoftInputFromWindow(mEditFromPltId.getWindowToken(), 0);
                }
                else if (mEditDivideBox.isFocused()) {
                    mEditDivideEa.requestFocus();
                    imm.hideSoftInputFromWindow(mEditDivideBox.getWindowToken(), 0);
                }
                else if (mEditDivideEa.isFocused()) {
                    mEditDividePltId.requestFocus();
                    imm.hideSoftInputFromWindow(mEditDivideEa.getWindowToken(), 0);
                }
                else if (mEditDividePltId.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditDividePltId.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditFromPltId.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditFromPltId.getWindowToken(), 0);
                    }
                    else if (mEditDivideBox.isFocused()) {
                        mEditDivideEa.requestFocus();
                        imm.hideSoftInputFromWindow(mEditDivideBox.getWindowToken(), 0);
                    }
                    else if (mEditDivideEa.isFocused()) {
                        mEditDividePltId.requestFocus();
                        imm.hideSoftInputFromWindow(mEditDivideEa.getWindowToken(), 0);
                    }
                    else if (mEditDividePltId.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditDividePltId.getWindowToken(), 0);
                    }
                }
            }

            return false;
        }
    };

    /**
     * 리스트 항목 선택 리스너
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
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
            if (id == R.id.inventory_plt_divide_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.inventory_plt_divide_btn_search) {
                reqPltSearch();
            }
            else if (id == R.id.inventory_plt_divide_btn_divide) {
                reqDivide();
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

                case HANDLER_SHOW_PLT_LIST :
                    showPltList((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_DIVIDE_RESULT :
                    showDivideResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_SCAN_RESULT :
                    showScanResult((String) msg.obj);
                    break;
            }
        }
    };
}
