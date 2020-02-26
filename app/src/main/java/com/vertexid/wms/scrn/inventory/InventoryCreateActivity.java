package com.vertexid.wms.scrn.inventory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.barcode.IScannerListener;
import com.vertexid.wms.barcode.ScannerManager;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.info.InvenInquiryInfo;
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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 재고조사 신규생성 화면
 */
public class InventoryCreateActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_CREATE_SEARCH_RESULT = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_CREATE_RESULT = HANDLER_SHOW_CREATE_SEARCH_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CREATE_RESULT + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;
    private InvenInquiryInfo mInfo = null;

    private EditText mEditGoodsCode = null;
    private EditText mEditGoodsName = null;
    private EditText mEditAcquire = null;
    private EditText mEditState = null;
    private EditText mEditBoxCount = null;
    private EditText mEditEaCount = null;
    private EditText mEditPltId = null;
    private EditText mEditManuLot = null;
    private EditText mEditLot1 = null;
    private EditText mEditLot2 = null;
    private EditText mEditLot3 = null;
    private EditText mEditLot4 = null;
    private EditText mEditLot5 = null;
    private EditText mEditMakeDate = null;
    private EditText mEditDurationDate = null;
    private EditText mEditLocation = null;

    private Button mBtnSearch = null;
    private Button mBtnCreate = null;

    private ProgressDialog mPrgDlg = null;

    private ArrayList<CodeInfo> mArrayState;

    private ScannerManager mScan = null;
    private String mActualityCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_create_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        Intent intent = getIntent();
        if (intent != null) {
            mActualityCategory = intent.getStringExtra(ScrnParam.INVEN_ACTUALITY_CATEGORY);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.inventory_create_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.inventory_create_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.inventory_create_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        mArrayState = config.getCodeList(CodeInfo.CODE_TYPE_GOODS_STATE);
        if (mArrayState == null) {
            mArrayState = new ArrayList<>();
            mArrayState.clear();
        }

        mEditGoodsCode = (EditText) findViewById(R.id.inventory_create_edit_goods);
        mEditGoodsName = (EditText) findViewById(R.id.inventory_create_edit_goods_name);
        mEditAcquire = (EditText) findViewById(R.id.inventory_create_edit_acquire);
        mEditState = (EditText) findViewById(R.id.inventory_create_edit_state);
        mEditBoxCount = (EditText) findViewById(R.id.inventory_create_edit_box);
        mEditEaCount = (EditText) findViewById(R.id.inventory_create_edit_ea);
        mEditPltId = (EditText) findViewById(R.id.inventory_create_edit_plt_id);
        mEditManuLot = (EditText) findViewById(R.id.inventory_create_edit_manufacture_lot);
        mEditLot1 = (EditText) findViewById(R.id.inventory_create_edit_lot1);
        mEditLot2 = (EditText) findViewById(R.id.inventory_create_edit_lot2);
        mEditLot3 = (EditText) findViewById(R.id.inventory_create_edit_lot3);
        mEditLot4 = (EditText) findViewById(R.id.inventory_create_edit_lot4);
        mEditLot5 = (EditText) findViewById(R.id.inventory_create_edit_lot5);

        mEditMakeDate = (EditText) findViewById(R.id.inventory_create_edit_manufacture_date);
        mEditMakeDate.setOnClickListener(mClickListener);
        mEditMakeDate.setInputType(InputType.TYPE_NULL);
//        mEditMakeDate.requestFocus();

        mEditDurationDate = (EditText) findViewById(R.id.inventory_create_edit_distribution_date);
        mEditDurationDate.setOnClickListener(mClickListener);
        mEditDurationDate.setInputType(InputType.TYPE_NULL);
//        mEditDurationDate.requestFocus();

        mEditLocation = (EditText) findViewById(R.id.inventory_create_edit_loc);

        mEditBoxCount.setText("0");
        mEditEaCount.setText("0");
        mEditState.setText("정상");

        mEditGoodsCode.setOnEditorActionListener(mEditorActionListener);
        mEditBoxCount.setOnEditorActionListener(mEditorActionListener);
        mEditEaCount.setOnEditorActionListener(mEditorActionListener);
        mEditPltId.setOnEditorActionListener(mEditorActionListener);
        mEditManuLot.setOnEditorActionListener(mEditorActionListener);
        mEditLot1.setOnEditorActionListener(mEditorActionListener);
        mEditLot2.setOnEditorActionListener(mEditorActionListener);
        mEditLot3.setOnEditorActionListener(mEditorActionListener);
        mEditLot4.setOnEditorActionListener(mEditorActionListener);
        mEditLot5.setOnEditorActionListener(mEditorActionListener);

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.inventory_create_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        mBtnSearch = (Button) findViewById(R.id.inventory_create_btn_search);
        mBtnSearch.setOnClickListener(mClickListener);
        mBtnCreate = (Button) findViewById(R.id.inventory_create_btn_create);
        mBtnCreate.setOnClickListener(mClickListener);
        View btn_state = findViewById(R.id.inventory_create_btn_state);
        btn_state.setOnClickListener(mClickListener);
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
     * 제품상태를 리스트 형식으로 보여줌
     */
    private void showGoodsState() {
        if (mArrayState == null) {
            mArrayState = new ArrayList<>();
        }

        mArrayState.clear();

        // SQLite로부터 제품상태 목록을 가져온다
        IConfigManager config = new ConfigManager(mContext);
        mArrayState = config.getCodeList(CodeInfo.CODE_TYPE_GOODS_STATE);
        if (mArrayState == null) {
            mArrayState = new ArrayList<>();
            mArrayState.clear();
        }

        String [] goods_state = new String[mArrayState.size()];
        for (int i = 0 ; i < mArrayState.size() ; i++) {
            goods_state[i] = mArrayState.get(i).getText();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("제품상태를 선택해주세요.");
        builder.setItems(goods_state, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = mArrayState.get(which).getText();

                mEditState.setText(name);
            }
        });

        builder.show();
    }

    /**
     * 달력을 나타낸다.
     */
    private void showCalendar() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog date_picker_dlg = new DatePickerDialog(
                mContext,
                mDateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        date_picker_dlg.show();
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

        if (mEditGoodsCode.isFocused()) {
            mEditGoodsCode.setText(scan);
            mEditBoxCount.requestFocus();
            imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
            reqCreateSearch();
        }
        else if (mEditGoodsName.isFocused()) {
            mEditGoodsName.setText(scan);
            mEditPltId.requestFocus();
            imm.hideSoftInputFromWindow(mEditGoodsName.getWindowToken(), 0);
        }
        else if (mEditPltId.isFocused()) {
            mEditPltId.setText(scan);
            mEditManuLot.requestFocus();
            imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
        }
        else if (mEditManuLot.isFocused()) {
            mEditManuLot.setText(scan);
            mEditLot1.requestFocus();
            imm.hideSoftInputFromWindow(mEditManuLot.getWindowToken(), 0);
        }
        else if (mEditLot1.isFocused()) {
            mEditLot1.setText(scan);
            mEditLot2.requestFocus();
            imm.hideSoftInputFromWindow(mEditLot1.getWindowToken(), 0);
        }
        else if (mEditLot2.isFocused()) {
            mEditLot2.setText(scan);
            mEditLot3.requestFocus();
            imm.hideSoftInputFromWindow(mEditLot2.getWindowToken(), 0);
        }
        else if (mEditLot3.isFocused()) {
            mEditLot3.setText(scan);
            mEditLot4.requestFocus();
            imm.hideSoftInputFromWindow(mEditLot3.getWindowToken(), 0);
        }
        else if (mEditLot4.isFocused()) {
            mEditLot4.setText(scan);
            mEditLot5.requestFocus();
            imm.hideSoftInputFromWindow(mEditLot4.getWindowToken(), 0);
        }
        else if (mEditLot5.isFocused()) {
            mEditLot5.setText(scan);
            imm.hideSoftInputFromWindow(mEditLot5.getWindowToken(), 0);
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
     * 생성 결과 처리
     * @param json 생성 결과
     */
    private void showCreateSearchResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        Object obj1 = VUtil.getValue(json, NetworkParam.GOODS_NAME);
        String name = "";
        if (obj1 != null) {
            name = (String) obj1;
        }
        mEditGoodsName.setText(name);

        Object obj2 = VUtil.getValue(json, NetworkParam.ACQUIRE);
        int acquire = 0;
        if (obj2 != null) {
            acquire = (int) obj2;
        }

        mEditAcquire.setText(String.valueOf(acquire));

        mBtnSearch.setVisibility(View.GONE);
        mBtnCreate.setVisibility(View.VISIBLE);
    }

    /**
     * 생성 결과 처리
     * @param json 생성 결과
     */
    private void showCreateResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

//        mBtnSearch.setVisibility(View.VISIBLE);
//        mBtnCreate.setVisibility(View.GONE);

        moveNextActivity();
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
     * 이전 화면에 생성 결과 전달
     */
    private void moveNextActivity() {
        Intent i = new Intent();
        i.setClass(mContext, InventoryInquiryActivity.class);
        i.putExtra(ScrnParam.INFO_DETAIL, mInfo);

        setResult(Activity.RESULT_OK, i);
        finish();
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
     * 재고조사 신규 제품 조회 요청
     */
    private void reqCreateSearch() {
        String goods_code = mEditGoodsCode.getText().toString();
        if (goods_code == null || goods_code.length() == 0) {
            showPopup(getString(R.string.not_exist_goods_code));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        InvenInquiryInfo info = new InvenInquiryInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setGoodsCode(goods_code);

        INetworkManager network = new NetworkManager(mContext, mCreateSearchNetworkListener);
        network.reqInvenCreateSearch(info);
    }

    /**
     * 신규 등록 요청
     */
    private void reqCreate() {
        String goods_code = mEditGoodsCode.getText().toString();
        String goods_name = mEditGoodsName.getText().toString();

        String acquire = mEditAcquire.getText().toString();
        int acquire_count = 0;
        if (acquire.length() > 0) {
            acquire_count = Integer.parseInt(acquire);
        }

        String state = mEditState.getText().toString();
        if (state.length() <= 0) {
            state = "";
        }

        String state_code = "";
        for (int i = 0 ; i < mArrayState.size() ; i++) {
            CodeInfo info = mArrayState.get(i);
            if (info == null) {
                continue;
            }

            String text = info.getText();
            if (text.equals(state)) {
                state_code = info.getCode();
                break;
            }
        }

        String box = mEditBoxCount.getText().toString();
        int box_value = 0;
        if (box.length() <= 0) {
            box_value = 0;
        }
        else {
            box_value = Integer.parseInt(box);
        }

        String ea = mEditEaCount.getText().toString();
        int ea_value = 0;
        if (ea.length() <= 0) {
            ea_value = 0;
        }
        else {
            ea_value = Integer.parseInt(ea);
        }

        String plt_id = mEditPltId.getText().toString();
        if (plt_id.length() <= 0) {
            plt_id = "";
        }

        String manu_lot = mEditManuLot.getText().toString();
        if (manu_lot.length() <= 0) {
            manu_lot = "";
        }

        String manu_date = mEditMakeDate.getText().toString();
        if (manu_date.length() <= 0) {
            manu_date = "";
        }

        String dist_date = mEditDurationDate.getText().toString();
        if (dist_date.length() <= 0) {
            dist_date = "";
        }

        String lot1 = mEditLot1.getText().toString();
        if (lot1.length() <= 0) {
            lot1 = "";
        }

        String lot2 = mEditLot2.getText().toString();
        if (lot2.length() <= 0) {
            lot2 = "";
        }

        String lot3 = mEditLot3.getText().toString();
        if (lot3.length() <= 0) {
            lot3 = "";
        }

        String lot4 = mEditLot4.getText().toString();
        if (lot4.length() <= 0) {
            lot4 = "";
        }

        String lot5 = mEditLot5.getText().toString();
        if (lot5.length() <= 0) {
            lot5 = "";
        }

        String loc = mEditLocation.getText().toString();
        if (loc.length() <= 0) {
            loc = "";
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        mInfo = new InvenInquiryInfo();
        mInfo.setCompanyCode(company.getCode());
        mInfo.setCenterCode(center.getCode());
        mInfo.setCustomerCode(customer.getCode());
        mInfo.setUserId(user_id);

        mInfo.setGoodsCode(goods_code);
        mInfo.setGoodsName(goods_name);
        mInfo.setAcquire(acquire_count);
        mInfo.setGoodsState(state_code);
        mInfo.setBoxCount(box_value);
        mInfo.setEaCount(ea_value);
        mInfo.setPltId(plt_id);
        mInfo.setManufactureLot(manu_lot);
        mInfo.setMakeDate(manu_date);
        mInfo.setDistributionDate(dist_date);
        mInfo.setLotAttribute1(lot1);
        mInfo.setLotAttribute2(lot2);
        mInfo.setLotAttribute3(lot3);
        mInfo.setLotAttribute4(lot4);
        mInfo.setLotAttribute5(lot5);
        mInfo.setActualityCategory(mActualityCategory);
        mInfo.setLocation(loc);

        showCreateResult(null);

        showProgress(true, getString(R.string.progress_create));

        INetworkManager network = new NetworkManager(mContext, mCreateNetworkListener);
        network.reqInvenCreate(mInfo);
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
     * 신규 제품 등록 요청 결과 수신 리스너
     */
    private INetworkListener mCreateSearchNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_CREATE_SEARCH_RESULT, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

    /**
     * 신규 등록 요청 결과 수신 리스너
     */
    private INetworkListener mCreateNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_CREATE_RESULT, json);
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

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar date = Calendar.getInstance();
            date.set(year, monthOfYear, dayOfMonth);

            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            String format = date_format.format(date.getTime());

            if (mEditMakeDate.isFocused()) {
                mEditMakeDate.setText(format);
            }
            else if (mEditDurationDate.isFocused()) {
                mEditDurationDate.setText(format);
            }
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
                if (mEditGoodsCode.isFocused()) {
                    mEditBoxCount.requestFocus();
                    imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
                }
                else if (mEditGoodsName.isFocused()) {
                    mEditAcquire.requestFocus();
                    imm.hideSoftInputFromWindow(mEditGoodsName.getWindowToken(), 0);
                }
                else if (mEditAcquire.isFocused()) {
                    mEditBoxCount.requestFocus();
                    imm.hideSoftInputFromWindow(mEditAcquire.getWindowToken(), 0);
                }
                else if (mEditBoxCount.isFocused()) {
                    mEditEaCount.requestFocus();
                    imm.hideSoftInputFromWindow(mEditBoxCount.getWindowToken(), 0);
                }
                else if (mEditEaCount.isFocused()) {
                    mEditPltId.requestFocus();
                    imm.hideSoftInputFromWindow(mEditEaCount.getWindowToken(), 0);
                }
                else if (mEditPltId.isFocused()) {
                    mEditManuLot.requestFocus();
                    imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
                }
                else if (mEditManuLot.isFocused()) {
                    mEditLot1.requestFocus();
                    imm.hideSoftInputFromWindow(mEditManuLot.getWindowToken(), 0);
                }
                else if (mEditLot1.isFocused()) {
                    mEditLot2.requestFocus();
                    imm.hideSoftInputFromWindow(mEditLot1.getWindowToken(), 0);
                }
                else if (mEditLot2.isFocused()) {
                    mEditLot3.requestFocus();
                    imm.hideSoftInputFromWindow(mEditLot2.getWindowToken(), 0);
                }
                else if (mEditLot3.isFocused()) {
                    mEditLot4.requestFocus();
                    imm.hideSoftInputFromWindow(mEditLot3.getWindowToken(), 0);
                }
                else if (mEditLot4.isFocused()) {
                    mEditLot5.requestFocus();
                    imm.hideSoftInputFromWindow(mEditLot4.getWindowToken(), 0);
                }
                else if (mEditLot5.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditLot5.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditGoodsCode.isFocused()) {
                        mEditBoxCount.requestFocus();
                        imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
                    }
                    else if (mEditGoodsName.isFocused()) {
                        mEditAcquire.requestFocus();
                        imm.hideSoftInputFromWindow(mEditGoodsName.getWindowToken(), 0);
                    }
                    else if (mEditAcquire.isFocused()) {
                        mEditBoxCount.requestFocus();
                        imm.hideSoftInputFromWindow(mEditAcquire.getWindowToken(), 0);
                    }
                    else if (mEditBoxCount.isFocused()) {
                        mEditEaCount.requestFocus();
                        imm.hideSoftInputFromWindow(mEditBoxCount.getWindowToken(), 0);
                    }
                    else if (mEditEaCount.isFocused()) {
                        mEditPltId.requestFocus();
                        imm.hideSoftInputFromWindow(mEditEaCount.getWindowToken(), 0);
                    }
                    else if (mEditPltId.isFocused()) {
                        mEditManuLot.requestFocus();
                        imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
                    }
                    else if (mEditManuLot.isFocused()) {
                        mEditLot1.requestFocus();
                        imm.hideSoftInputFromWindow(mEditManuLot.getWindowToken(), 0);
                    }
                    else if (mEditLot1.isFocused()) {
                        mEditLot2.requestFocus();
                        imm.hideSoftInputFromWindow(mEditLot1.getWindowToken(), 0);
                    }
                    else if (mEditLot2.isFocused()) {
                        mEditLot3.requestFocus();
                        imm.hideSoftInputFromWindow(mEditLot2.getWindowToken(), 0);
                    }
                    else if (mEditLot3.isFocused()) {
                        mEditLot4.requestFocus();
                        imm.hideSoftInputFromWindow(mEditLot3.getWindowToken(), 0);
                    }
                    else if (mEditLot4.isFocused()) {
                        mEditLot5.requestFocus();
                        imm.hideSoftInputFromWindow(mEditLot4.getWindowToken(), 0);
                    }
                    else if (mEditLot5.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditLot5.getWindowToken(), 0);
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
            if (id == R.id.inventory_create_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.inventory_create_edit_manufacture_date) {
                showCalendar();
            }
            if (id == R.id.inventory_create_edit_distribution_date) {
                showCalendar();
            }
            else if (id == R.id.inventory_create_btn_search) {
                reqCreateSearch();
            }
            else if (id == R.id.inventory_create_btn_create) {
                reqCreate();
            }
            else if (id == R.id.inventory_create_btn_state) {
                showGoodsState();
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

                case HANDLER_SHOW_CREATE_SEARCH_RESULT :
                    showCreateSearchResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_CREATE_RESULT :
                    showCreateResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_SCAN_RESULT :
                    showScanResult((String) msg.obj);
                    break;
            }
        }
    };
}
