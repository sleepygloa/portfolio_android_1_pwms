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

/**
 * 재고조사 상세 화면
 */
public class InventoryInquiryDetailActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_CONFIRM_RESULT = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CONFIRM_RESULT + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;

    private EditText mEditActualityBoxCount = null;
    private EditText mEditActualityEaCount = null;
    private EditText mEditPltScan = null;

    private ProgressDialog mPrgDlg = null;

    private InvenInquiryInfo mInfo = null;
    private int mPosition;

    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_inquiry_detail_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        Intent intent = getIntent();
        if (intent != null) {
            mInfo = (InvenInquiryInfo) intent.getSerializableExtra(ScrnParam.INFO_DETAIL);
            mPosition = intent.getIntExtra(ScrnParam.INFO_POSITION, 0);
        }

        String location = mInfo.getLocation();
        String goods_code = mInfo.getGoodsCode();
        String goods_name = mInfo.getGoodsName();
        int inven_box = mInfo.getInvenBoxCount();
        int inven_ea = mInfo.getInvenEaCount();
        String plt_id = mInfo.getPltId();
        String make_date = mInfo.getMakeDate();
        String distribution_date = mInfo.getDistributionDate();
        String make_lot = mInfo.getManufactureLot();
        String lot1 = mInfo.getLotAttribute1();
        String lot2 = mInfo.getLotAttribute2();
        String lot3 = mInfo.getLotAttribute3();
        String lot4 = mInfo.getLotAttribute4();
        String lot5 = mInfo.getLotAttribute5();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.inventory_inquiry_detail_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.inventory_inquiry_detail_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.inventory_inquiry_detail_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.inventory_inquiry_detail_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        Button btn_confirm = (Button) findViewById(R.id.inventory_inquiry_detail_btn_confirm);
        btn_confirm.setOnClickListener(mClickListener);

        mEditActualityBoxCount = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_actuality_box);
        mEditActualityEaCount = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_actuality_ea);
        mEditPltScan = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_plt_scan);

        mEditActualityBoxCount.setOnEditorActionListener(mEditorActionListener);
        mEditActualityEaCount.setOnEditorActionListener(mEditorActionListener);
        mEditPltScan.setOnEditorActionListener(mEditorActionListener);

        mEditActualityBoxCount.requestFocus();

        EditText edit_location = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_loc);
        edit_location.setText(location);
        EditText edit_goods_code = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_goods_code);
        edit_goods_code.setText(goods_code);
        EditText edit_goods_name = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_goods_name);
        edit_goods_name.setText(goods_name);
        EditText edit_inven_box = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_inven_box);
        edit_inven_box.setText(String.valueOf(inven_box));
        EditText edit_inven_ea = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_inven_ea);
        edit_inven_ea.setText(String.valueOf(inven_ea));
        EditText edit_plt_id = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_plt_id);
        edit_plt_id.setText(plt_id);
        EditText edit_manufacture_lot = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_manufacture_lot);
        edit_manufacture_lot.setText(make_lot);
        EditText edit_make_date = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_manufacture_date);
        edit_make_date.setText(make_date);
        EditText edit_distribution_date = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_distribution_date);
        edit_distribution_date.setText(distribution_date);
        EditText edit_lot1 = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_lot1);
        edit_lot1.setText(lot1);
        EditText edit_lot2 = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_lot2);
        edit_lot2.setText(lot2);
        EditText edit_lot3 = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_lot3);
        edit_lot3.setText(lot3);
        EditText edit_lot4 = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_lot4);
        edit_lot4.setText(lot4);
        EditText edit_lot5 = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_lot5);
        edit_lot5.setText(lot5);

        int actual_box = mInfo.getActualityBoxCount();
        int actual_ea = mInfo.getActualityEaCount();
        mEditActualityBoxCount.setText(String.valueOf(actual_box));
        mEditActualityEaCount.setText(String.valueOf(actual_ea));
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
        if (mEditPltScan.isFocused()) {
            mEditPltScan.setText(scan);
            imm.hideSoftInputFromWindow(mEditPltScan.getWindowToken(), 0);
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
                mInfo.setProcessState(InvenInquiryInfo.STATE_DONE);
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
        mEditPltScan = (EditText) findViewById(R.id.inventory_inquiry_detail_edit_plt_scan);
        String plt_scan = mEditPltScan.getText().toString();
        if (plt_scan == null || plt_scan.length() <= 0) {
            showPopup(getString(R.string.not_exist_plt_id_scan));
            return ;
        }

        String actuality_box = mEditActualityBoxCount.getText().toString();
        int box_count = 0;
        if (actuality_box != null && actuality_box.length() > 0) {
            box_count = Integer.parseInt(actuality_box);
        }

        String actuality_ea = mEditActualityEaCount.getText().toString();
        int ea_count = 0;
        if (actuality_ea != null && actuality_ea.length() > 0) {
            ea_count = Integer.parseInt(actuality_ea);
        }

        if (box_count == 0 && ea_count == 0) {
            showPopup(getString(R.string.not_exist_actuality));
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

        mInfo.setPltScanCode(plt_scan);
        mInfo.setActualityBoxCount(box_count);
        mInfo.setActualityEaCount(ea_count);

        showProgress(true, getString(R.string.progress_confirm));

        INetworkManager network = new NetworkManager(mContext, mConfirmNetworkListener);
        network.reqInvenInquiryConfirm(mInfo);
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
                if (mEditActualityBoxCount.isFocused()) {
                    mEditActualityEaCount.requestFocus();
                    imm.hideSoftInputFromWindow(mEditActualityBoxCount.getWindowToken(), 0);
                }
                else if (mEditActualityEaCount.isFocused()) {
                    mEditPltScan.requestFocus();
                    imm.hideSoftInputFromWindow(mEditActualityEaCount.getWindowToken(), 0);
                }
                else if (mEditPltScan.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditPltScan.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditActualityBoxCount.isFocused()) {
                        mEditActualityEaCount.requestFocus();
                        imm.hideSoftInputFromWindow(mEditActualityBoxCount.getWindowToken(), 0);
                    }
                    else if (mEditActualityEaCount.isFocused()) {
                        mEditPltScan.requestFocus();
                        imm.hideSoftInputFromWindow(mEditActualityEaCount.getWindowToken(), 0);
                    }
                    else if (mEditPltScan.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditPltScan.getWindowToken(), 0);
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
            if (id == R.id.inventory_inquiry_detail_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.inventory_inquiry_detail_btn_confirm) {
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
