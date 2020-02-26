package com.vertexid.wms.scrn.warehouse;

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
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VLog;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 입하검수 상세 화면
 */
public class ArrivalDetailActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_CHECK_RESULT = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CHECK_RESULT + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;

    private EditText mEditGoodsState = null;
    private EditText mEditCheckBox = null;
    private EditText mEditCheckEa = null;
    private EditText mEditPltId = null;
    private EditText mEditMakeLot = null;

    private EditText mEditMakeDate = null;
    private EditText mEditDurationDate = null;

    private EditText mEditLotAttr1 = null;
    private EditText mEditLotAttr2 = null;
    private EditText mEditLotAttr3 = null;
    private EditText mEditLotAttr4 = null;
    private EditText mEditLotAttr5 = null;

    private ProgressDialog mPrgDlg = null;

    private WareHouseInfo mInfo = null;
    private int mPosition = 0;

    private ArrayList<CodeInfo> mArrayState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arrival_detail_activity);

        mContext = this;

        ScannerManager scan = ScannerManager.getInst(mContext);
        scan.setListener(mScannerListener);

        mArrayState = new ArrayList<>();
        mArrayState.clear();

        Intent intent = getIntent();
        if (intent != null) {
            mInfo = (WareHouseInfo) intent.getSerializableExtra(ScrnParam.INFO_DETAIL);
            mPosition = intent.getIntExtra(ScrnParam.INFO_POSITION, 0);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.arrival_detail_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.arrival_detail_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.arrival_detail_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        TextView number_n_supplier = (TextView) findViewById(R.id.arrival_detail_text_number_n_supplier);
        number_n_supplier.setText(mInfo.getWareHouseNumber() + " - " + mInfo.getSupplierName());

        EditText edit_goods_code = (EditText) findViewById(R.id.arrival_detail_edit_goods_code);
        EditText edit_goods_name = (EditText) findViewById(R.id.arrival_detail_edit_goods_name);
        EditText edit_acquire = (EditText) findViewById(R.id.arrival_detail_edit_acquire);

        mEditGoodsState = (EditText) findViewById(R.id.arrival_detail_edit_goods_state);
        mEditCheckBox = (EditText) findViewById(R.id.arrival_detail_edit_box);
        mEditCheckEa = (EditText) findViewById(R.id.arrival_detail_edit_ea);
        mEditPltId = (EditText) findViewById(R.id.arrival_detail_edit_plt_id);
        mEditMakeLot = (EditText) findViewById(R.id.arrival_detail_edit_manufacture_lot);

        mEditMakeDate = (EditText) findViewById(R.id.arrival_detail_edit_manufacture_date);
        mEditMakeDate.setOnClickListener(mClickListener);
        mEditMakeDate.setInputType(InputType.TYPE_NULL);

        mEditDurationDate = (EditText) findViewById(R.id.arrival_detail_edit_distribution_date);
        mEditDurationDate.setOnClickListener(mClickListener);
        mEditDurationDate.setInputType(InputType.TYPE_NULL);

        mEditLotAttr1 = (EditText) findViewById(R.id.arrival_detail_edit_lot1);
        mEditLotAttr2 = (EditText) findViewById(R.id.arrival_detail_edit_lot2);
        mEditLotAttr3 = (EditText) findViewById(R.id.arrival_detail_edit_lot3);
        mEditLotAttr4 = (EditText) findViewById(R.id.arrival_detail_edit_lot4);
        mEditLotAttr5 = (EditText) findViewById(R.id.arrival_detail_edit_lot5);

        mEditCheckBox.setOnEditorActionListener(mEditorActionListener);
        mEditCheckEa.setOnEditorActionListener(mEditorActionListener);
        mEditPltId.setOnEditorActionListener(mEditorActionListener);
        mEditMakeLot.setOnEditorActionListener(mEditorActionListener);
        mEditLotAttr1.setOnEditorActionListener(mEditorActionListener);
        mEditLotAttr2.setOnEditorActionListener(mEditorActionListener);
        mEditLotAttr3.setOnEditorActionListener(mEditorActionListener);
        mEditLotAttr4.setOnEditorActionListener(mEditorActionListener);
        mEditLotAttr5.setOnEditorActionListener(mEditorActionListener);

        mEditCheckBox.requestFocus();

        edit_goods_code.setText(mInfo.getGoods());
        edit_goods_name.setText(mInfo.getGoodsName());
        edit_acquire.setText(String.valueOf(mInfo.getAcquireCount()));
        mEditCheckBox.setText(String.valueOf(mInfo.getCheckBoxCount()));
        mEditCheckEa.setText(String.valueOf(mInfo.getCheckEaCount()));

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.arrival_detail_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        View btn_state = findViewById(R.id.arrival_detail_btn_goods_state);
        btn_state.setOnClickListener(mClickListener);
        Button btn_check = (Button) findViewById(R.id.arrival_detail_btn_check);
        btn_check.setOnClickListener(mClickListener);

        if (mArrayState == null) {
            mArrayState = new ArrayList<>();
        }

        mArrayState.clear();

        // SQLite로부터 제품상태 목록을 가져온다
        mArrayState = config.getCodeList(CodeInfo.CODE_TYPE_GOODS_STATE);
        if (mArrayState != null && mArrayState.size() > 0) {
            CodeInfo code_info = mArrayState.get(0);
            if (code_info != null) {
                mEditGoodsState.setText(code_info.getText());
            }
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
     * 바코드 스캐너로 스캔한 내용 처리
     * @param scan 스캔 내용
     */
    private void showScanResult(String scan) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (mEditPltId.isFocused()) {
            mEditPltId.setText(scan);
            mEditMakeLot.requestFocus();

            imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
        }
        else if (mEditMakeLot.isFocused()) {
            mEditMakeLot.setText(scan);
            mEditLotAttr1.requestFocus();
            imm.hideSoftInputFromWindow(mEditMakeLot.getWindowToken(), 0);
        }
        else if (mEditLotAttr1.isFocused()) {
            mEditLotAttr1.setText(scan);
            mEditLotAttr2.requestFocus();

            imm.hideSoftInputFromWindow(mEditLotAttr1.getWindowToken(), 0);
        }
        else if (mEditLotAttr2.isFocused()) {
            mEditLotAttr2.setText(scan);
            mEditLotAttr3.requestFocus();

            imm.hideSoftInputFromWindow(mEditLotAttr2.getWindowToken(), 0);
        }
        else if (mEditLotAttr3.isFocused()) {
            mEditLotAttr3.setText(scan);
            mEditLotAttr4.requestFocus();

            imm.hideSoftInputFromWindow(mEditLotAttr3.getWindowToken(), 0);
        }
        else if (mEditLotAttr4.isFocused()) {
            mEditLotAttr4.setText(scan);
            mEditLotAttr5.requestFocus();

            imm.hideSoftInputFromWindow(mEditLotAttr4.getWindowToken(), 0);
        }
        else if (mEditLotAttr5.isFocused()) {
            mEditLotAttr5.setText(scan);
            imm.hideSoftInputFromWindow(mEditLotAttr5.getWindowToken(), 0);
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

        int result = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (result != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        Intent closeActivity = new Intent(ACTION_ACTIVITY_CLOSE);
        closeActivity.setPackage(getPackageName());
        sendBroadcast(closeActivity);
    }

    /**
     * 검수 요청 결과를 처리
     * @param json 검수 요청 결과
     */
    private void showCheckResult(JSONObject json) {
        if (json == null) {
            String text = getString(R.string.net_error_invalid_payload);
            showPopup(text);
            return ;
        }

        int result = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (result != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.check_complete));
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mInfo.setArrivalCheckState(WareHouseInfo.CHECK_COMPLETE);
                moveNextActivity();
            }
        });
        builder.show();
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
                mEditGoodsState.setText(name);
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
     * 다음 화면으로 이동
     * @param cls 이동할 activity 클래스
     */
    private void moveNextActivity(Class<?> cls) {
        Intent i = new Intent();
        i.setClass(mContext, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    /**
     * 다음 화면으로 이동
     */
    private void moveNextActivity() {
        Intent i = new Intent();
        i.setClass(mContext, ArrivalCheckActivity.class);
        i.putExtra(ScrnParam.INFO_DETAIL, mInfo);
        i.putExtra(ScrnParam.INFO_POSITION, mPosition);

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
     * 입하 제품 검수 요청
     */
    private void reqArrivalCheck() {
        String goods_state = mEditGoodsState.getText().toString();
        String state_code = "";
        for (int i = 0 ; i < mArrayState.size() ; i++) {
            CodeInfo info = mArrayState.get(i);
            if (info.getText().equals(goods_state)) {
                state_code = info.getCode();
                break;
            }
        }

        String box = mEditCheckBox.getText().toString();
        int box_count = 0;
        if (box != null && box.length() > 0) {
            box_count = Integer.parseInt(box);
        }

        String ea = mEditCheckEa.getText().toString();
        int ea_count = 0;
        if (ea != null && ea.length() > 0) {
            ea_count = Integer.parseInt(ea);
        }

        if (box_count == 0 && ea_count == 0) {
            return ;
        }

        String plt_id = mEditPltId.getText().toString();
        String manufacture_lot = mEditMakeLot.getText().toString();
        String make_date = mEditMakeDate.getText().toString();
        String end_date = mEditDurationDate.getText().toString();

        String lot1 = mEditLotAttr1.getText().toString();
        String lot2 = mEditLotAttr2.getText().toString();
        String lot3 = mEditLotAttr3.getText().toString();
        String lot4 = mEditLotAttr4.getText().toString();
        String lot5 = mEditLotAttr5.getText().toString();

        IConfigManager config = new ConfigManager(mContext);
        String user_id = config.getUserId();
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        mInfo.setCompanyCode(company.getCode());
        mInfo.setCenterCode(center.getCode());
        mInfo.setCustomerCode(customer.getCode());
        mInfo.setUserId(user_id);

        mInfo.setGoodsState(state_code);
        mInfo.setCheckBoxCount(box_count);
        mInfo.setCheckEaCount(ea_count);
        mInfo.setPltId(plt_id);
        mInfo.setManufactureLot(manufacture_lot);
        mInfo.setMakeDate(make_date.replaceAll("-", ""));
        mInfo.setDistributionDate(end_date.replaceAll("-", ""));
        mInfo.setLotAttribute1(lot1);
        mInfo.setLotAttribute2(lot2);
        mInfo.setLotAttribute3(lot3);
        mInfo.setLotAttribute4(lot4);
        mInfo.setLotAttribute5(lot5);

        showProgress(true, getString(R.string.progress_check));

        INetworkManager network = new NetworkManager(mContext, mArrivalCheckNetworkListener);
        network.reqArrivalCheck(mInfo);
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
     * 입하 제품 검수 요청 결과 수신 리스너
     */
    private INetworkListener mArrivalCheckNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_CHECK_RESULT, json);
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

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar date = Calendar.getInstance();
            date.set(year, monthOfYear, dayOfMonth);

            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            String format = date_format.format(date.getTime());

            if (mEditMakeDate.isFocused()) {
                mEditMakeDate.setText(format);
//                mEditDurationDate.requestFocus();
            }
            else if (mEditDurationDate.isFocused()) {
                mEditDurationDate.setText(format);
            }
        }
    };

    private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mEditCheckBox.isFocused()) {
                    mEditCheckBox.requestFocus();
                    imm.hideSoftInputFromWindow(mEditCheckBox.getWindowToken(), 0);
                }
                else if (mEditCheckEa.isFocused()) {
                    mEditCheckEa.requestFocus();
                    imm.hideSoftInputFromWindow(mEditCheckEa.getWindowToken(), 0);
                }
                else if (mEditPltId.isFocused()) {
                    mEditMakeLot.requestFocus();
                    imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
                }
                else if (mEditLotAttr1.isFocused()) {
                    mEditLotAttr2.requestFocus();
                    imm.hideSoftInputFromWindow(mEditLotAttr1.getWindowToken(), 0);
                }
                else if (mEditLotAttr2.isFocused()) {
                    mEditLotAttr3.requestFocus();
                    imm.hideSoftInputFromWindow(mEditLotAttr2.getWindowToken(), 0);
                }
                else if (mEditLotAttr3.isFocused()) {
                    mEditLotAttr4.requestFocus();
                    imm.hideSoftInputFromWindow(mEditLotAttr3.getWindowToken(), 0);
                }
                else if (mEditLotAttr4.isFocused()) {
                    mEditLotAttr5.requestFocus();
                    imm.hideSoftInputFromWindow(mEditLotAttr4.getWindowToken(), 0);
                }
                else if (mEditLotAttr5.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditLotAttr5.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditCheckBox.isFocused()) {
                        mEditCheckBox.requestFocus();
                        imm.hideSoftInputFromWindow(mEditCheckBox.getWindowToken(), 0);
                    }
                    else if (mEditCheckEa.isFocused()) {
                        mEditCheckEa.requestFocus();
                        imm.hideSoftInputFromWindow(mEditCheckEa.getWindowToken(), 0);
                    }
                    else if (mEditPltId.isFocused()) {
                        mEditMakeLot.requestFocus();
                        imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
                    }
                    else if (mEditLotAttr1.isFocused()) {
                        mEditLotAttr2.requestFocus();
                        imm.hideSoftInputFromWindow(mEditLotAttr1.getWindowToken(), 0);
                    }
                    else if (mEditLotAttr2.isFocused()) {
                        mEditLotAttr3.requestFocus();
                        imm.hideSoftInputFromWindow(mEditLotAttr2.getWindowToken(), 0);
                    }
                    else if (mEditLotAttr3.isFocused()) {
                        mEditLotAttr4.requestFocus();
                        imm.hideSoftInputFromWindow(mEditLotAttr3.getWindowToken(), 0);
                    }
                    else if (mEditLotAttr4.isFocused()) {
                        mEditLotAttr5.requestFocus();
                        imm.hideSoftInputFromWindow(mEditLotAttr4.getWindowToken(), 0);
                    }
                    else if (mEditLotAttr5.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditLotAttr5.getWindowToken(), 0);
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
            if (id == R.id.arrival_detail_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.arrival_detail_btn_goods_state) {
                showGoodsState();
            }
            else if (id == R.id.arrival_detail_edit_manufacture_date) {
                showCalendar();
            }
            else if (id == R.id.arrival_detail_edit_distribution_date) {
                showCalendar();
            }
            else if (id == R.id.arrival_detail_btn_check) {
                reqArrivalCheck();
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

                case HANDLER_SHOW_CHECK_RESULT :
                    showCheckResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_SCAN_RESULT :
                    showScanResult((String) msg.obj);
                    break;
            }
        }
    };
}
