package com.vertexid.wms.scrn.takeout;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.barcode.IScannerListener;
import com.vertexid.wms.barcode.ScannerManager;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.info.SerialScanInfo;
import com.vertexid.wms.info.TakeOutInfo;
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
import com.vertexid.wms.scrn.takeout.adapter.TakeOutSerialScanItemAdapter;
import com.vertexid.wms.scrn.warehouse.WareHouseActivity;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VLog;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 출고 시리얼 스캔 화면
 */
public class TakeOutSerialScanActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_LIST = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_CONFIRM_RESULT = HANDLER_SHOW_LIST + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CONFIRM_RESULT + 1;

    private final int REQ_SEARCH = 100;

    private DrawerLayout mDrawerLayout = null;
    private Context mContext = null;

    private TextView mTextTotalCount = null;

    private EditText mEditTakeOutNumber = null;
    private EditText mEditGoodsCode = null;
    private EditText mEditSerialNumber = null;
    private EditText mEditCheckCount = null;
    private EditText mEditSerialScanCount = null;

    private ProgressDialog mPrgDlg = null;

    private TakeOutSerialScanItemAdapter mAdapter = null;
    private ArrayList<SerialScanInfo> mArray = null;

    private CheckBox mCheckBoxAll = null;

    private int mDetailNumber;

    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_out_serial_scan_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        mArray = new ArrayList<>();
        mArray.clear();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.take_out_serial_scan_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.take_out_serial_scan_navi_view);
        navi_view.setNavigationItemSelectedListener(mNavigationListener);
        View nav_header_view = navi_view.getHeaderView(0);

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        String user_id = config.getUserId();

        TextView text_menu_user = (TextView) nav_header_view.findViewById(R.id.menu_text_user);
        TextView text_menu_center = (TextView) nav_header_view.findViewById(R.id.menu_text_center);
        TextView text_menu_customer = (TextView) nav_header_view.findViewById(R.id.menu_text_customer);
        text_menu_user.setText(user_id);
        text_menu_center.setText(center.getText());
        text_menu_customer.setText(customer.getText());

        TextView text_user = (TextView) findViewById(R.id.take_out_serial_scan_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        Button mBtnSearch = (Button) findViewById(R.id.take_out_serial_scan_btn_search);
        mBtnSearch.setOnClickListener(mClickListener);

        Button btn_menu = (Button) findViewById(R.id.take_out_serial_scan_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        Button btn_delete = (Button) findViewById(R.id.take_out_serial_scan_btn_delete);
        btn_delete.setOnClickListener(mClickListener);
        Button btn_inquiry = (Button) findViewById(R.id.take_out_serial_scan_btn_inquiry);
        btn_inquiry.setOnClickListener(mClickListener);
        Button btn_confirm = (Button) findViewById(R.id.take_out_serial_scan_btn_confirm);
        btn_confirm.setOnClickListener(mClickListener);

        mEditTakeOutNumber = (EditText) findViewById(R.id.take_out_serial_scan_edit_take_out_number);
        mEditGoodsCode = (EditText) findViewById(R.id.take_out_serial_scan_edit_goods_code);
        mEditTakeOutNumber.setOnEditorActionListener(mEditorActionListener);
        mEditGoodsCode.setOnEditorActionListener(mEditorActionListener);

        mEditTakeOutNumber.requestFocus();

        mEditSerialNumber = (EditText) findViewById(R.id.take_out_serial_scan_edit_take_out_serial_number);
        mEditCheckCount = (EditText) findViewById(R.id.take_out_serial_scan_edit_check_count);
        mEditSerialScanCount = (EditText) findViewById(R.id.take_out_serial_scan_edit_scan_count);

        mTextTotalCount = (TextView) findViewById(R.id.take_out_list_text_total_count);
        mTextTotalCount.setText("총 " + String.valueOf(mArray.size()) + "건");

        Button btn_serial_confirm = (Button) findViewById(R.id.take_out_serial_scan_btn_serial_confirm);
        btn_serial_confirm.setOnClickListener(mClickListener);

        mCheckBoxAll = (CheckBox) findViewById(R.id.take_out_serial_scan_check_box_all);
        mCheckBoxAll.setOnCheckedChangeListener(mCheckChangeListener);

        mAdapter = new TakeOutSerialScanItemAdapter(mContext, mArray, mCheckBoxCheckedListener);
        ListView list_view = (ListView) findViewById(R.id.take_out_serial_scan_listview);
        list_view.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SEARCH) {
            if (resultCode != Activity.RESULT_OK) {
                return ;
            }

            if (data == null) {
                return ;
            }

            String number = data.getStringExtra(ScrnParam.NUMBER);
            mEditTakeOutNumber.setText(number);
            mEditGoodsCode.requestFocus();
        }
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

    /**
     * 바코드 스캔 내용을 처리
     * @param text 바코드 스캔 내용
     */
    private void showScanResult(String text) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (mEditTakeOutNumber.isFocused()) {
            mEditTakeOutNumber.setText(text);
            mEditGoodsCode.requestFocus();

            imm.hideSoftInputFromWindow(mEditTakeOutNumber.getWindowToken(), 0);
        }
        else if (mEditGoodsCode.isFocused()) {
            mEditGoodsCode.setText(text);
            imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
        }
        else if (mEditSerialNumber.isFocused()) {
            mEditSerialNumber.setText(text);
            imm.hideSoftInputFromWindow(mEditSerialNumber.getWindowToken(), 0);

            makeSerialArray();
        }
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
     * 시리얼 스캔 목록 요청 결과를 처리
     * @param json 시리얼 스캔 목록 요청 결과
     */
    private void showSerialScanListResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        Object obj1 = VUtil.getValue(json, NetworkParam.TAKE_OUT_CHECK_COUNT);
        int check_count = 0;
        if (obj1 != null && !obj1.toString().equals("null")) {
            check_count = Integer.parseInt(obj1.toString());
        }

        Object obj2 = VUtil.getValue(json, NetworkParam.TAKE_OUT_DETAIL_NUMBER);
        if (obj2 != null && !obj2.toString().equals("null")) {
            mDetailNumber = Integer.parseInt(obj2.toString());
        }

        mEditCheckCount.setText(String.valueOf(check_count));
        mEditSerialNumber.requestFocus();
    }

    /**
     * 확정 요청 결과를 처리
     * @param json 확정 요청 결과
     */
    private void showConfirmResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        showPopup(getString(R.string.success_confirm));
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
    private void moveTakeOutNumberInquiryActivity() {
        Intent i = new Intent();
        i.setClass(mContext, TakeOutNumSearchActivity.class);
        startActivityForResult(i, REQ_SEARCH);
    }

    private void makeSerialArray() {
        String number = mEditTakeOutNumber.getText().toString();
        String goods_code = mEditGoodsCode.getText().toString();
        String serial = mEditSerialNumber.getText().toString();

        if (serial == null || serial.length() == 0) {
            showPopup(getString(R.string.not_exist_serial_num));
            return ;
        }

        if (mArray == null) {
            mArray = new ArrayList<>();
            mArray.clear();
        }

        SerialScanInfo info = new SerialScanInfo();
        info.setNumber(number);
        info.setGoods(goods_code);
        info.setDetailNumber(String.valueOf(mDetailNumber));
        info.setSerialNumber(serial);

        mArray.add(info);

        mTextTotalCount.setText("총 " + mArray.size() + "건");
        mEditSerialScanCount.setText(String.valueOf(mArray.size()));

        mAdapter.notifyDataSetChanged();
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
     * 시리얼 스캔 목록 요청
     */
    private void reqSerialScanList() {
        String number = mEditTakeOutNumber.getText().toString();
        if (number == null || number.length() == 0) {
            showPopup(getString(R.string.not_exist_take_out_number));
            return;
        }

        String code = mEditGoodsCode.getText().toString();
        if (code == null || code.length() == 0) {
            showPopup(getString(R.string.not_exist_goods_code));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        TakeOutInfo info = new TakeOutInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setUserId(user_id);

        info.setTakeOutNum(number);
        info.setGoods(code);

        showProgress(true, getString(R.string.progress_search));

        INetworkManager network = new NetworkManager(mContext, mSerialSearchNetworkListener);
        network.reqTakeOutSerialScanlList(info);
    }

    /**
     * 삭제 요청
     */
    private void reqRemove() {
       ArrayList<SerialScanInfo> list = new ArrayList<>();
        for(SerialScanInfo item : mArray){
            if(item.isRemoveChecked()) list.add(item);
        }

        if(list.size() > 0){
            for(SerialScanInfo item : list){
                mArray.remove(item);
            }
        }

        mTextTotalCount.setText("총 " + mArray.size() + "건");
        mAdapter.notifyDataSetChanged();

        if(mArray.size() == 0){
            mCheckBoxAll.setChecked(false);
        }
    }

    /**
     * 확정 요청
     */
    private void reqConfirm() {
        if (mArray == null || mArray.size() == 0) {
            showPopup(getString(R.string.not_exist_confirm_list));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        String user_id = config.getUserId();
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        SerialScanInfo info = new SerialScanInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setUserId(user_id);

        JSONArray json_array = new JSONArray();
        try {
            for (int i = 0 ; i < mArray.size() ; i++) {
                SerialScanInfo loop_info = mArray.get(i);
                String number = loop_info.getNumber();
                String goods_code = loop_info.getGoods();
                String serial = loop_info.getSerialNumber();

                JSONObject json = new JSONObject();
                json.put(NetworkParam.TAKE_OUT_NUMBER, number);
                json.put(NetworkParam.TAKE_OUT_DETAIL_NUMBER, mDetailNumber);
                json.put(NetworkParam.SERIAL_ID, serial);
                json.put(NetworkParam.GOODS_CODE, goods_code);
                json.put(NetworkParam.USER_ID, user_id);

                json_array.put(json);
            }
        }
        catch (JSONException e) {
            return ;
        }

        info.setDtGrid(json_array);

        showProgress(true, getString(R.string.progress_confirm));

        INetworkManager network = new NetworkManager(mContext, mConfirmNetworkListener);
        network.reqTakeOutSerialScanConfirm(info);
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
     * 시리얼 스캔 목록 요청 결과 수신 리스너
     */
    private INetworkListener mSerialSearchNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_LIST, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

    /**
     * 확정 요청 결과 수신 리스너
     */
    private INetworkListener mConfirmNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_CONFIRM_RESULT, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

//    /**
//     * 삭제 요청 결과 수신 리스너
//     */
//    private INetworkListener mRemoveNetworkListener = new INetworkListener() {
//        @Override
//        public void onRespResult(int error, String payload) {
//            showProgress(false, "");
//
//            if (error != VErrors.E_NONE) {
//                String text = "";
//                if (error == VErrors.E_DISCONNECTED) {
//                    text = mContext.getString(R.string.net_error_not_connect);
//                }
//                else if (error == VErrors.E_UNKNOWN_HOST) {
//                    text = mContext.getString(R.string.net_error_not_svr);
//                }
//                else if (error == VErrors.E_NOT_CONNECTED) {
//                    text = mContext.getString(R.string.net_error_not_svr);
//                }
//                else if (error == VErrors.E_SEND_TO_SERVER) {
//                    text = mContext.getString(R.string.net_error_fail_send);
//                }
//                else if (error == VErrors.E_RECV_FROM_SERVER) {
//                    text = mContext.getString(R.string.net_error_fail_recv);
//                }
//
//                nextState(HANDLER_SHOW_NET_ERROR, text);
//                return ;
//            }
//
//            // 서버로부터 받은 데이터가 없다.
//            if (payload == null || payload.length() <= 0) {
//                nextState(HANDLER_SHOW_NET_ERROR, mContext.getString(R.string.net_error_fail_recv));
//                return ;
//            }
//
//            JSONObject json = VUtil.convertToJSON(payload);
//            if (json == null) {
//                nextState(HANDLER_SHOW_NET_ERROR, mContext.getString(R.string.net_error_invalid_payload));
//                return ;
//            }
//
//            nextState(HANDLER_SHOW_REMOVE_RESULT, json);
//        }
//
//        @Override
//        public void onProgress(int progress) {
//        }
//
//        @Override
//        public void onResult(int error) {
//        }
//    };

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
                if (mEditTakeOutNumber.isFocused()) {
                    mEditGoodsCode.requestFocus();
                    imm.hideSoftInputFromWindow(mEditTakeOutNumber.getWindowToken(), 0);
                }
                else if (mEditGoodsCode.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditTakeOutNumber.isFocused()) {
                        mEditGoodsCode.requestFocus();
                        imm.hideSoftInputFromWindow(mEditTakeOutNumber.getWindowToken(), 0);
                    }
                    else if (mEditGoodsCode.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
                    }
                }
            }

            return false;
        }
    };

    /**
     * 리스트에서 체크박스 선택 시 선택된 항목 수신 리스너
     */
    private TakeOutSerialScanItemAdapter.CheckBoxListener mCheckBoxCheckedListener = new TakeOutSerialScanItemAdapter.CheckBoxListener() {
        @Override
        public void onCheckBoxChecked(int position, boolean isChecked) {
            SerialScanInfo info = mArray.get(position);
            info.setRemoveChecked(isChecked);
            mArray.set(position, info);
            mAdapter.notifyDataSetChanged();
        }
    };

    private CompoundButton.OnCheckedChangeListener mCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            for (int i = 0 ; i < mArray.size() ; i++) {
                SerialScanInfo info = mArray.get(i);
                if (info == null) {
                    continue;
                }

                info.setRemoveChecked(isChecked);
                mArray.set(i, info);
            }

            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 타이틀 좌측 상단 메뉴 클릭 리스너
     */
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
            if (id == R.id.take_out_serial_scan_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if(id == R.id.take_out_serial_scan_btn_inquiry){
                moveTakeOutNumberInquiryActivity();
            }
            else if (id == R.id.take_out_serial_scan_btn_search) {
                reqSerialScanList();
            }
            else if(id == R.id.take_out_serial_scan_btn_delete) {
                reqRemove();
            }
            else if (id == R.id.take_out_serial_scan_btn_confirm) {
                reqConfirm();
            }
            else if(id == R.id.take_out_serial_scan_btn_serial_confirm){
                String serialNumber = mEditSerialNumber.getText().toString();
                if(serialNumber == null || serialNumber.equals("")){
                    VUtil.showPopup(mContext, getString(R.string.not_exist_serial_num));
                    return;
                }

                makeSerialArray();
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

                case HANDLER_SHOW_LIST :
                    showSerialScanListResult((JSONObject) msg.obj);
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
