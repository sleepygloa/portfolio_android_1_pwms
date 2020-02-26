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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.barcode.IScannerListener;
import com.vertexid.wms.barcode.ScannerManager;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.info.InventoryStateChangeInfo;
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

import java.util.ArrayList;

/**
 * 재고상태 변경 상세 화면
 */
public class InventoryStateChangeDetailActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_CONFIRM_RESULT = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CONFIRM_RESULT + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;

    private EditText mEditChangeState = null;
    private EditText mEditChangeLoc = null;
    private EditText mEditChangePltId = null;
    private EditText mEditChangeBoxCount = null;
    private EditText mEditChangeEaCount = null;

    private ProgressDialog mPrgDlg = null;

    private ArrayList<CodeInfo> mArrayTo = null;

    private ScannerManager mScan = null;
    private InventoryStateChangeInfo mInfo = null;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_state_change_detail_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        Intent intent = getIntent();
        if (intent != null) {
            mInfo = (InventoryStateChangeInfo) intent.getSerializableExtra(ScrnParam.INFO_DETAIL);
            mPosition = intent.getIntExtra(ScrnParam.INFO_POSITION, 0);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.inventory_state_change_detail_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.inventory_state_change_detail_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.inventory_state_change_detail_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.inventory_state_change_detail_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        View btn_confirm = findViewById(R.id.inventory_state_change_detail_btn_confirm);
        btn_confirm.setOnClickListener(mClickListener);

        mEditChangeLoc = (EditText) findViewById(R.id.inventory_state_change_detail_edit_change_loc);
        mEditChangePltId = (EditText) findViewById(R.id.inventory_state_change_detail_edit_change_plt_id);
        mEditChangeBoxCount = (EditText) findViewById(R.id.inventory_state_change_detail_edit_change_box);
        mEditChangeEaCount = (EditText) findViewById(R.id.inventory_state_change_detail_edit_change_ea);

        mEditChangeLoc.setOnEditorActionListener(mEditorActionListener);
        mEditChangePltId.setOnEditorActionListener(mEditorActionListener);
        mEditChangeBoxCount.setOnEditorActionListener(mEditorActionListener);
        mEditChangeEaCount.setOnEditorActionListener(mEditorActionListener);

        mEditChangeLoc.requestFocus();

        ArrayList<CodeInfo> array_state = config.getCodeList(CodeInfo.CODE_TYPE_GOODS_STATE);
        String text = "";
        if (array_state != null) {
            for (int i = 0 ; i < array_state.size() ; i++) {
                CodeInfo info = array_state.get(i);
                String code = info.getCode();
                if (code.equals(mInfo.getFromGoodState())) {
                    text = info.getText();
                    break;
                }
            }
        }

        EditText edit_code = (EditText) findViewById(R.id.inventory_state_change_detail_edit_goods_code);
        EditText edit_name = (EditText) findViewById(R.id.inventory_state_change_detail_edit_goods_name);
        EditText edit_state = (EditText) findViewById(R.id.inventory_state_change_detail_edit_goods_state);
        EditText edit_loc = (EditText) findViewById(R.id.inventory_state_change_detail_edit_from_loc);
        EditText edit_plt_id = (EditText) findViewById(R.id.inventory_state_change_detail_edit_from_plt_id);
        EditText edit_box = (EditText) findViewById(R.id.inventory_state_change_detail_edit_inven_box);
        EditText edit_ea = (EditText) findViewById(R.id.inventory_state_change_detail_edit_inven_ea);

        edit_code.setText(mInfo.getGoodsCode());
        edit_name.setText(mInfo.getGoodsName());
        edit_state.setText(text);
        edit_loc.setText(mInfo.getFromLocation());
        edit_plt_id.setText(mInfo.getFromPltId());
        edit_box.setText(String.valueOf(mInfo.getInvenBoxCount()));
        edit_ea.setText(String.valueOf(mInfo.getInvenEaCount()));

        mEditChangeBoxCount.setText(String.valueOf(mInfo.getChangeBoxCount()));
        mEditChangeEaCount.setText(String.valueOf(mInfo.getChangeEaCount()));

        mArrayTo = config.getCodeList(CodeInfo.CODE_TYPE_GOODS_STATE);
        if (mArrayTo == null) {
            mArrayTo = new ArrayList<>();
            mArrayTo.clear();
        }

//        mEditChangeState.setText("정상");
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

//    /**
//     * 제품상태를 리스트 형식으로 보여줌
//     */
//    private void showFromGoodsState() {
//        if (mArrayFrom == null) {
//            mArrayFrom = new ArrayList<>();
//        }
//
//        mArrayFrom.clear();
//
//        // SQLite로부터 제품상태 목록을 가져온다
//        IConfigManager config = new ConfigManager();
////        mArrayFrom = config.getCodeList(mContext, CodeInfo.CODE_TYPE_GOODS_STATE);
//
//        CodeInfo info1 = new CodeInfo();
//        info1.setCodeInfoType(CodeInfo.CODE_TYPE_GOODS_STATE);
//        info1.setCode("001");
//        info1.setText("정상");
//        mArrayFrom.add(info1);
//
//        CodeInfo info2 = new CodeInfo();
//        info2.setCodeInfoType(CodeInfo.CODE_TYPE_GOODS_STATE);
//        info2.setCode("002");
//        info2.setText("불량");
//        mArrayFrom.add(info2);
//
//        CodeInfo info3 = new CodeInfo();
//        info3.setCodeInfoType(CodeInfo.CODE_TYPE_GOODS_STATE);
//        info3.setCode("003");
//        info3.setText("파손");
//        mArrayFrom.add(info3);
//
//        String [] goods_state = new String[mArrayFrom.size()];
//        for (int i = 0 ; i < mArrayFrom.size() ; i++) {
//            goods_state[i] = mArrayFrom.get(i).getText();
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("제품상태를 선택해주세요.");
//        builder.setItems(goods_state, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String name = mArrayFrom.get(which).getText();
//
//                EditText edit_goods_state = (EditText) findViewById(R.id.inventory_state_change_detail_edit_goods_state);
//                edit_goods_state.setText(name);
//            }
//        });
//
//        builder.show();
//    }

//    /**
//     * 제품상태를 리스트 형식으로 보여줌
//     */
//    private void showToGoodsState() {
//        if (mArrayTo == null) {
//            mArrayTo = new ArrayList<>();
//        }
//
//        mArrayTo.clear();
//
//        // SQLite로부터 제품상태 목록을 가져온다
//        IConfigManager config = new ConfigManager();
//        mArrayTo = config.getCodeList(mContext, CodeInfo.CODE_TYPE_GOODS_STATE);
//        if (mArrayTo == null) {
//            mArrayTo = new ArrayList<>();
//            mArrayTo.clear();
//        }
//
//        String [] goods_state = new String[mArrayTo.size()];
//        for (int i = 0 ; i < mArrayTo.size() ; i++) {
//            goods_state[i] = mArrayTo.get(i).getText();
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("제품상태를 선택해주세요.");
//        builder.setItems(goods_state, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String name = mArrayTo.get(which).getText();
//
//                EditText edit_goods_state = (EditText) findViewById(R.id.inventory_state_change_detail_edit_change_goods_state);
//                edit_goods_state.setText(name);
//            }
//        });
//
//        builder.show();
//    }

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
        if (mEditChangeLoc.isFocused()) {
            mEditChangeLoc.setText(scan);
            mEditChangePltId.requestFocus();

            imm.hideSoftInputFromWindow(mEditChangeLoc.getWindowToken(), 0);
        }
        else if (mEditChangePltId.isFocused()) {
            mEditChangePltId.setText(scan);
            imm.hideSoftInputFromWindow(mEditChangePltId.getWindowToken(), 0);
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
     * 확정 요청 결과 처리
     * @param json 확정요청 결과
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
                mInfo.setConfirmState(InventoryStateChangeInfo.CONFIRM_COMPLETE);
                moveDetailActivity();
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
     */
    private void moveDetailActivity() {
        Intent i = new Intent();
        i.setClass(mContext, InventoryStateChangeActivity.class);
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
     * 재고 목록 요청
     */
    private void reqConfirm() {
        String change_state = mEditChangeState.getText().toString();
        String state_code = "";
        if (change_state == null || change_state.length() <= 0) {
            showPopup(getString(R.string.not_exist_goods_state));
            return ;
        }
        else {
            for (int i = 0 ; i < mArrayTo.size() ; i++) {
                CodeInfo info = mArrayTo.get(i);
                if (change_state.equals(info.getText())) {
                    state_code = info.getCode();
                    break;
                }
            }
        }

        String change_loc = mEditChangeLoc.getText().toString();
        if (change_loc == null || change_loc.length() <= 0) {
            showPopup(getString(R.string.not_exist_to_loc));
            return ;
        }

        String change_plt_id = mEditChangePltId.getText().toString();
        if (change_plt_id == null || change_plt_id.length() <= 0) {
            showPopup(getString(R.string.not_exist_to_plt_id));
            return ;
        }

        String change_box = mEditChangeBoxCount.getText().toString();
        int box_count = 0;
        if (change_box != null && change_box.length() > 0) {
            box_count = Integer.parseInt(change_box);
        }

        String change_ea = mEditChangeEaCount.getText().toString();
        int ea_count = 0;
        if (change_ea != null && change_ea.length() > 0) {
            ea_count = Integer.parseInt(change_ea);
        }

        if (box_count == 0 && ea_count == 0) {
            showPopup(getString(R.string.not_exist_move_count));
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

        mInfo.setChangeGoodsState(state_code);
        mInfo.setChangeLocation(change_loc);
        mInfo.setChangePltId(change_plt_id);
        mInfo.setChangeBoxCount(box_count);
        mInfo.setChangeEaCount(ea_count);

        showProgress(true, getString(R.string.progress_confirm));

        INetworkManager network = new NetworkManager(mContext, mConfirmNetworkListener);
        network.reqInvenStateChangeConfirm(mInfo);
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
     * 재고목록 요청 결과 수신 리스너
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
                if (mEditChangeLoc.isFocused()) {
                    mEditChangePltId.requestFocus();
                    imm.hideSoftInputFromWindow(mEditChangeLoc.getWindowToken(), 0);
                }
                else if (mEditChangePltId.isFocused()) {
                    mEditChangeBoxCount.requestFocus();
                    imm.hideSoftInputFromWindow(mEditChangePltId.getWindowToken(), 0);
                }
                else if (mEditChangeBoxCount.isFocused()) {
                    mEditChangeEaCount.requestFocus();
                    imm.hideSoftInputFromWindow(mEditChangeBoxCount.getWindowToken(), 0);
                }
                else if (mEditChangeEaCount.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditChangeEaCount.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditChangeLoc.isFocused()) {
                        mEditChangePltId.requestFocus();
                        imm.hideSoftInputFromWindow(mEditChangeLoc.getWindowToken(), 0);
                    }
                    else if (mEditChangePltId.isFocused()) {
                        mEditChangeBoxCount.requestFocus();
                        imm.hideSoftInputFromWindow(mEditChangePltId.getWindowToken(), 0);
                    }
                    else if (mEditChangeBoxCount.isFocused()) {
                        mEditChangeEaCount.requestFocus();
                        imm.hideSoftInputFromWindow(mEditChangeBoxCount.getWindowToken(), 0);
                    }
                    else if (mEditChangeEaCount.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditChangeEaCount.getWindowToken(), 0);
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
            if (id == R.id.inventory_state_change_detail_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.inventory_state_change_detail_btn_confirm) {
                reqConfirm();
            }
//            else if (id == R.id.inventory_state_change_detail_btn_goods_state) {
//                showFromGoodsState();
//            }
//            else if (id == R.id.inventory_state_change_detail_btn_to_state) {
//                showToGoodsState();
//            }
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
