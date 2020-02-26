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
import com.vertexid.wms.info.WareHouseInfo;
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
import com.vertexid.wms.scrn.warehouse.adapter.ArrivalItemAdapter;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VLog;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 입하검수 화면
 */
public class ArrivalCheckActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_ARRIVAL_LIST = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_CONFIRM_RESULT = HANDLER_SHOW_ARRIVAL_LIST + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CONFIRM_RESULT + 1;

    private final int REQ_DETAIL = 100;
    private final int REQ_SEARCH = REQ_DETAIL + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;
    private TextView mTextTotalCount = null;
    private Button mBtnSearch = null;
    private LinearLayout mLayoutButtons = null;

    private EditText mEditWareNumber = null;
    private EditText mEditGoodsCode = null;
    private EditText mEditSupplier = null;

    private CheckBox mCheckAllSelect = null;

    private ProgressDialog mPrgDlg = null;

    /** 입하검수 목록 */
    private ArrayList<WareHouseInfo> mArray = null;
    /** 확정을 위해 체크된 항목들 */
    private ArrayList<Integer> mArrayIndex = null;
    private ArrivalItemAdapter mAdapter = null;

    private ScannerManager mScan = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arrival_check_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        mArray = new ArrayList<>();
        mArray.clear();

        mArrayIndex = new ArrayList<>();
        mArrayIndex.clear();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.arrival_check_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.arrival_check_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.arrival_check_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.arrival_check_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        Button btn_inquiry = (Button) findViewById(R.id.arrival_check_btn_inquiry);
        btn_inquiry.setOnClickListener(mClickListener);

        mBtnSearch = (Button) findViewById(R.id.arrival_check_btn_search);
        mBtnSearch.setOnClickListener(mClickListener);
        Button btn_all_select = (Button) findViewById(R.id.arrival_check_btn_all_select);
        btn_all_select.setOnClickListener(mClickListener);
        Button btn_confirm = (Button) findViewById(R.id.arrival_check_btn_confirm);
        btn_confirm.setOnClickListener(mClickListener);

        mLayoutButtons = (LinearLayout) findViewById(R.id.arrival_check_layout_btns);

        mEditWareNumber = (EditText) findViewById(R.id.arrival_check_edit_warehouse_number);
        mEditGoodsCode = (EditText) findViewById(R.id.arrival_check_edit_goods_code);
        mEditSupplier = (EditText) findViewById(R.id.arrival_check_edit_supplier);

        mEditWareNumber.setOnEditorActionListener(mEditorActionListener);
        mEditGoodsCode.setOnEditorActionListener(mEditorActionListener);

        mEditWareNumber.requestFocus();

        mCheckAllSelect = (CheckBox) findViewById(R.id.arrival_item_check_select);
        mCheckAllSelect.setOnCheckedChangeListener(mCheckChangeListener);

        mTextTotalCount = (TextView) findViewById(R.id.arrival_check_text_total_count);
        mTextTotalCount.setText("총 " + mArray.size() + "건");

        mAdapter = new ArrivalItemAdapter(mContext, mArray, mCheckBoxListener, mItemClickListener);
        ListView mListView = (ListView) findViewById(R.id.arrival_check_list);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_DETAIL) {
            if (resultCode != Activity.RESULT_OK) {
                return ;
            }

            if (data == null) {
                return ;
            }

            WareHouseInfo info = (WareHouseInfo) data.getSerializableExtra(ScrnParam.INFO_DETAIL);
            if (info == null) {
                return ;
            }

            int position = data.getIntExtra(ScrnParam.INFO_POSITION, 0);

            mArray.set(position, info);
            mAdapter.notifyDataSetChanged();

            reqArrivalList();
        }
        else if (requestCode == REQ_SEARCH) {
            if (resultCode != Activity.RESULT_OK) {
                return ;
            }

            if (data == null) {
                return ;
            }

            String warehouse_number = data.getStringExtra(ScrnParam.NUMBER);
            String supplier = data.getStringExtra(ScrnParam.SUPPLIER);

            mEditWareNumber.setText(warehouse_number);
            mEditSupplier.setText(supplier);
            mEditGoodsCode.requestFocus();

            reqArrivalList();
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

//        new AlertDialog.Builder(mContext).
//                setMessage(text).
//                setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                }).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
            imm.hideSoftInputFromWindow(mEditWareNumber.getWindowToken(), 0);

            reqArrivalList();
        }
        else if (mEditGoodsCode.isFocused()) {
            mEditGoodsCode.setText(scan);
            imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);

            if (mArray == null) {
                return ;
            }

            int position = 0;
            boolean is_exist = false;
            for (int i = 0 ; i < mArray.size() ; i++) {
                WareHouseInfo info = mArray.get(i);
                if (info == null) {
                    continue;
                }

                String goods_code = info.getGoods();
                if (goods_code.equals(scan)) {
                    // 승인수량이 검수수량과 크거나 같으면 다음 제품 검색
                    int appr_count = info.getApprovalCount();
                    int check_count = info.getCheckCount();
                    if (appr_count >= check_count) {
                        continue;
                    }

                    position = i;
                    is_exist = true;
                    break;
                }
            }

            if (is_exist) {
                moveDetailActivity(position);
            }
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
            showPopup((String) VUtil.getValue(json, NetworkParam.RESULT_MSG));
            return ;
        }

        Intent closeActivity = new Intent(ACTION_ACTIVITY_CLOSE);
        closeActivity.setPackage(getPackageName());
        sendBroadcast(closeActivity);
    }

    /**
     * 수신된 내용을 리스트뷰로 표현
     */
    private void showArrivalList(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            showPopup((String) VUtil.getValue(json, NetworkParam.RESULT_MSG));
            return ;
        }

        JSONArray json_array = (JSONArray) VUtil.getValue(json, NetworkParam.ARRAY_LIST);
        if (json_array == null || json_array.length() <= 0) {
            showPopup(getString(R.string.not_exist_list));
            return ;
        }

        if (mArray == null) {
            mArray = new ArrayList<>();
        }

        mArray.clear();

        try {
            for (int i = 0 ; i < json_array.length() ; i++) {
                JSONObject json_obj = json_array.getJSONObject(i);
                if (json_obj == null) {
                    continue ;
                }

                Object obj1 = VUtil.getValue(json_obj, NetworkParam.WAREHOUSE_NUMBER);
                String number = "";
                if (obj1 != null && !obj1.toString().equals("null")) {
                    number = obj1.toString();
                }

                Object obj2 = VUtil.getValue(json_obj, NetworkParam.WAREHOUSE_DETAIL_NUMBER);
                int detail_number = 0;
                if (obj2 != null && !obj2.toString().equals("null")) {
                    detail_number = Integer.parseInt(obj2.toString());
                }

                Object obj3 = VUtil.getValue(json_obj, NetworkParam.GOODS_CODE);
                String goods_code = "";
                if (obj3 != null && !obj3.toString().equals("null")) {
                    goods_code = obj3.toString();
                }

                Object obj4 = VUtil.getValue(json_obj, NetworkParam.GOODS_NAME);
                String goods_name = "";
                if (obj4 != null && !obj4.toString().equals("null")) {
                    goods_name = obj4.toString();
                }

                Object obj5 = VUtil.getValue(json_obj, NetworkParam.SUPPLIER_NAME);
                String supplier = "";
                if (obj5 != null && !obj5.toString().equals("null")) {
                    supplier = obj5.toString();
                }

                Object obj6 = VUtil.getValue(json_obj, NetworkParam.APPROVAL_COUNT);
                int approval_count = 0;
                if (obj6 != null && !obj6.toString().equals("null")) {
                    approval_count = Integer.parseInt(obj6.toString());
                }

                Object obj7 = VUtil.getValue(json_obj, NetworkParam.CHECK_COUNT);
                int check_count = 0;
                if (obj7 != null && !obj7.toString().equals("null")) {
                    check_count = Integer.parseInt(obj7.toString());
                }

                Object obj8 = VUtil.getValue(json_obj, NetworkParam.ACQUIRE);
                int acquire = 0;
                if (obj8 != null && !obj8.toString().equals("null")) {
                    acquire = Integer.parseInt(obj8.toString());
                }

                Object obj9 = VUtil.getValue(json_obj, NetworkParam.CHECK_COUNT_BOX);
                int check_box_count = 0;
                if (obj9 != null && !obj9.toString().equals("null")) {
                    check_box_count = Integer.parseInt(obj9.toString());
                }

                Object obj10 = VUtil.getValue(json_obj, NetworkParam.CHECK_COUNT_EA);
                int check_ea_count = 0;
                if (obj10 != null && !obj10.toString().equals("null")) {
                    check_ea_count = Integer.parseInt(obj10.toString());
                }

                WareHouseInfo info = new WareHouseInfo();
                info.setWareHouseNumber(number);
                info.setWareHouseDetailNumber(detail_number);
                info.setGoods(goods_code);
                info.setGoodsName(goods_name);
                info.setSupplierName(supplier);
                info.setApprovalCount(approval_count);
                info.setCheckCount(check_count);
                info.setCheckBoxCount(check_box_count);
                info.setCheckEaCount(check_ea_count);
                info.setAcquireCount(acquire);

                mArray.add(info);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        mTextTotalCount.setText("총 " + mArray.size() + "건");
        mAdapter.notifyDataSetChanged();

        mBtnSearch.setBackgroundResource(R.drawable.search_small_btn_selector);
        mLayoutButtons.setVisibility(View.VISIBLE);

        mCheckAllSelect.setVisibility(View.VISIBLE);

        mEditGoodsCode.requestFocus();

        if (mArray != null && mArray.size() == 1) {
            moveDetailActivity(0);
        }
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
        if (error != NError.SUCCESS) {
            showPopup((String) VUtil.getValue(json, NetworkParam.RESULT_MSG));
            return ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.confirm_complete));
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0 ; j < mArrayIndex.size() ; j++) {
                    Integer index = mArrayIndex.get(j);
                    WareHouseInfo info = mArray.get(index);
                    info.setArrivalCheckState(WareHouseInfo.CONFIRM_COMPLETE);

                    mArray.set(index, info);
                }

                mArrayIndex.clear();

                int nArraySize = mArray.size();
                for (int j = nArraySize - 1; j >= 0 ; j--) {
                    WareHouseInfo info = mArray.get(j);
                    if (info == null) {
                        continue;
                    }

                    int state = info.getArrivalCheckState();
                    if (state == WareHouseInfo.CONFIRM_COMPLETE) {
                        mArray.remove(j);
                    }
                }

                mCheckAllSelect.setChecked(false);
                mTextTotalCount.setText("총 " + mArray.size() + "건");
                mAdapter.notifyDataSetChanged();
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
     * 상세 화면으로 이동
     * @param position 입하검수 내용 인덱스
     */
    private void moveDetailActivity(int position) {
        Intent i = new Intent();
        i.setClass(mContext, ArrivalDetailActivity.class);
        i.putExtra(ScrnParam.INFO_DETAIL, mArray.get(position));
        i.putExtra(ScrnParam.INFO_POSITION, position);
        startActivityForResult(i, REQ_DETAIL);
    }

    /**
     * 조회 화면으로 이동
     */
    private void moveWareHouseNumberInquiryActivity() {
        Intent i = new Intent();
        i.setClass(mContext, WareHouseNumberSearchActivity.class);
        startActivityForResult(i, REQ_SEARCH);
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
     * 입하검수 목록 요청
     */
    private void reqArrivalList() {
        String number = mEditWareNumber.getText().toString();
        if (number == null || number.length() == 0) {
            showPopup(getString(R.string.not_exist_warehouse_number));
            return ;
        }

        String code = mEditGoodsCode.getText().toString();
        if (code == null || code.length() == 0) {
            code = "";
        }

        IConfigManager config = new ConfigManager(mContext);
        String user_id = config.getUserId();
        CodeInfo company_info = config.getCompanyInfo();
        CodeInfo center_info = config.getCenterInfo();
        CodeInfo customer_info = config.getCustomerInfo();

        WareHouseInfo info = new WareHouseInfo();
        info.setUserId(user_id);
        info.setCompanyCode(company_info.getCode());
        info.setCenterCode(center_info.getCode());
        info.setCustomerCode(customer_info.getCode());

        info.setWareHouseNumber(number);
        info.setGoods(code);

        showProgress(true, getString(R.string.progress_req_list));
        INetworkManager network = new NetworkManager(mContext, mSearchNetworkListener);
        network.reqArrivalList(info);
    }

    /**
     * 검수 목록 확정 요청
     */
    private void reqConfirm() {
        ArrayList<WareHouseInfo> array_confirm = new ArrayList<>();
        array_confirm.clear();

        if (mArrayIndex == null) {
            mArrayIndex = new ArrayList<>();
        }

        mArrayIndex.clear();

        for (int i = 0 ; i < mArray.size() ; i++) {
            WareHouseInfo info = mArray.get(i);
            boolean is_checked = info.isSelected();
            if (is_checked) {
                array_confirm.add(info);
                mArrayIndex.add(i);
            }
        }

        if (mArrayIndex.size() == 0) {
            showPopup(getString(R.string.not_exist_confirm_list));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        JSONArray json_array = new JSONArray();

        try {
            for (int i = 0 ; i < array_confirm.size() ; i++) {
                WareHouseInfo info = array_confirm.get(i);
                String number = info.getWareHouseNumber();
                int detail_number = info.getWareHouseDetailNumber();
                String code = info.getGoods();

                JSONObject json_obj = new JSONObject();
                json_obj.put(NetworkParam.WAREHOUSE_NUMBER, number);
                json_obj.put(NetworkParam.WAREHOUSE_DETAIL_NUMBER, detail_number);
                json_obj.put(NetworkParam.GOODS_CODE, code);
                json_obj.put(NetworkParam.USER_ID, user_id);

                json_array.put(json_obj);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (json_array == null || json_array.length() <= 0) {
            return ;
        }

        WareHouseInfo info = new WareHouseInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setUserId(user_id);

        showProgress(true, getString(R.string.progress_confirm));
        INetworkManager network = new NetworkManager(mContext, mConfirmNetworkListener);
        network.reqArrivalConfirm(info, json_array);
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

    /**
     * 입하 검수 목록 요청 결과 수신 리스너
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

            nextState(HANDLER_SHOW_ARRIVAL_LIST, json);
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

    private ArrivalItemAdapter.IContentsClickListener mItemClickListener = new ArrivalItemAdapter.IContentsClickListener() {
        public void onClick(int position) {
            moveDetailActivity(position);
        }
    };

    private ArrivalItemAdapter.ICheckBoxListener mCheckBoxListener = new ArrivalItemAdapter.ICheckBoxListener() {
        @Override
        public void onCheckBoxChecked(int position, boolean isChecked) {
            WareHouseInfo info = mArray.get(position);
            info.setIsSelected(isChecked);
            mArray.set(position, info);
            mAdapter.notifyDataSetChanged();
        }
    };

    private CompoundButton.OnCheckedChangeListener mCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            for (int i = 0 ; i < mArray.size() ; i++) {
                WareHouseInfo info = mArray.get(i);
                if (info == null) {
                    continue;
                }

                info.setIsSelected(isChecked);
                mArray.set(i, info);
            }

            mAdapter.notifyDataSetChanged();
        }
    };

//    /**
//     * 리스트 항목 선택 리스너
//     */
//    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            moveDetailActivity(position);
//        }
//    };

//    /**
//     * 리스트 항목 롱클릭 리스너
//     */
//    private AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {
//        @Override
//        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//            return false;
//        }
//    };

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
                if (mEditWareNumber.isFocused()) {
                    mEditGoodsCode.requestFocus();
//                    imm.hideSoftInputFromWindow(mEditWareNumber.getWindowToken(), 0);
                }
                else if (mEditGoodsCode.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditWareNumber.isFocused()) {
                        mEditGoodsCode.requestFocus();
//                        imm.hideSoftInputFromWindow(mEditWareNumber.getWindowToken(), 0);
                    }
                    else if (mEditGoodsCode.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
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
            if (id == R.id.arrival_check_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.arrival_check_btn_inquiry) {
                moveWareHouseNumberInquiryActivity();
            }
            else if (id == R.id.arrival_check_btn_search) {
                reqArrivalList();
            }
            else if (id == R.id.arrival_check_btn_all_select) {
                boolean bSelect = false;
                if(mCheckAllSelect.isChecked()) {
                    bSelect = false;
                    mCheckAllSelect.setChecked(false);
                }else {
                    bSelect = true;
                    mCheckAllSelect.setChecked(true);
                }

                for (int i = 0 ; i < mArray.size() ; i++) {
                    WareHouseInfo info = mArray.get(i);
                    if (info == null) {
                        continue;
                    }

                    info.setIsSelected(bSelect);
                    mArray.set(i, info);
                }

                mAdapter.notifyDataSetChanged();
            }
            else if (id == R.id.arrival_check_btn_confirm) {
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

                case HANDLER_SHOW_ARRIVAL_LIST :
                    showArrivalList((JSONObject) msg.obj);
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
