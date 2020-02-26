package com.vertexid.wms.scrn.carryin;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 반입 적치
 */
public class CarryInPileUpActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_GOODS_LIST = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_SEARCH_RESULT = HANDLER_SHOW_GOODS_LIST + 1;
    private final int HANDLER_SHOW_CONFIRM_RESULT = HANDLER_SHOW_SEARCH_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CONFIRM_RESULT + 1;

    private final int REQ_LIST = 100;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;

    private EditText mEditPltId = null;
    private EditText mEditOrderLoc = null;
    private EditText mEditToLoc = null;

    private ProgressDialog mPrgDlg = null;

    private String mCarryInOrderNumber;
    private String mCarryInNumber;
    private int mCarryInDetailNumber;

    private ArrayList<CarryInInfo> mArrayGoods;
    private CarryInInfo mInfo = null;

    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carry_in_pile_up_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.carry_in_pile_up_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.carry_in_pile_up_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.carry_in_pile_up_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        mEditPltId = (EditText) findViewById(R.id.carry_in_pile_up_edit_plt_id);
        mEditOrderLoc = (EditText) findViewById(R.id.carry_in_pile_up_edit_order_loc);
        mEditToLoc = (EditText) findViewById(R.id.carry_in_pile_up_edit_pile_up_to_loc);

        mEditPltId.requestFocus();
        mEditPltId.setOnEditorActionListener(mEditorActionListener);

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.carry_in_pile_up_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        View btn_state = findViewById(R.id.carry_in_pile_up_btn_inquiry);
        btn_state.setOnClickListener(mClickListener);
        View btn_search = findViewById(R.id.carry_in_pile_up_btn_search);
        btn_search.setOnClickListener(mClickListener);
        View btn_pile_up = findViewById(R.id.carry_in_pile_up_btn_pile_up);
        btn_pile_up.setOnClickListener(mClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQ_LIST) {
            return ;
        }

        if (resultCode != Activity.RESULT_OK) {
            return ;
        }

        mInfo = (CarryInInfo) data.getSerializableExtra(ScrnParam.INFO_DETAIL);
        if (mInfo == null) {
            return ;
        }

        String plt_id = mInfo.getPltId();

        mEditPltId.setText(plt_id);
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
            mEditToLoc.requestFocus();
            imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);

            reqSearch();
        }
        else if (mEditToLoc.isFocused()) {
            mEditToLoc.setText(scan);
            imm.hideSoftInputFromWindow(mEditToLoc.getWindowToken(), 0);
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
     * 적치할 제품 목록 요청 결과 처리
     * @param json 적치할 제품 목록 요청 결과
     */
    private void showGoodsListResult(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        if (mArrayGoods == null) {
            mArrayGoods = new ArrayList<>();
        }

        mArrayGoods.clear();

        JSONArray json_array = (JSONArray) VUtil.getValue(json, NetworkParam.ARRAY_LIST);
        if (json_array == null || json_array.length() == 0) {
            String text = getString(R.string.not_exist_list);
            showPopup(text);
            return ;
        }

        try {
            for (int i = 0 ; i < json_array.length() ; i++) {
                JSONObject json_obj = json_array.getJSONObject(i);
                if (json_obj == null || json_obj.length() == 0) {
                    continue;
                }

                Object obj1 = VUtil.getValue(json_obj, NetworkParam.GOODS_CODE);
                String code = "";
                if (obj1 != null && !obj1.toString().equals("null")) {
                    code = obj1.toString();
                }

                Object obj2 = VUtil.getValue(json_obj, NetworkParam.GOODS_NAME);
                String name = "";
                if (obj2 != null && !obj2.toString().equals("null")) {
                    name = obj2.toString();
                }

                Object obj3 = VUtil.getValue(json_obj, NetworkParam.LOC_CD);
                String loc = "";
                if (obj3 != null && !obj3.toString().equals("null")) {
                    loc = obj3.toString();
                }

                Object obj4 = VUtil.getValue(json_obj, NetworkParam.PLT_ID);
                String plt_id = "";
                if (obj4 != null && !obj4.toString().equals("null")) {
                    plt_id = obj4.toString();
                }

                Object obj5 = VUtil.getValue(json_obj, NetworkParam.ORDER_COUNT);
                int order_count = 0;
                if (obj5 != null && !obj5.toString().equals("null")) {
                    order_count = Integer.parseInt(obj5.toString());
                }

                CarryInInfo info = new CarryInInfo();
                info.setGoodsCode(code);
                info.setGoodsName(name);
                info.setLocation(loc);
                info.setPltId(plt_id);
                info.setOrderCount(order_count);

                mArrayGoods.add(info);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (mArrayGoods == null || mArrayGoods.size() == 0) {
            return ;
        }

        moveListActivity();
    }

    /**
     * 적치대상 제품 상세 정보 요청 결과 처리
     * @param json 적치대상 제품 상세 정보 결과
     */
    private void showSearchResult(JSONObject json) {
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
        String goods_name = "";
        if (obj1 != null && !obj1.toString().equals("null")) {
            goods_name = obj1.toString();
        }

        Object obj2 = VUtil.getValue(json, NetworkParam.ACQUIRE);
        int acquire = 0;
        if (obj2 != null && !obj2.toString().equals("null")) {
            acquire = Integer.parseInt(obj2.toString());
        }

        Object obj3 = VUtil.getValue(json, NetworkParam.ORDER_COUNT_BOX);
        int order_box = 0;
        if (obj3 != null && !obj3.toString().equals("null")) {
            order_box = Integer.parseInt(obj3.toString());
        }

        Object obj4 = VUtil.getValue(json, NetworkParam.ORDER_COUNT_EA);
        int order_ea = 0;
        if (obj4 != null && !obj4.toString().equals("null")) {
            order_ea = Integer.parseInt(obj4.toString());
        }

        Object obj5 = VUtil.getValue(json, NetworkParam.PILE_UP_LOCATION);
        String pile_up_loc = "";
        if (obj5 != null && !obj5.toString().equals("null")) {
            pile_up_loc = obj5.toString();
        }

        Object obj6 = VUtil.getValue(json, NetworkParam.MAKE_LOT);
        String make_lot = "";
        if (obj6 != null && !obj6.toString().equals("null")) {
            make_lot = obj6.toString();
        }

        Object obj7 = VUtil.getValue(json, NetworkParam.MAKE_DATE);
        String make_date = "";
        if (obj7 != null && !obj7.toString().equals("null")) {
            make_date = obj7.toString();
        }

        Object obj8 = VUtil.getValue(json, NetworkParam.EXPIRE_DATE);
        String end_date = "";
        if (obj8 != null && !obj8.toString().equals("null")) {
            end_date = obj8.toString();
        }

        Object obj9 = VUtil.getValue(json, NetworkParam.LOT_ATTR1);
        String lot1 = "";
        if (obj9 != null && !obj9.toString().equals("null")) {
            lot1 = obj9.toString();
        }

        Object obj10 = VUtil.getValue(json, NetworkParam.LOT_ATTR2);
        String lot2 = "";
        if (obj10 != null && !obj10.toString().equals("null")) {
            lot2 = obj10.toString();
        }

        Object obj11 = VUtil.getValue(json, NetworkParam.LOT_ATTR3);
        String lot3 = "";
        if (obj11 != null && !obj11.toString().equals("null")) {
            lot3 = obj11.toString();
        }

        Object obj12 = VUtil.getValue(json, NetworkParam.LOT_ATTR4);
        String lot4 = "";
        if (obj10 != null && !obj10.toString().equals("null")) {
            lot4 = obj12.toString();
        }

        Object obj13 = VUtil.getValue(json, NetworkParam.LOT_ATTR5);
        String lot5 = "";
        if (obj11 != null && !obj11.toString().equals("null")) {
            lot5 = obj13.toString();
        }

        Object obj14 = VUtil.getValue(json, NetworkParam.CARRY_IN_NUMBER);
        if (obj14 != null && !obj14.toString().equals("null")) {
            mCarryInNumber = obj14.toString();
        }

        Object obj15 = VUtil.getValue(json, NetworkParam.CARRY_IN_DETAIL_NUMBER);
        if (obj15 != null && !obj15.toString().equals("null")) {
            mCarryInDetailNumber = Integer.parseInt(obj15.toString());
        }

        Object obj16 = VUtil.getValue(json, NetworkParam.CARRY_IN_ORDER_NUMBER);
        if (obj16 != null && !obj16.toString().equals("null")) {
            mCarryInOrderNumber = obj14.toString();
        }

        if (mInfo == null) {
            mInfo = new CarryInInfo();
        }

        mInfo.setGoodsName(goods_name);
        mInfo.setAcquire(acquire);
        mInfo.setOrderBoxCount(order_box);
        mInfo.setOrderEaCount(order_ea);
        mInfo.setPileUpLocation(pile_up_loc);
        mInfo.setManufactureLot(make_lot);
        mInfo.setMakeDate(make_date);
        mInfo.setDistributionDate(end_date);
        mInfo.setLot1(lot1);
        mInfo.setLot2(lot2);
        mInfo.setLot3(lot3);
        mInfo.setLot4(lot4);
        mInfo.setLot5(lot5);

        mEditOrderLoc.setText(goods_name);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_complete));
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mEditPltId.setText("");
                mEditOrderLoc.setText("");
                mEditToLoc.setText("");

                mCarryInNumber = "";
                mCarryInOrderNumber = "";
                mCarryInDetailNumber = 0;
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
        i.setClass(mContext, CarryInPileUpGoodsListActivity.class);
        i.putExtra(ScrnParam.INFO_LIST, mArrayGoods);
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
     * 제품 목록 요청
     */
    private void reqGoodsList() {
        String code = mEditPltId.getText().toString();
        if (code == null || code.length() == 0) {
            showPopup(getString(R.string.not_exist_plt_id));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        CarryInInfo info = new CarryInInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setUserId(user_id);

        info.setGoodsCode(code);

        showProgress(true, getString(R.string.progress_search));

        INetworkManager network = new NetworkManager(mContext, mInquiryNetworkListener);
        network.reqCarryInPileUpGoodsList(info);
    }

    /**
     * 제품 상제 정보 요청
     */
    private void reqSearch() {
        String plt_id = mEditPltId.getText().toString();
        if (plt_id == null || plt_id.length() == 0) {
            showPopup(getString(R.string.not_exist_plt_id));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        CarryInInfo info = new CarryInInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setUserId(user_id);

        info.setPltId(plt_id);

        INetworkManager network = new NetworkManager(mContext, mSearchNetworkListener);
        network.reqCarryInPileUpSearch(info);
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

        String to_loc = mEditToLoc.getText().toString();
        if (to_loc.length() == 0) {
            showPopup(getString(R.string.not_exist_loc_confirm));
            return ;
        }

        EditText edit_loc = (EditText) findViewById(R.id.warehouse_pile_up_edit_pile_up_loc);
        String loc = edit_loc.getText().toString();
        if (!loc.equals(to_loc)) {
            showPopup(getString(R.string.not_equal_picking_loc_scan));
            return ;
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        CarryInInfo info = new CarryInInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setUserId(user_id);

        info.setCarryInNumber(mCarryInNumber);
        info.setCarryInDetailNumber(mCarryInDetailNumber);
        info.setCarryInOrderNumber(mCarryInOrderNumber);

        showProgress(true, getString(R.string.progress_confirm));
        INetworkManager network = new NetworkManager(mContext, mConfirmNetworkListener);
        network.reqCarryInPileUpConfirm(info);
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
     * 제품 상세 정보 요청 결과 수신 리스너
     */
    private INetworkListener mInquiryNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_GOODS_LIST, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

    /**
     * 제품 상세 정보 요청 결과 수신 리스너
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
                if (mEditPltId.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
                }
                else if (mEditToLoc.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditToLoc.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditPltId.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditPltId.getWindowToken(), 0);
                    }
                    else if (mEditToLoc.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditToLoc.getWindowToken(), 0);
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
            if (id == R.id.carry_in_pile_up_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.carry_in_pile_up_btn_inquiry) {
                reqGoodsList();
            }
            else if (id == R.id.carry_in_pile_up_btn_search) {
                reqSearch();
            }
            else if (id == R.id.carry_in_pile_up_btn_pile_up) {
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

                case HANDLER_SHOW_GOODS_LIST :
                    showGoodsListResult((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_SEARCH_RESULT :
                    showSearchResult((JSONObject) msg.obj);
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
