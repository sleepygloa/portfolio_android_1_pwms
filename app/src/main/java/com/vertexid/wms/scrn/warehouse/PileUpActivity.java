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
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 적치 화면
 */
public class PileUpActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_SEARCH_RESULT = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_GOODS_SEARCH_RESULT = HANDLER_SHOW_SEARCH_RESULT + 1;
    private final int HANDLER_SHOW_CONFIRM_RESULT = HANDLER_SHOW_GOODS_SEARCH_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CONFIRM_RESULT + 1;

    private final int REQ_LIST = 100;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;

    private EditText mEditPltId = null;
    private EditText mEditLocConfirm = null;

    private ProgressDialog mPrgDlg = null;

    private ArrayList<WareHouseInfo> mArray = null;

    private WareHouseInfo mInfo = null;
    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warehouse_pile_up_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        mArray = new ArrayList<>();
        mArray.clear();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.warehouse_pile_up_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.warehouse_pile_up_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.warehouse_pile_up_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        mEditPltId = (EditText) findViewById(R.id.warehouse_pile_up_edit_plt_id);
        mEditLocConfirm = (EditText) findViewById(R.id.warehouse_pile_up_edit_pile_up_loc_confirm);

        mEditPltId.setOnEditorActionListener(mEditorActionListener);
        mEditLocConfirm.setOnEditorActionListener(mEditorActionListener);

        mEditPltId.requestFocus();

        Button btn_menu = (Button) findViewById(R.id.warehouse_pile_up_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        Button btn_code = (Button) findViewById(R.id.warehouse_pile_up_btn_goods_code);
        btn_code.setOnClickListener(mClickListener);
        Button btn_search = (Button) findViewById(R.id.warehouse_pile_up_btn_search);
        btn_search.setOnClickListener(mClickListener);
        Button btn_confirm = (Button) findViewById(R.id.warehouse_pile_up_btn_confirm);
        btn_confirm.setOnClickListener(mClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQ_LIST) {
            return ;
        }

        if (resultCode != Activity.RESULT_OK) {
            return ;
        }

        WareHouseInfo info = (WareHouseInfo) data.getSerializableExtra(ScrnParam.INFO_DETAIL);
        if (info == null) {
            return ;
        }

        mEditPltId.setText(info.getPltId());

        EditText edit_loc = (EditText) findViewById(R.id.warehouse_pile_up_edit_pile_up_loc);
        edit_loc.setText(info.getLocation());
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
     * 스캐너로 스캔한 내용을 보여준다.
     * @param scan 스캐너로 스캔한 내용
     */
    private void showScanResult(String scan) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (mEditPltId.isFocused()) {
            mEditPltId.setText(scan);
            imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);

            mEditLocConfirm.requestFocus();
            reqSearch();
        }
        else if (mEditLocConfirm.isFocused()) {
            mEditLocConfirm.setText(scan);
            imm.hideSoftInputFromWindow(mEditLocConfirm.getWindowToken(), 0);
        }
    }

    /**
     * 로그 아웃 결과를 처리
     * @param json 로그아웃 결과
     */
    private void showLogOutResult(JSONObject json) {
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

        Intent closeActivity = new Intent(ACTION_ACTIVITY_CLOSE);
        closeActivity.setPackage(getPackageName());
        sendBroadcast(closeActivity);
    }

    /**
     * 제품 코드를 통해 요청한 제품 목록을 보여준다.
     * @param json 제품 목록
     */
    private void showGoodsSearchResult(JSONObject json) {
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

        JSONArray json_array = (JSONArray) VUtil.getValue(json, NetworkParam.ARRAY_LIST);
        if (json_array == null) {
            String text = getString(R.string.not_exist_list);
            showPopup(text);
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
                    continue;
                }

                String location = (String) VUtil.getValue(json_obj, NetworkParam.PILE_UP_LOCATION);
                String goods_code = (String) VUtil.getValue(json_obj, NetworkParam.GOODS_CODE);
                String plt_id = (String) VUtil.getValue(json_obj, NetworkParam.PLT_ID);
                int order_count = (int) VUtil.getValue(json_obj, NetworkParam.ORDER_COUNT);

                WareHouseInfo info = new WareHouseInfo();
                info.setLocation(location);
                info.setGoods(goods_code);
                info.setPltId(plt_id);
                info.setOrderCount(order_count);

                mArray.add(info);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        moveListActivity();
    }

    /**
     * 조회 요청 결과 처리
     * @param json 조회 요청 결과
     */
    private void showSearchResult(JSONObject json) {
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

        Object obj1 = VUtil.getValue(json, NetworkParam.PLT_ID);
        String plt_id = "";
        if (obj1 != null && !obj1.toString().equals("null")) {
            plt_id = obj1.toString();
        }

        Object obj2 = VUtil.getValue(json, NetworkParam.WAREHOUSE_ORDER_NUMBER);
        String order_number = "";
        if (obj2 != null && !obj2.toString().equals("null")) {
            order_number = obj2.toString();
        }

        Object obj3 = VUtil.getValue(json, NetworkParam.WAREHOUSE_DETAIL_NUMBER);
        int detail_number = 0;
        if (obj3 != null && !obj3.toString().equals("null")) {
            detail_number = Integer.parseInt(obj3.toString());
        }

        Object obj5 = VUtil.getValue(json, NetworkParam.GOODS_NAME);
        String goods_name = "";
        if (obj5 != null && !obj5.toString().equals("null")) {
            goods_name = obj5.toString();
        }

        Object obj6 = VUtil.getValue(json, NetworkParam.ACQUIRE);
        int acquire = 0;
        if (obj6 != null && !obj6.toString().equals("null")) {
            acquire = Integer.parseInt(obj6.toString());
        }

        Object obj7 = VUtil.getValue(json, NetworkParam.ORDER_COUNT_BOX);
        int order_count_box = 0;
        if (obj7 != null && !obj7.toString().equals("null")) {
            order_count_box = Integer.parseInt(obj7.toString());
        }

        Object obj8 = VUtil.getValue(json, NetworkParam.ORDER_COUNT_EA);
        int order_count_ea = 0;
        if (obj8 != null && !obj8.toString().equals("null")) {
            order_count_ea = Integer.parseInt(obj8.toString());
        }

        Object obj9 = VUtil.getValue(json, NetworkParam.PILE_UP_LOCATION);
        String pile_up_loc = "";
        if (obj9 != null && !obj9.toString().equals("null")) {
            pile_up_loc = obj9.toString();
        }

        Object obj10 = VUtil.getValue(json, NetworkParam.MAKE_DATE);
        String make_date = "";
        if (obj10 != null && !obj10.toString().equals("null")) {
            make_date = obj10.toString();
        }

        Object obj11 = VUtil.getValue(json, NetworkParam.EXPIRE_DATE);
        String end_date = "";
        if (obj11 != null && !obj11.toString().equals("null")) {
            end_date = obj11.toString();
        }

        Object obj12 = VUtil.getValue(json, NetworkParam.MAKE_LOT);
        String make_lot = "";
        if (obj12 != null && !obj12.toString().equals("null")) {
            make_lot = obj12.toString();
        }

        Object obj13 = VUtil.getValue(json, NetworkParam.LOT_ATTR1);
        String lot1 = "";
        if (obj13 != null && !obj13.toString().equals("null")) {
            lot1 = obj13.toString();
        }

        Object obj14 = VUtil.getValue(json, NetworkParam.LOT_ATTR2);
        String lot2 = "";
        if (obj14 != null && !obj14.toString().equals("null")) {
            lot2 = obj14.toString();
        }

        Object obj15 = VUtil.getValue(json, NetworkParam.LOT_ATTR3);
        String lot3 = "";
        if (obj15 != null && !obj15.toString().equals("null")) {
            lot3 = obj15.toString();
        }

        Object obj16 = VUtil.getValue(json, NetworkParam.LOT_ATTR4);
        String lot4 = "";
        if (obj16 != null && !obj16.toString().equals("null")) {
            lot4 = obj16.toString();
        }

        Object obj17 = VUtil.getValue(json, NetworkParam.LOT_ATTR5);
        String lot5 = "";
        if (obj17 != null && !obj17.toString().equals("null")) {
            lot5 = obj17.toString();
        }

        Object obj18 = VUtil.getValue(json, NetworkParam.WAREHOUSE_NUMBER);
        String ware_number = "";
        if (obj18 != null && !obj18.toString().equals("null")) {
            ware_number = obj18.toString();
        }

        if (mInfo == null) {
            mInfo = new WareHouseInfo();
        }

        mInfo.setWareHouseNumber(ware_number);
        mInfo.setPltId(plt_id);
        mInfo.setWareHouseOrderNumber(order_number);
        mInfo.setWareHouseDetailNumber(detail_number);
        mInfo.setGoodsName(goods_name);
        mInfo.setAcquireCount(acquire);
        mInfo.setOrderBoxCount(order_count_box);
        mInfo.setOrderEaCount(order_count_ea);
        mInfo.setLocation(pile_up_loc);
        mInfo.setMakeDate(make_date);
        mInfo.setDistributionDate(end_date);
        mInfo.setManufactureLot(make_lot);
        mInfo.setLotAttribute1(lot1);
        mInfo.setLotAttribute2(lot2);
        mInfo.setLotAttribute3(lot3);
        mInfo.setLotAttribute4(lot4);
        mInfo.setLotAttribute5(lot5);

        EditText edit_loc = (EditText) findViewById(R.id.warehouse_pile_up_edit_pile_up_loc);
        edit_loc.setText(mInfo.getLocation());

        mEditLocConfirm.requestFocus();
    }

    /**
     * 확정 요청 결과 처리
     * @param json 확정 요청 결과
     */
    private void showConfirmResult(JSONObject json) {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.pile_up_complete));
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText edit_loc = (EditText) findViewById(R.id.warehouse_pile_up_edit_pile_up_loc);
                edit_loc.setText("");

                mEditPltId.setText("");
                mEditLocConfirm.setText("");

                mEditPltId.requestFocus();

                mInfo = null;
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
     * 조회 화면으로 이동
     */
    private void moveListActivity() {
        Intent i = new Intent();
        i.setClass(mContext, PileUpGoodsListActivity.class);
        i.putExtra(ScrnParam.INFO_LIST, mArray);
        startActivityForResult(i, REQ_LIST);
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
     * 파렛트ID를 통해 제품조회 요청
     */
    private void reqPltIDSearch() {
        String code = mEditPltId.getText().toString();
        if (code == null || code.length() == 0) {
            showPopup(getString(R.string.not_exist_plt_id));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        WareHouseInfo info = new WareHouseInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());

        info.setGoods(code);

        showProgress(true, getString(R.string.progress_req_goods_code_list));

        INetworkManager network = new NetworkManager(mContext, mGoodsSearchNetworkListener);
        network.reqWarePileUpGoodsSearch(info);
    }

    /**
     * 적치 내용 조회 요청
     */
    private void reqSearch() {
        String plt_id = mEditPltId.getText().toString();
        if (plt_id.length() == 0) {
            showPopup(getString(R.string.not_exist_plt_id));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        WareHouseInfo info = new WareHouseInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());

        info.setPltId(plt_id);

        INetworkManager network = new NetworkManager(mContext, mSearchNetworkListener);
        network.reqWarePileUpSearch(info);
    }

    /**
     * 확정 요청
     */
    private void reqConfirm() {
        String plt_id = mEditPltId.getText().toString();
        if (plt_id.length() == 0) {
            showPopup(getString(R.string.not_exist_plt_id));
            return ;
        }

        String loc_confirm = mEditLocConfirm.getText().toString();
        if (loc_confirm.length() == 0) {
            showPopup(getString(R.string.not_exist_loc_confirm));
            return ;
        }

        EditText edit_loc = (EditText) findViewById(R.id.warehouse_pile_up_edit_pile_up_loc);
        String loc = edit_loc.getText().toString();
        if (!loc.equals(loc_confirm)) {
            showPopup(getString(R.string.not_equal_picking_loc_scan));
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

        mInfo.setLocationConfirm(loc_confirm);

        showProgress(true, getString(R.string.progress_confirm));
        INetworkManager network = new NetworkManager(mContext, mConfirmNetworkListener);
        network.reqWarePileUpConfirm(mInfo);
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
     * 제품조회 요청 결과 수신 리스너
     */
    private INetworkListener mGoodsSearchNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_GOODS_SEARCH_RESULT, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

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

            nextState(HANDLER_SHOW_SEARCH_RESULT, json);
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
                if (mEditPltId.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
                }
                else if (mEditLocConfirm.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditLocConfirm.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditPltId.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
                    }
                    else if (mEditLocConfirm.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditLocConfirm.getWindowToken(), 0);
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
            if (id == R.id.warehouse_pile_up_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.warehouse_pile_up_btn_goods_code) {
                reqPltIDSearch();
            }
            else if (id == R.id.warehouse_pile_up_btn_search) {
                reqSearch();
            }
            else if (id == R.id.warehouse_pile_up_btn_confirm) {
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

                case HANDLER_SHOW_SEARCH_RESULT :
                    showSearchResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_GOODS_SEARCH_RESULT :
                    showGoodsSearchResult((JSONObject) msg.obj);
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
