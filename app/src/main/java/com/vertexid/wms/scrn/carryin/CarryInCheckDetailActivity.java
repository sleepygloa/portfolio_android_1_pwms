package com.vertexid.wms.scrn.carryin;

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
import com.vertexid.wms.info.CarryInInfo;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.network.INetworkListener;
import com.vertexid.wms.network.INetworkManager;
import com.vertexid.wms.network.NError;
import com.vertexid.wms.network.NetworkManager;
import com.vertexid.wms.network.NetworkParam;
import com.vertexid.wms.scrn.BaseActivity;
import com.vertexid.wms.scrn.ScrnParam;
import com.vertexid.wms.scrn.carryout.CarryOutActivity;
import com.vertexid.wms.scrn.inventory.InventoryActivity;
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
 * 반입검수 상세 화면
 */
public class CarryInCheckDetailActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_CARRY_IN_CHECK_RESULT = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CARRY_IN_CHECK_RESULT + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;

    private EditText mEditState = null;
    private EditText mEditBoxCount = null;
    private EditText mEditEaCount = null;
    private EditText mEditPltId = null;
    private EditText mEditMakeLot = null;

    private EditText mEditMakeDate = null;
    private EditText mEditDurationDate = null;

    private EditText mEditLot1 = null;
    private EditText mEditLot2 = null;
    private EditText mEditLot3 = null;
    private EditText mEditLot4 = null;
    private EditText mEditLot5 = null;

    private ProgressDialog mPrgDlg = null;

    private CarryInInfo mInfo = null;
    private int mPosition;

    private ArrayList<CodeInfo> mArrayState;

    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carry_in_check_detail_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        Intent intent = getIntent();
        if (intent != null) {
            mInfo = (CarryInInfo) intent.getSerializableExtra(ScrnParam.INFO_DETAIL);
            mPosition = intent.getIntExtra(ScrnParam.INFO_POSITION, 0);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.carry_in_check_detail_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.carry_in_check_detail_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.carry_in_check_detail_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        mEditState = (EditText) findViewById(R.id.carry_in_check_detail_edit_goods_state);
        mEditBoxCount = (EditText) findViewById(R.id.carry_in_check_detail_edit_box);
        mEditEaCount = (EditText) findViewById(R.id.carry_in_check_detail_edit_ea);
        mEditPltId = (EditText) findViewById(R.id.carry_in_check_detail_edit_plt_id);
        mEditMakeLot = (EditText) findViewById(R.id.carry_in_check_detail_edit_manufacture_lot);

        mEditLot1 = (EditText) findViewById(R.id.carry_in_check_detail_edit_lot1);
        mEditLot2 = (EditText) findViewById(R.id.carry_in_check_detail_edit_lot2);
        mEditLot3 = (EditText) findViewById(R.id.carry_in_check_detail_edit_lot3);
        mEditLot4 = (EditText) findViewById(R.id.carry_in_check_detail_edit_lot4);
        mEditLot5 = (EditText) findViewById(R.id.carry_in_check_detail_edit_lot5);

        mEditMakeDate = (EditText) findViewById(R.id.carry_in_check_detail_edit_manufacture_date);
        mEditMakeDate.setOnClickListener(mClickListener);
        mEditMakeDate.setInputType(InputType.TYPE_NULL);

        mEditDurationDate = (EditText) findViewById(R.id.carry_in_check_detail_edit_distribution_date);
        mEditDurationDate.setOnClickListener(mClickListener);
        mEditDurationDate.setInputType(InputType.TYPE_NULL);

        mEditBoxCount.setOnEditorActionListener(mEditorActionListener);
        mEditEaCount.setOnEditorActionListener(mEditorActionListener);
        mEditPltId.setOnEditorActionListener(mEditorActionListener);
        mEditMakeLot.setOnEditorActionListener(mEditorActionListener);
        mEditLot1.setOnEditorActionListener(mEditorActionListener);
        mEditLot2.setOnEditorActionListener(mEditorActionListener);
        mEditLot3.setOnEditorActionListener(mEditorActionListener);
        mEditLot4.setOnEditorActionListener(mEditorActionListener);
        mEditLot5.setOnEditorActionListener(mEditorActionListener);
        mEditMakeDate.setOnEditorActionListener(mEditorActionListener);
        mEditDurationDate.setOnEditorActionListener(mEditorActionListener);

        mEditBoxCount.requestFocus();

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.carry_in_check_detail_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        View btn_state = findViewById(R.id.carray_in_check_detail_btn_goods_state);
        btn_state.setOnClickListener(mClickListener);
        View btn_check = findViewById(R.id.carry_in_check_detail_btn_check);
        btn_check.setOnClickListener(mClickListener);

        if (mInfo != null) {
            TextView text_number = (TextView) findViewById(R.id.carry_in_check_detail_text_number);
            TextView text_delivery = (TextView) findViewById(R.id.carry_in_check_detail_text_delivery);
            EditText edit_goods_code = (EditText) findViewById(R.id.carry_in_check_detail_edit_goods_code);
            EditText edit_goods_name = (EditText) findViewById(R.id.carry_in_check_detail_edit_goods_name);
            EditText edit_acquire = (EditText) findViewById(R.id.carry_in_check_detail_edit_acquire);

            String number = mInfo.getCarryInNumber();
            String delivery = mInfo.getDeliveryName();
            String code = mInfo.getGoodsCode();
            String name = mInfo.getGoodsName();
            int acquire = mInfo.getAcquire();

            text_number.setText(number);
            text_delivery.setText(delivery);
            edit_goods_code.setText(code);
            edit_goods_name.setText(name);
            edit_acquire.setText(String.valueOf(acquire));

            mEditBoxCount.setText(String.valueOf(mInfo.getCheckBoxCount()));
            mEditEaCount.setText(String.valueOf(mInfo.getCheckEaCount()));
        }

        mArrayState = config.getCodeList(CodeInfo.CODE_TYPE_GOODS_STATE);
        if (mArrayState == null) {
            mArrayState = new ArrayList<>();
            mArrayState.clear();
        }

        mEditState.setText("정상");
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

        if (mEditPltId.isFocused()) {
            mEditPltId.setText(scan);
            mEditMakeLot.requestFocus();

            imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
        }
        else if (mEditMakeLot.isFocused()) {
            mEditMakeLot.setText(scan);
            mEditLot1.requestFocus();

            imm.hideSoftInputFromWindow(mEditMakeLot.getWindowToken(), 0);
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
     * 반입 검수 결과 처리
     * @param json 반입검수
     */
    private void showCarryInCheckResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.check_complete));
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mInfo.setProcessState(CarryInInfo.STATE_CONFIRM);
                moveNextActivity();
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
     * 다음 화면으로 이동
     */
    private void moveNextActivity() {
        Intent i = new Intent();
        i.setClass(mContext, CarryInCheckActivity.class);
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
     * 반입검수 요청
     */
    private void reqCarryInCheck() {
        String goods_state = mEditState.getText().toString();
        String state_code = "";
        if (goods_state != null && goods_state.length() > 0) {
            for (int i = 0 ; i < mArrayState.size() ; i++) {
                CodeInfo info = mArrayState.get(i);
                String text = info.getText();
                if (text.equals(goods_state)) {
                    state_code = info.getCode();
                    break;
                }
            }
        }

        String box_count = mEditBoxCount.getText().toString();
        int box = 0;
        if (box_count != null && box_count.length() > 0) {
            box = Integer.parseInt(box_count);
        }

        String ea_count = mEditEaCount.getText().toString();
        int ea = 0;
        if (ea_count != null && ea_count.length() > 0) {
            ea = Integer.parseInt(ea_count);
        }

        if (box == 0 && ea == 0) {
            showPopup(getString(R.string.not_exist_check_count));
            return ;
        }

        String plt_id = mEditPltId.getText().toString();
        if (plt_id == null || plt_id.length() <= 0) {
            plt_id = "";
        }

        String manufacture_lot = mEditMakeLot.getText().toString();
        if (manufacture_lot == null || manufacture_lot.length() <= 0) {
            manufacture_lot = "";
        }

        String make_date = mEditMakeDate.getText().toString();
        if (make_date == null || make_date.length() <= 0) {
            make_date = "";
        }

        String end_date = mEditDurationDate.getText().toString();
        if (end_date == null || end_date.length() <= 0) {
            end_date = "";
        }

        String lot1 = mEditLot1.getText().toString();
        if (lot1 == null) {
            lot1 = "";
        }

        String lot2 = mEditLot2.getText().toString();
        if (lot2 == null || lot2.length() <= 0) {
            lot2 = "";
        }

        String lot3 = mEditLot3.getText().toString();
        if (lot3 == null || lot3.length() <= 0) {
            lot3 = "";
        }

        String lot4 = mEditLot4.getText().toString();
        if (lot4 == null || lot4.length() <= 0) {
            lot4 = "";
        }

        String lot5 = mEditLot5.getText().toString();
        if (lot5 == null || lot5.length() <= 0) {
            lot5 = "";
        }

        mInfo.setCheckBoxCount(box);
        mInfo.setCheckEaCount(ea);
        mInfo.setGoodsState(state_code);

        mInfo.setPltId(plt_id);
        mInfo.setManufactureLot(manufacture_lot);

        mInfo.setMakeDate(make_date);
        mInfo.setDistributionDate(end_date);

        mInfo.setLot1(lot1);
        mInfo.setLot2(lot2);
        mInfo.setLot3(lot3);
        mInfo.setLot4(lot4);
        mInfo.setLot5(lot5);

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        mInfo.setCompanyCode(company.getCode());
        mInfo.setCenterCode(center.getCode());
        mInfo.setCustomerCode(customer.getCode());
        mInfo.setUserId(user_id);

        showProgress(true, getString(R.string.progress_check));

        INetworkManager network = new NetworkManager(mContext, mCarryInCheckNetworkListener);
        network.reqCarryInCheck(mInfo);
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
     * 반입검수 요청 결과 수신 리스너
     */
    private INetworkListener mCarryInCheckNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_CARRY_IN_CHECK_RESULT, json);
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
                if (mEditBoxCount.isFocused()) {
                    mEditEaCount.requestFocus();
                    imm.hideSoftInputFromWindow(mEditBoxCount.getWindowToken(), 0);
                }
                else if (mEditEaCount.isFocused()) {
                    mEditPltId.requestFocus();
                    imm.hideSoftInputFromWindow(mEditEaCount.getWindowToken(), 0);
                }
                else if (mEditPltId.isFocused()) {
                    mEditMakeLot.requestFocus();
                    imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
                }
                else if (mEditMakeLot.isFocused()) {
                    mEditLot1.requestFocus();
                    imm.hideSoftInputFromWindow(mEditMakeLot.getWindowToken(), 0);
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
                    if (mEditBoxCount.isFocused()) {
                        mEditEaCount.requestFocus();
                        imm.hideSoftInputFromWindow(mEditBoxCount.getWindowToken(), 0);
                    }
                    else if (mEditEaCount.isFocused()) {
                        mEditPltId.requestFocus();
                        imm.hideSoftInputFromWindow(mEditEaCount.getWindowToken(), 0);
                    }
                    else if (mEditPltId.isFocused()) {
                        mEditMakeLot.requestFocus();
                        imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
                    }
                    else if (mEditMakeLot.isFocused()) {
                        mEditLot1.requestFocus();
                        imm.hideSoftInputFromWindow(mEditMakeLot.getWindowToken(), 0);
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
            if (id == R.id.carry_in_check_detail_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.carray_in_check_detail_btn_goods_state) {
                showGoodsState();
            }
            else if (id == R.id.carry_in_check_detail_edit_manufacture_date) {
                showCalendar();
            }
            else if (id == R.id.carry_in_check_detail_edit_distribution_date) {
                showCalendar();
            }
            else if (id == R.id.carry_in_check_detail_btn_check) {
                reqCarryInCheck();
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

                case HANDLER_SHOW_CARRY_IN_CHECK_RESULT :
                    showCarryInCheckResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_SCAN_RESULT :
                    showScanResult((String) msg.obj);
                    break;
            }
        }
    };
}
