package com.vertexid.wms.scrn.warehouse;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.barcode.IScannerListener;
import com.vertexid.wms.barcode.ScannerManager;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.info.SerialScanInfo;
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
import com.vertexid.wms.scrn.takeout.TakeOutActivity;
import com.vertexid.wms.scrn.warehouse.adapter.SerialScanItemAdapter;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VLog;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 시리얼스캔 화면
 */
public class SerialScanActivity extends BaseActivity {
    private final int REQ_INQUIRY = 100;

    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_SERIAL_SCAN_LIST = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_CONFIRM_RESULT = HANDLER_SHOW_SERIAL_SCAN_LIST + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CONFIRM_RESULT + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;
    private TextView mTextTotalCount = null;
    private LinearLayout mBtnRemoves = null;
    private Button mBtnSearch = null;

    private EditText mEditWareNumber = null;
    private EditText mEditGoodsCode = null;
    private EditText mEditSerialNumber = null;
    private EditText mEditSerialScanCount = null;

    private ProgressDialog mPrgDlg = null;

    private SerialScanItemAdapter mAdapter = null;

    private ArrayList<SerialScanInfo> mArray = null;

    private int mWareDetailNumber;

    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serial_scan_activity);

        mContext = this;

        mScan = ScannerManager.getInst(mContext);

        mArray = new ArrayList<>();
        mArray.clear();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.serial_scan_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.serial_scan_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.serial_scan_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.serial_scan_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        Button btn_inquiry = (Button) findViewById(R.id.serial_scan_btn_inquiry);
        btn_inquiry.setOnClickListener(mClickListener);
        Button btn_serial_add = (Button) findViewById(R.id.serial_scan_btn_serial_add);
        btn_serial_add.setOnClickListener(mClickListener);

        mBtnSearch = (Button) findViewById(R.id.serial_scan_btn_search);
        mBtnSearch.setOnClickListener(mClickListener);
        mBtnRemoves = (LinearLayout) findViewById(R.id.serial_scan_btn_removes);

        Button btn_select_remove = (Button) findViewById(R.id.serial_scan_btn_remove);
        btn_select_remove.setOnClickListener(mClickListener);
        Button btn_confirm = (Button) findViewById(R.id.serial_scan_btn_confirm);
        btn_confirm.setOnClickListener(mClickListener);

        CheckBox check_select = (CheckBox) findViewById(R.id.serial_scan_check_select);
        check_select.setOnCheckedChangeListener(mCheckChangeListener);

        mEditWareNumber = (EditText) findViewById(R.id.serial_scan_edit_warehouse_number);
        mEditGoodsCode = (EditText) findViewById(R.id.serial_scan_edit_goods);
        mEditSerialNumber = (EditText) findViewById(R.id.serial_scan_edit_serial_number);
        mEditSerialScanCount = (EditText) findViewById(R.id.serial_scan_edit_scan_count);

        mEditWareNumber.setOnEditorActionListener(mEditorActionListener);
        mEditGoodsCode.setOnEditorActionListener(mEditorActionListener);
        mEditSerialNumber.setOnEditorActionListener(mEditorActionListener);

        mEditWareNumber.requestFocus();

        mTextTotalCount = (TextView) findViewById(R.id.serial_scan_text_total_count);
        mTextTotalCount.setText("총 " + String.valueOf(mArray.size()) + "건");

        mAdapter = new SerialScanItemAdapter(mContext, mArray, mCheckBoxCheckedListener);
        ListView list_view = (ListView) findViewById(R.id.serial_scan_list);
        list_view.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_INQUIRY) {
            if (resultCode != Activity.RESULT_OK) {
                return ;
            }

            if (data == null) {
                return ;
            }

            String number = data.getStringExtra(ScrnParam.NUMBER);
            mEditWareNumber.setText(number);
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
     * 스캐너로 스캔한 내용 처리
     * @param scan 스캔 내용
     */
    private void showScanResult(String scan) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (mEditWareNumber.isFocused()) {
            mEditWareNumber.setText(scan);
            mEditGoodsCode.requestFocus();

            imm.hideSoftInputFromWindow(mEditWareNumber.getWindowToken(), 0);
        }
        else if (mEditGoodsCode.isFocused()) {
            mEditGoodsCode.setText(scan);
            mEditSerialNumber.requestFocus();
            imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
            if(mEditWareNumber.getTextSize() > 0) {
                // 입고번호 입력 후, 제품코드 스캔 시, 조회하도록 함
                reqCheckCount();
            }
        }
        else if (mEditSerialNumber.isFocused()) {
            mEditSerialNumber.setText(scan);
            makeSerialArray();

            imm.hideSoftInputFromWindow(mEditSerialNumber.getWindowToken(), 0);
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
     * 시리얼 스캔 목록 표시
     * @param json 서버로부터 수신된 시리얼 스캔 목록
     */
    private void showSerialScanList(JSONObject json) {
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

        Object obj1 = VUtil.getValue(json, NetworkParam.WAREHOUSE_DETAIL_NUMBER);
        mWareDetailNumber = 0;
        if (obj1 != null && !obj1.toString().equals("null")) {
            mWareDetailNumber = Integer.parseInt(obj1.toString());
        }

        Object obj2 = VUtil.getValue(json, NetworkParam.CHECK_COUNT);
        int check_count = 0;
        if (obj2 != null && !obj2.toString().equals("null")) {
            check_count = Integer.parseInt(obj2.toString());
        }

        EditText edit_count = (EditText) findViewById(R.id.serial_scan_edit_check_count);
        edit_count.setText(String.valueOf(check_count));

        mBtnSearch.setBackgroundResource(R.drawable.search_small_btn_selector);
        mBtnRemoves.setVisibility(View.VISIBLE);

        mEditSerialNumber.requestFocus();
    }

    /**
     * 삭제된 아이템을 리스트에서 삭제
     */
    private void showRemoveResult() {
        if (mArray == null || mArray.size() == 0) {
            return ;
        }

        int size = mArray.size() - 1;
        while (true) {
            if (size < 0) {
                break;
            }

            SerialScanInfo info = mArray.get(size);
            boolean is_checked = info.isRemoveChecked();
            if (is_checked) {
                mArray.remove(size);
            }

            size--;
        }

        mTextTotalCount.setText("총 " + mArray.size() + "건");
        mEditSerialScanCount.setText(String.valueOf(mArray.size()));
        CheckBox check_select = (CheckBox) findViewById(R.id.serial_scan_check_select);
        check_select.setChecked(false);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 확정 요청 결과 처리
     * @param json 확정 요청 결과
     */
    private void showConfirmResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
        if (error != NError.SUCCESS) {
            showPopup(text);
            return;
        }

//        for (int i = 0 ; i < mArray.size() ; i++) {
//            SerialScanInfo info = mArray.get(i);
//            info.setConfirmState(SerialScanInfo.CONFIRM_COMPLETE);
//
//            mArray.set(i, info);
//        }
//
//        mAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.confirm_complete));
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int nArraySize = mArray.size();
                for (int j = nArraySize - 1; j >= 0 ; j--) {
                    mArray.remove(j);
                }

                mArray.clear();
                mArray = null;

                CheckBox check_select = (CheckBox) findViewById(R.id.serial_scan_check_select);
                check_select.setChecked(false);

                mAdapter.notifyDataSetChanged();

                mEditWareNumber.setText("");
                mEditGoodsCode.setText("");
                mEditSerialNumber.setText("");
                mEditSerialScanCount.setText("");

                mEditWareNumber.requestFocus();
            }
        });
        builder.show();
    }

    private void makeSerialArray() {
        String ware_number = mEditWareNumber.getText().toString();
        String goods_code = mEditGoodsCode.getText().toString();
        String serial = mEditSerialNumber.getText().toString();
        if (serial == null || serial.length() == 0) {
            return ;
        }

        if (mArray == null) {
            mArray = new ArrayList<>();
            mArray.clear();
        }

        SerialScanInfo info = new SerialScanInfo();
        info.setNumber(ware_number);
        info.setGoods(goods_code);
        info.setDetailNumber(String.valueOf(mWareDetailNumber));
        info.setSerialNumber(serial);

        mArray.add(info);

        mTextTotalCount.setText("총 " + mArray.size() + "건");
        mEditSerialScanCount.setText(String.valueOf(mArray.size()));

        mAdapter.notifyDataSetChanged();
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
    private void moveWareHouseNumberInquiryActivity() {
        Intent i = new Intent();
        i.setClass(mContext, WareHouseNumberSearchActivity.class);
        startActivityForResult(i, REQ_INQUIRY);
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
     * 시리얼 번호 요청
     */
    private void reqCheckCount() {
        EditText edit_number = (EditText) findViewById(R.id.serial_scan_edit_warehouse_number);
        String number = edit_number.getText().toString();
        if (number == null || number.length() <= 0) {
            String text = getString(R.string.not_exist_warehouse_number);
            showPopup(text);
            return ;
        }

        EditText edit_goods = (EditText) findViewById(R.id.serial_scan_edit_goods);
        String goods = edit_goods.getText().toString();
        if (goods == null || goods.length() <= 0) {
            goods = "";
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        SerialScanInfo info = new SerialScanInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setNumber(number);
        info.setGoods(goods);

        showProgress(true, mContext.getString(R.string.progress_serial_scan));

        INetworkManager network = new NetworkManager(mContext, mCheckCountNetworkListener);
        network.reqWareSerialScanList(info);
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
                String ware_number = loop_info.getNumber();
                String detail_number = loop_info.getDetailNumber();
                String serial = loop_info.getSerialNumber();
                String code = loop_info.getGoods();

                JSONObject json = new JSONObject();
                json.put(NetworkParam.WAREHOUSE_NUMBER, ware_number);
                json.put(NetworkParam.WAREHOUSE_DETAIL_NUMBER, detail_number);
                json.put(NetworkParam.SERIAL_ID, serial);
                json.put(NetworkParam.GOODS_CODE, code);

                json_array.put(json);
            }
        }
        catch (JSONException e) {
            return ;
        }

        info.setDtGrid(json_array);

        showProgress(true, mContext.getString(R.string.progress_confirm));

        INetworkManager network = new NetworkManager(mContext, mConfirmNetworkListener);
        network.reqWareSerialScanConfirm(info);
    }

    /**
     * 로그아웃 요청
     */
    private void reqLogOut() {
        IConfigManager config = new ConfigManager(mContext);
        String user_id = config.getUserId();

        showProgress(true, mContext.getString(R.string.progress_logout));

        INetworkManager network = new NetworkManager(mContext, mLogOutNetworkListener);
        network.reqLogOut(user_id);
    }

    /**
     * 시리얼 번호 요청 결과 수신 리스너
     */
    private INetworkListener mCheckCountNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_SERIAL_SCAN_LIST, json);
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

    private CompoundButton.OnCheckedChangeListener mCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            if (id == R.id.serial_scan_check_select) {
                if(mArray != null) {
                    for (int i = 0; i < mArray.size(); i++) {
                        SerialScanInfo info = mArray.get(i);
                        if (info == null) {
                            continue;
                        }

                        info.setRemoveChecked(isChecked);
                        mArray.set(i, info);
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private SerialScanItemAdapter.CheckBoxListener mCheckBoxCheckedListener = new SerialScanItemAdapter.CheckBoxListener() {
        @Override
        public void onCheckBoxChecked(int position, boolean isChecked) {
            SerialScanInfo info = mArray.get(position);
            info.setRemoveChecked(isChecked);
            mArray.set(position, info);

            mAdapter.notifyDataSetChanged();
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

            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                if (mEditWareNumber.isFocused()) {
                    mEditGoodsCode.requestFocus();
//                    imm.hideSoftInputFromWindow(mEditWareNumber.getWindowToken(), 0);
                }
                else if (mEditGoodsCode.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
                    mEditSerialNumber.requestFocus();
                    if(mEditWareNumber.getTextSize() > 0) {
                        // 입고번호 입력 후, 제품코드 스캔 시, 조회하도록 함
                        reqCheckCount();
                    }
                }
                else if (mEditSerialNumber.isFocused()) {
                    String ware_number = mEditWareNumber.getText().toString();
                    String goods_code = mEditGoodsCode.getText().toString();
                    String serial = mEditSerialNumber.getText().toString();
                    if (serial == null || serial.length() == 0) {
                        return false;
                    }

                    if (mArray == null) {
                        mArray = new ArrayList<>();
                        mArray.clear();
                    }

                    SerialScanInfo info = new SerialScanInfo();
                    info.setNumber(ware_number);
                    info.setGoods(goods_code);
                    info.setDetailNumber(String.valueOf(mWareDetailNumber));
                    info.setSerialNumber(serial);

                    mArray.add(info);
                    mTextTotalCount.setText("총 " + mArray.size() + "건");

                    imm.hideSoftInputFromWindow(mEditSerialNumber.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditWareNumber.isFocused()) {
                        mEditGoodsCode.requestFocus();
//                    imm.hideSoftInputFromWindow(mEditWareNumber.getWindowToken(), 0);
                    }
                    else if (mEditGoodsCode.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
                    }
                    else if (mEditSerialNumber.isFocused()) {
                        makeSerialArray();
                        imm.hideSoftInputFromWindow(mEditSerialNumber.getWindowToken(), 0);
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
            if (id == R.id.serial_scan_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.serial_scan_btn_inquiry) {
                moveWareHouseNumberInquiryActivity();
            }
            else if (id == R.id.serial_scan_btn_serial_add) {
                makeSerialArray();
            }
            else if (id == R.id.serial_scan_btn_search) {
                reqCheckCount();
            }
            else if (id == R.id.serial_scan_btn_remove) {
                showRemoveResult();
            }
            else if (id == R.id.serial_scan_btn_confirm) {
                reqConfirm();
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

                case HANDLER_SHOW_SERIAL_SCAN_LIST :
                    showSerialScanList((JSONObject) msg.obj);
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
