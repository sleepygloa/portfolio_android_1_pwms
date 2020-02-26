package com.vertexid.wms.scrn.carryout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
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
import com.vertexid.wms.scrn.carryout.adapter.CarryOutItemAdapter;
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
 * 반출관리 화면
 */
public class CarryOutActivity extends BaseActivity {
    private final int REQ_DETAIL = 100;
    private final int REQ_INQUIRY = REQ_DETAIL + 1;

    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_CARRY_OUT_LIST = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_CARRY_OUT_LIST + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;
    private TextView mTextTotalCount = null;

    private EditText mEditNumber = null;
    private EditText mEditCode = null;
    private EditText mEditSupplier = null;

    private ProgressDialog mPrgDlg = null;

    private CarryOutItemAdapter mAdapter = null;

    private ArrayList<CarryOutInfo> mArray = null;
    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carry_out_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        mArray = new ArrayList<>();
        mArray.clear();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.carry_out_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.carry_out_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.carry_out_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.carry_out_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        Button btn_inquiry = (Button) findViewById(R.id.carry_out_btn_inquiry);
        btn_inquiry.setOnClickListener(mClickListener);
        Button btn_search = (Button) findViewById(R.id.carry_out_btn_search);
        btn_search.setOnClickListener(mClickListener);

        mEditNumber = (EditText) findViewById(R.id.carry_out_edit_number);
        mEditCode = (EditText) findViewById(R.id.carry_out_edit_goods_code);
        mEditSupplier = (EditText) findViewById(R.id.carry_out_edit_supplier);

        mEditNumber.setOnEditorActionListener(mEditorActionListener);
        mEditCode.setOnEditorActionListener(mEditorActionListener);

        mEditNumber.requestFocus();

        mTextTotalCount = (TextView) findViewById(R.id.carry_out_text_total_count);
        mTextTotalCount.setText("총 " + String.valueOf(mArray.size()) + "건");

        mAdapter = new CarryOutItemAdapter(mContext, mArray);
        ListView list_view = (ListView) findViewById(R.id.carry_out_list);
        list_view.setOnItemClickListener(mItemClickListener);
        list_view.setAdapter(mAdapter);
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

            CarryOutInfo info = (CarryOutInfo) data.getSerializableExtra(ScrnParam.INFO_DETAIL);
            int position = data.getIntExtra(ScrnParam.INFO_POSITION, 0);

            mArray.set(position, info);
            mAdapter.notifyDataSetChanged();

            reqCarryOutList();
        }
        else if (requestCode == REQ_INQUIRY) {
            if (resultCode != Activity.RESULT_OK) {
                return ;
            }

            if (data == null) {
                return ;
            }

            String number = data.getStringExtra(ScrnParam.NUMBER);
            String supplier = data.getStringExtra(ScrnParam.SUPPLIER);

            mEditNumber.setText(number);
            mEditSupplier.setText(supplier);

            reqCarryOutList();
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

        if (mEditNumber.isFocused()) {
            mEditNumber.setText(text);
            imm.hideSoftInputFromWindow(mEditNumber.getWindowToken(), 0);

            reqCarryOutList();
        }
        else if (mEditCode.isFocused()) {
            mEditCode.setText(text);
            imm.hideSoftInputFromWindow(mEditCode.getWindowToken(), 0);

            if (mArray == null) {
                return ;
            }

            int position = 0;
            boolean is_exist = false;

            for (int i = 0 ; i < mArray.size() ; i++) {
                CarryOutInfo info = mArray.get(i);
                if (info == null) {
                    continue;
                }

                String goods_code = info.getGoodsCode();
                if (goods_code.equals(text)) {
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
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        Intent closeActivity = new Intent(ACTION_ACTIVITY_CLOSE);
        closeActivity.setPackage(getPackageName());
        sendBroadcast(closeActivity);
    }

    /**
     * 리스트 뷰에 반출 목록 표시
     */
    private void showCarryOutList(JSONObject json) {
        if (json == null) {
            return ;
        }

        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        JSONArray json_array = (JSONArray) VUtil.getValue(json, NetworkParam.ARRAY_LIST);
        if (json_array == null || json_array.length() == 0) {
            String text = getString(R.string.not_exist_list);
            showPopup(text);
            return ;
        }

        String number = mEditNumber.getText().toString();

        if (mArray == null) {
            mArray = new ArrayList<>();
        }

        mArray.clear();

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

                Object obj3 = VUtil.getValue(json_obj, NetworkParam.CARRY_OUT_DETAIL_NUMBER);
                int detail_number = 0;
                if (obj3 != null && !obj3.toString().equals("null")) {
                    detail_number = Integer.parseInt(obj3.toString());
                }

                Object obj4 = VUtil.getValue(json_obj, NetworkParam.CARRY_OUT_ORDER_NUMBER);
                String order_number = "";
                if (obj4 != null && !obj4.toString().equals("null")) {
                    order_number = obj4.toString();
                }

                Object obj5 = VUtil.getValue(json_obj, NetworkParam.CARRY_OUT_LOC);
                String loc = "";
                if (obj5 != null && !obj5.toString().equals("null")) {
                    loc = obj5.toString();
                }

                Object obj17 = VUtil.getValue(json_obj, NetworkParam.PLT_ID);
                String plt_id = "";
                if (obj17 != null && !obj17.toString().equals("null")) {
                    plt_id = obj17.toString();
                }

                Object obj6 = VUtil.getValue(json_obj, NetworkParam.ORDER_COUNT);
                int order_count = 0;
                if (obj6 != null && !obj6.toString().equals("null")) {
                    order_count = Integer.parseInt(obj6.toString());
                }

                Object obj7 = VUtil.getValue(json_obj, NetworkParam.ORDER_COUNT_BOX);
                int box_count = 0;
                if (obj7 != null && !obj7.toString().equals("null")) {
                    box_count = Integer.parseInt(obj7.toString());
                }

                Object obj8 = VUtil.getValue(json_obj, NetworkParam.ORDER_COUNT_EA);
                int ea_count = 0;
                if (obj8 != null && !obj8.toString().equals("null")) {
                    ea_count = Integer.parseInt(obj8.toString());
                }

                Object obj9 = VUtil.getValue(json_obj, NetworkParam.MAKE_LOT);
                String make_lot = "";
                if (obj9 != null && !obj9.toString().equals("null")) {
                    make_lot = obj9.toString();
                }

                Object obj10 = VUtil.getValue(json_obj, NetworkParam.MAKE_DATE);
                String make_date = "";
                if (obj10 != null && !obj10.toString().equals("null")) {
                    make_date = obj10.toString();
                }

                Object obj11 = VUtil.getValue(json_obj, NetworkParam.EXPIRE_DATE);
                String end_date = "";
                if (obj11 != null && !obj11.toString().equals("null")) {
                    end_date = obj11.toString();
                }

                Object obj12 = VUtil.getValue(json_obj, NetworkParam.LOT_ATTR1);
                String lot1 = "";
                if (obj12 != null && !obj12.toString().equals("null")) {
                    lot1 = obj12.toString();
                }

                Object obj13 = VUtil.getValue(json_obj, NetworkParam.LOT_ATTR2);
                String lot2 = "";
                if (obj13 != null && !obj13.toString().equals("null")) {
                    lot2 = obj13.toString();
                }

                Object obj14 = VUtil.getValue(json_obj, NetworkParam.LOT_ATTR3);
                String lot3 = "";
                if (obj14 != null && !obj14.toString().equals("null")) {
                    lot3 = obj14.toString();
                }

                Object obj15 = VUtil.getValue(json_obj, NetworkParam.LOT_ATTR4);
                String lot4 = "";
                if (obj15 != null && !obj15.toString().equals("null")) {
                    lot4 = obj15.toString();
                }

                Object obj16 = VUtil.getValue(json_obj, NetworkParam.LOT_ATTR5);
                String lot5 = "";
                if (obj16 != null && !obj16.toString().equals("null")) {
                    lot5 = obj16.toString();
                }

                CarryOutInfo info = new CarryOutInfo();
                info.setGoodsCode(code);
                info.setGoodsName(name);
                info.setCarryOutNumber(number);
                info.setCarryOutDetailNumber(detail_number);
                info.setCarryOutOrderNumber(order_number);

                info.setLocation(loc);
                info.setPltId(plt_id);

                info.setOrderCount(order_count);
                info.setOrderBoxCount(box_count);
                info.setOrderEaCount(ea_count);

                info.setPickingBoxCount(box_count);
                info.setPickingEaCount(ea_count);

                info.setManufactureLot(make_lot);
                info.setMakeDate(make_date);
                info.setDistributionDate(end_date);

                info.setLot1(lot1);
                info.setLot2(lot2);
                info.setLot3(lot3);
                info.setLot4(lot4);
                info.setLot5(lot5);

                mArray.add(info);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            mArray.clear();
            return ;
        }

        mTextTotalCount.setText("총 " + String.valueOf(mArray.size()) + "건");
        mAdapter.notifyDataSetChanged();

        mEditCode.requestFocus();

        if (mArray != null && mArray.size() == 1) {
            moveDetailActivity(0);
        }
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
        i.setClass(mContext, CarryOutPickingActivity.class);
        i.putExtra(ScrnParam.INFO_DETAIL, mArray.get(position));
        i.putExtra(ScrnParam.INFO_POSITION, position);
        startActivityForResult(i, REQ_DETAIL);
    }

    /**
     * 조회 화면으로 이동
     */
    private void moveInquiryActivity() {
        Intent i = new Intent();
        i.setClass(mContext, CarryOutSearchActivity.class);
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
     * 반출 목록 요청
     */
    private void reqCarryOutList() {
        String number = mEditNumber.getText().toString();
        if (number == null || number.length() <= 0) {
            showPopup(getString(R.string.not_exist_carry_out_number));
            return ;
        }

        String code = mEditCode.getText().toString();
        if (code == null || code.length() <= 0) {
            code = "";
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        CarryOutInfo info = new CarryOutInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setUserId(user_id);

        info.setCarryOutNumber(number);
        info.setGoodsCode(code);

        showProgress(true, getString(R.string.progress_req_list));

        INetworkManager network = new NetworkManager(mContext, mCarryOutListNetworkListener);
        network.reqCarryOutList(info);
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
     * 반출 목록 요청 결과 수신 리스너
     */
    private INetworkListener mCarryOutListNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_CARRY_OUT_LIST, json);
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
                if (mEditNumber.isFocused()) {
                    mEditCode.requestFocus();
                    imm.hideSoftInputFromWindow(mEditNumber.getWindowToken(), 0);
                }
                else if (mEditCode.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditCode.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditNumber.isFocused()) {
                        mEditCode.requestFocus();
                        imm.hideSoftInputFromWindow(mEditNumber.getWindowToken(), 0);
                    }
                    else if (mEditCode.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditCode.getWindowToken(), 0);
                    }
                }
            }

            return false;
        }
    };

    /**
     * 리스트 항목 선택 리스너
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            moveDetailActivity(position);
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
            if (id == R.id.carry_out_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.carry_out_btn_inquiry) {
                moveInquiryActivity();
            }
            else if (id == R.id.carry_out_btn_search) {
                reqCarryOutList();
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

                case HANDLER_SHOW_CARRY_OUT_LIST :
                    showCarryOutList((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_SCAN_RESULT :
                    showScanResult((String) msg.obj);
                    break;
            }
        }
    };
}
