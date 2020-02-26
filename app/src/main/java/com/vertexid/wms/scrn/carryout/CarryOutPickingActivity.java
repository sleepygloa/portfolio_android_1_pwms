package com.vertexid.wms.scrn.carryout;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.barcode.IScannerListener;
import com.vertexid.wms.barcode.ScannerManager;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CarryOutInfo;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.network.INetworkListener;
import com.vertexid.wms.network.INetworkManager;
import com.vertexid.wms.network.NError;
import com.vertexid.wms.network.NetworkManager;
import com.vertexid.wms.network.NetworkParam;
import com.vertexid.wms.scrn.BaseActivity;
import com.vertexid.wms.scrn.ScrnParam;
import com.vertexid.wms.scrn.carryin.CarryInActivity;
import com.vertexid.wms.scrn.inventory.InventoryActivity;
import com.vertexid.wms.scrn.search.SearchActivity;
import com.vertexid.wms.scrn.setting.SettingActivity;
import com.vertexid.wms.scrn.takeout.TakeOutActivity;
import com.vertexid.wms.scrn.warehouse.WareHouseActivity;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VLog;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 반출 피킹 화면
 */
public class CarryOutPickingActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_CONFIRM_RESULT = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CONFIRM_RESULT + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;

    private EditText mEditLocScan = null;
    private EditText mEditPltIdScan = null;
    private EditText mEditPickingBox = null;
    private EditText mEditPickingEa = null;
    private EditText mEditReason = null;

    private ProgressDialog mPrgDlg = null;

    private ArrayList<CodeInfo> mArrayReason = null;

    private CarryOutInfo mInfo = null;
    private int mPosition = 0;

    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carry_out_picking_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        Intent intent = getIntent();
        if (intent != null) {
            mInfo = (CarryOutInfo) intent.getSerializableExtra(ScrnParam.INFO_DETAIL);
            mPosition = intent.getIntExtra(ScrnParam.INFO_POSITION, 0);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.carry_out_picking_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.carry_out_picking_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.carry_out_picking_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.carry_out_picking_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        View btn_reason = findViewById(R.id.carry_out_picking_btn_not_carry_out_reason);
        btn_reason.setOnClickListener(mClickListener);
        View btn_confirm = findViewById(R.id.carry_out_picking_btn_confirm);
        btn_confirm.setOnClickListener(mClickListener);

        mEditLocScan = (EditText) findViewById(R.id.carry_out_picking_edit_picking_loc_scan);
        mEditPltIdScan = (EditText) findViewById(R.id.carry_out_picking_edit_plt_id_scan);
        mEditPickingBox = (EditText) findViewById(R.id.carry_out_picking_edit_picking_box);
        mEditPickingEa = (EditText) findViewById(R.id.carry_out_picking_edit_picking_ea);
        mEditReason = (EditText) findViewById(R.id.carry_out_picking_edit_not_carry_out_reason);

        mEditLocScan.requestFocus();

        mEditLocScan.setOnEditorActionListener(mEditorActionListener);
        mEditPltIdScan.setOnEditorActionListener(mEditorActionListener);
        mEditPickingBox.setOnEditorActionListener(mEditorActionListener);
        mEditPickingEa.setOnEditorActionListener(mEditorActionListener);

        EditText edit_picking_loc = (EditText) findViewById(R.id.carry_out_picking_edit_picking_loc);
        EditText edit_goods_code = (EditText) findViewById(R.id.carry_out_picking_edit_goods_code);
        EditText edit_goods_name = (EditText) findViewById(R.id.carry_out_picking_edit_goods_name);
        EditText edit_plt_id = (EditText) findViewById(R.id.carry_out_picking_edit_plt_id);
        EditText edit_order_box = (EditText) findViewById(R.id.carry_out_picking_edit_order_box);
        EditText edit_order_ea = (EditText) findViewById(R.id.carry_out_picking_edit_order_ea);
        EditText edit_lot = (EditText) findViewById(R.id.carry_out_picking_edit_manufacture_lot);
        EditText edit_make_date = (EditText) findViewById(R.id.carry_out_picking_edit_manufacture_date);
        EditText edit_dist_date = (EditText) findViewById(R.id.carry_out_picking_edit_distribution_date);
        EditText edit_lot1 = (EditText) findViewById(R.id.carry_out_picking_edit_lot1);
        EditText edit_lot2 = (EditText) findViewById(R.id.carry_out_picking_edit_lot2);
        EditText edit_lot3 = (EditText) findViewById(R.id.carry_out_picking_edit_lot3);
        EditText edit_lot4 = (EditText) findViewById(R.id.carry_out_picking_edit_lot4);
        EditText edit_lot5 = (EditText) findViewById(R.id.carry_out_picking_edit_lot5);

        String picking_loc = "";
        String goods_code = "";
        String goods_name = "";
        String plt_id = "";
        int order_box = 0;
        int order_ea = 0;
        String lot = "";
        String make_date = "";
        String dist_date = "";
        String lot1 = "";
        String lot2 = "";
        String lot3 = "";
        String lot4 = "";
        String lot5 = "";

        if (mInfo != null) {
            picking_loc = mInfo.getLocation();
            goods_code = mInfo.getGoodsCode();
            goods_name = mInfo.getGoodsName();
            plt_id = mInfo.getPltId();
            order_box = mInfo.getOrderBoxCount();
            order_ea = mInfo.getOrderEaCount();
            lot = mInfo.getManufactureLot();
            make_date = mInfo.getMakeDate();
            dist_date = mInfo.getDistributionDate();
            lot1 = mInfo.getLot1();
            lot2 = mInfo.getLot2();
            lot3 = mInfo.getLot3();
            lot4 = mInfo.getLot4();
            lot5 = mInfo.getLot5();
        }

        edit_picking_loc.setText(picking_loc);
        edit_goods_code.setText(goods_code);
        edit_goods_name.setText(goods_name);
        edit_plt_id.setText(plt_id);
        edit_order_box.setText(String.valueOf(order_box));
        edit_order_ea.setText(String.valueOf(order_ea));
        edit_lot.setText(lot);
        edit_make_date.setText(make_date);
        edit_dist_date.setText(dist_date);
        edit_lot1.setText(lot1);
        edit_lot2.setText(lot2);
        edit_lot3.setText(lot3);
        edit_lot4.setText(lot4);
        edit_lot5.setText(lot5);

        mEditPickingBox.setText(String.valueOf(order_box));
        mEditPickingEa.setText(String.valueOf(order_ea));

        mArrayReason = config.getCodeList(CodeInfo.CODE_TYPE_CARRY_OUT_REASON);
        if (mArrayReason == null) {
            mArrayReason = new ArrayList<>();
            mArrayReason.clear();
        }
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
     * 반출 사유를 팝업 형태로 보여줌
     */
    private void showNotCarryOutReason() {
        if (mArrayReason == null) {
            mArrayReason = new ArrayList<>();
        }

        mArrayReason.clear();

        // SQLite로부터 제품상태 목록을 가져온다
        IConfigManager config = new ConfigManager(mContext);
        mArrayReason = config.getCodeList(CodeInfo.CODE_TYPE_CARRY_OUT_REASON);
        if (mArrayReason == null) {
            return ;
        }

        String [] reason = new String[mArrayReason.size()];
        for (int i = 0 ; i < mArrayReason.size() ; i++) {
            reason[i] = mArrayReason.get(i).getText();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("반출사유를 선택해주세요.");
        builder.setItems(reason, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = mArrayReason.get(which).getText();
                mEditReason.setText(name);
            }
        });

        builder.show();
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

    private void showScanResult(String text) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (mEditLocScan.isFocused()) {
            mEditLocScan.setText(text);
            mEditPltIdScan.requestFocus();
            imm.hideSoftInputFromWindow(mEditLocScan.getWindowToken(), 0);
        }
        else if (mEditPltIdScan.isFocused()) {
            mEditPltIdScan.setText(text);
            mEditPickingBox.requestFocus();
            imm.hideSoftInputFromWindow(mEditPltIdScan.getWindowToken(), 0);
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
     * 확정 요청 결과 처리
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_complete));
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mInfo.setConfirmState(CarryOutInfo.CONFRIM_STATE_COMPLETE);
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
        finish();
    }

    /**
     * 다음 화면으로 이동
     */
    private void moveNextActivity() {
        Intent i = new Intent();
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
     * 확정 요청
     */
    private void reqConfirm() {
        String loc_scan = mEditLocScan.getText().toString();
        if (loc_scan.length() <= 0) {
            showPopup(getString(R.string.not_exist_picking_loc_scan));
            return ;
        }

        String plt_id_scan = mEditPltIdScan.getText().toString();
        if (plt_id_scan.length() <= 0) {
            showPopup(getString(R.string.not_exist_picking_plt_id_scan));
            return ;
        }

        String box = mEditPickingBox.getText().toString();
        int box_count = 0;
        if (box.length() > 0) {
            box_count = Integer.parseInt(box);
        }

        String ea = mEditPickingEa.getText().toString();
        int ea_count = 0;
        if (ea.length() > 0) {
            ea_count = Integer.parseInt(ea);
        }

        String reason = mEditReason.getText().toString();
        if (reason.length() <= 0) {
            reason = "";
        }

        if (box_count == 0 && ea_count == 0) {
            showPopup(getString(R.string.not_exist_picking_count));
            return ;
        }

        EditText edit_order_box = (EditText) findViewById(R.id.carry_out_picking_edit_order_box);
        EditText edit_order_ea = (EditText) findViewById(R.id.carry_out_picking_edit_order_ea);

        String order_box = edit_order_box.getText().toString();
        int order_count_box = 0;
        if (order_box.length() > 0) {
            order_count_box = Integer.parseInt(order_box);
        }

        String order_ea = edit_order_ea.getText().toString();
        int order_count_ea = 0;
        if (order_ea.length() > 0) {
            order_count_ea = Integer.parseInt(order_ea);
        }

        if (order_count_box > box_count || order_count_ea > ea_count) {
            showPopup(getString(R.string.not_exist_carry_out_reason));
            return ;
        }

        for (int i = 0 ; i < mArrayReason.size() ; i++) {
            CodeInfo info = mArrayReason.get(i);
            String text = info.getText();
            if (text.equals(reason)) {
                reason = info.getCode();
                break;
            }
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

        mInfo.setLocScan(loc_scan);
        mInfo.setPltIdScan(plt_id_scan);
        mInfo.setCarryOutReason(reason);
        mInfo.setPickingBoxCount(box_count);
        mInfo.setPickingEaCount(ea_count);

        showProgress(true, getString(R.string.progress_confirm));
        INetworkManager network = new NetworkManager(mContext, mConfirmNetworkListener);
        network.reqCarryOutPickingConfirm(mInfo);
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
                if (mEditLocScan.isFocused()) {
                    mEditPltIdScan.requestFocus();
                    imm.hideSoftInputFromWindow(mEditLocScan.getWindowToken(), 0);
                }
                else if (mEditPltIdScan.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditPltIdScan.getWindowToken(), 0);
                }
                else if (mEditPickingBox.isFocused()) {
                    mEditPickingEa.requestFocus();
                    imm.hideSoftInputFromWindow(mEditPickingBox.getWindowToken(), 0);
                }
                else if (mEditPickingEa.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditPickingEa.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditLocScan.isFocused()) {
                        mEditPltIdScan.requestFocus();
                        imm.hideSoftInputFromWindow(mEditLocScan.getWindowToken(), 0);
                    }
                    else if (mEditPltIdScan.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditPltIdScan.getWindowToken(), 0);
                    }
                    else if (mEditPickingBox.isFocused()) {
                        mEditPickingEa.requestFocus();
                        imm.hideSoftInputFromWindow(mEditPickingBox.getWindowToken(), 0);
                    }
                    else if (mEditPickingEa.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditPickingEa.getWindowToken(), 0);
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
            if (id == R.id.carry_out_picking_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.carry_out_picking_btn_not_carry_out_reason) {
                showNotCarryOutReason();
            }
            else if (id == R.id.carry_out_picking_btn_confirm) {
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
