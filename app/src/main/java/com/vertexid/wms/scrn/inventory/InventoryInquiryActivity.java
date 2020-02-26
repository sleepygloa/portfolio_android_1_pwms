package com.vertexid.wms.scrn.inventory;

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
import com.vertexid.wms.scrn.inventory.adapter.InventoryInquiryItemAdapter;
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
 * 재고조사 화면
 */
public class InventoryInquiryActivity extends BaseActivity {
    private final int REQ_DETAIL = 100;
    private final int REQ_CREATE = REQ_DETAIL + 1;

    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_INVENTORY_LIST = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_INVENTORY_LIST + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;
    private TextView mTextCount = null;

    private EditText mEditOrderNumber;
    private EditText mEditLocation;
    private EditText mEditGoodsCode;

    private ProgressDialog mPrgDlg = null;

    private InventoryInquiryItemAdapter mAdapter = null;

    private ArrayList<InvenInquiryInfo> mArray = null;

    private ScannerManager mScan = null;
    private String mActualityCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_inquiry_activity);

        mContext = this;
        mScan = ScannerManager.getInst(mContext);

        mArray = new ArrayList<>();
        mArray.clear();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.inventory_inquiry_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.inventory_inquiry_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.inventory_inquiry_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        mEditOrderNumber = (EditText) findViewById(R.id.inventory_inquiry_edit_order_number);
        mEditLocation = (EditText) findViewById(R.id.inventory_inquiry_edit_location);
        mEditGoodsCode = (EditText) findViewById(R.id.inventory_inquiry_edit_goods_code);

        mEditOrderNumber.setOnEditorActionListener(mEditorActionListener);
        mEditLocation.setOnEditorActionListener(mEditorActionListener);
        mEditGoodsCode.setOnEditorActionListener(mEditorActionListener);

        mEditOrderNumber.requestFocus();

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.inventory_inquiry_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        Button btn_search = (Button) findViewById(R.id.inventory_inquiry_btn_search);
        btn_search.setOnClickListener(mClickListener);
        Button btn_create = (Button) findViewById(R.id.inventory_inquiry_btn_regi);
        btn_create.setOnClickListener(mClickListener);

        mTextCount = (TextView) findViewById(R.id.inventory_inquiry_text_total_count);
        mTextCount.setText("총 " + String.valueOf(mArray.size()) + "건");

        ListView list_view = (ListView) findViewById(R.id.inventory_inquiry_list);
        list_view.setOnItemClickListener(mItemClickListener);
        mAdapter = new InventoryInquiryItemAdapter(mContext, mArray);
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

            InvenInquiryInfo info = (InvenInquiryInfo) data.getSerializableExtra(ScrnParam.INFO_DETAIL);
            if (info == null) {
                return ;
            }

            int position = data.getIntExtra(ScrnParam.INFO_POSITION, 0);
            mArray.set(position, info);
            mAdapter.notifyDataSetChanged();

            reqInvenListInquiry();
        }
        else if (requestCode == REQ_CREATE) {
            if (resultCode != Activity.RESULT_OK) {
                return ;
            }

            if (data == null) {
                return ;
            }

            if (mArray == null) {
                mArray = new ArrayList<>();
                mArray.clear();
            }

            InvenInquiryInfo info = (InvenInquiryInfo) data.getSerializableExtra(ScrnParam.INFO_DETAIL);
            if (info == null) {
                return ;
            }

            mArray.add(info);

            mTextCount.setText("총 " + mArray.size() + "건");
            mAdapter.notifyDataSetChanged();
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
     * 사용자에게 알릴 내용을 팝업으로 보여줌
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
        if (mEditOrderNumber.isFocused()) {
            mEditOrderNumber.setText(scan);
            imm.hideSoftInputFromWindow(mEditOrderNumber.getWindowToken(), 0);

            reqInvenListInquiry();
        }
        else if (mEditLocation.isFocused()) {
            mEditLocation.setText(scan);
            imm.hideSoftInputFromWindow(mEditLocation.getWindowToken(), 0);

            if (mArray == null) {
                return;
            }

            String goods_code = mEditGoodsCode.getText().toString();
            if (goods_code == null || goods_code.length() == 0) {
                goods_code = "";
            }

            int position = 0;
            boolean is_exist = false;

            for (int i = 0 ; i < mArray.size() ; i++) {
                InvenInquiryInfo info = mArray.get(i);
                if (info == null) {
                    continue;
                }

                String location = info.getLocation();
                String code = info.getGoodsCode();
                if (location.equals(scan) && goods_code.equals(code)) {
                    position = i;
                    is_exist = true;
                    break;
                }
            }

            if (is_exist) {
                moveDetailActivity(position);
            }
            else if (goods_code == null || goods_code.length() == 0) {
                mEditGoodsCode.requestFocus();
            }
        }
        else if (mEditGoodsCode.isFocused()) {
            mEditGoodsCode.setText(scan);
            imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);

            if (mArray == null) {
                return;
            }

            String location = mEditLocation.getText().toString();
            if (location == null || location.length() == 0) {
                location = "";
            }

            int position = 0;
            boolean is_exist = false;

            for (int i = 0 ; i < mArray.size() ; i++) {
                InvenInquiryInfo info = mArray.get(i);
                if (info == null) {
                    continue;
                }

                String loc = info.getLocation();
                String goods_code = info.getGoodsCode();
                if (location.equals(loc) && goods_code.equals(scan)) {
                    position = i;
                    is_exist = true;
                    break;
                }
            }

            if (is_exist) {
                moveDetailActivity(position);
            }
            else if (location == null || location.length() == 0) {
                mEditLocation.requestFocus();
            }
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
     * 재고 목록을 보여준다.
     * @param json 서버로부터 받은 재고 목록
     */
    private void showInventoryList(JSONObject json) {
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

        Object obj = VUtil.getValue(json, NetworkParam.INVEN_ACTUALITY_CATEGORY);
        if (obj != null && !obj.toString().equals("null")) {
            mActualityCategory = obj.toString();
        }

        JSONArray json_array = (JSONArray) VUtil.getValue(json, NetworkParam.ARRAY_LIST);
        if (json_array == null || json_array.length() == 0) {
            String text = getString(R.string.net_error_invalid_payload);
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
                Object obj1 = VUtil.getValue(json_obj, NetworkParam.LOC_CD);
                String location = "";
                if (obj1 != null && !obj1.toString().equals("null")) {
                    location = obj1.toString();
                }

                Object obj2 = VUtil.getValue(json_obj, NetworkParam.GOODS_CODE);
                String code = "";
                if (obj2 != null && !obj2.toString().equals("null")) {
                    code = obj2.toString();
                }

                Object obj3 = VUtil.getValue(json_obj, NetworkParam.GOODS_NAME);
                String name = "";
                if (obj3 != null && !obj3.toString().equals("null")) {
                    name = obj3.toString();
                }

                Object obj4 = VUtil.getValue(json_obj, NetworkParam.PLT_ID);
                String plt_id = "";
                if (obj4 != null && !obj4.toString().equals("null")) {
                    plt_id = obj4.toString();
                }

                Object obj5 = VUtil.getValue(json_obj, NetworkParam.ACQUIRE);
                int acquire = 0;
                if (obj5 != null && !obj5.toString().equals("null")) {
                    acquire = Integer.parseInt(obj5.toString());
                }

                Object obj6 = VUtil.getValue(json_obj, NetworkParam.INVEN_COUNT);
                int inven_count = 0;
                if (obj6 != null && !obj6.toString().equals("null")) {
                    inven_count = Integer.parseInt(obj6.toString());
                }

                Object obj7 = VUtil.getValue(json_obj, NetworkParam.INVEN_MOVE_BOX_COUNT);
                int inven_box = 0;
                if (obj7 != null && !obj7.toString().equals("null")) {
                    inven_box = Integer.parseInt(obj7.toString());
                }

                Object obj8 = VUtil.getValue(json_obj, NetworkParam.INVEN_MOVE_EA_COUNT);
                int inven_ea = 0;
                if (obj8 != null && !obj8.toString().equals("null")) {
                    inven_ea = Integer.parseInt(obj8.toString());
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
                String lot_attr1 = "";
                if (obj12 != null && !obj12.toString().equals("null")) {
                    lot_attr1 = obj12.toString();
                }

                Object obj13 = VUtil.getValue(json_obj, NetworkParam.LOT_ATTR2);
                String lot_attr2 = "";
                if (obj13 != null && !obj13.toString().equals("null")) {
                    lot_attr2 = obj13.toString();
                }

                Object obj14 = VUtil.getValue(json_obj, NetworkParam.LOT_ATTR3);
                String lot_attr3 = "";
                if (obj14 != null && !obj14.toString().equals("null")) {
                    lot_attr3 = obj14.toString();
                }

                Object obj15 = VUtil.getValue(json_obj, NetworkParam.LOT_ATTR4);
                String lot_attr4 = "";
                if (obj15 != null && !obj15.toString().equals("null")) {
                    lot_attr4 = obj15.toString();
                }

                Object obj16 = VUtil.getValue(json_obj, NetworkParam.LOT_ATTR5);
                String lot_attr5 = "";
                if (obj16 != null && !obj16.toString().equals("null")) {
                    lot_attr5 = obj16.toString();
                }

                Object obj17 = VUtil.getValue(json_obj, NetworkParam.INVEN_ORDER_NUMBER);
                String order_number = "";
                if (obj17 != null && !obj17.toString().equals("null")) {
                    order_number = obj17.toString();
                }

                Object obj18 = VUtil.getValue(json_obj, NetworkParam.INVEN_ORDER_DETAIL_NUMBER);
                int detail_number = 0;
                if (obj18 != null && !obj16.toString().equals("null")) {
                    detail_number = Integer.parseInt(obj18.toString());
                }

                Object obj19 = VUtil.getValue(json_obj, NetworkParam.INVEN_ACTUALITY_CATEGORY);
                String category = "";
                if (obj19 != null && !obj19.toString().equals("null")) {
                    category = obj19.toString();
                }

                InvenInquiryInfo info = new InvenInquiryInfo();
                info.setLocation(location);
                info.setGoodsCode(code);
                info.setGoodsName(name);
                info.setPltId(plt_id);
                info.setActualityCategory(category);
                info.setAcquire(acquire);
                info.setInvenCount(inven_count);
                info.setInvenBoxCount(inven_box);
                info.setInvenEaCount(inven_ea);
                info.setActualityBoxCount(inven_box);
                info.setActualityEaCount(inven_ea);
                info.setManufactureLot(make_lot);
                info.setMakeDate(make_date);
                info.setDistributionDate(end_date);
                info.setLotAttribute1(lot_attr1);
                info.setLotAttribute2(lot_attr2);
                info.setLotAttribute3(lot_attr3);
                info.setLotAttribute4(lot_attr4);
                info.setLotAttribute5(lot_attr5);
                info.setOrderNumber(order_number);
                info.setOrderDetailNumber(detail_number);

                mArray.add(info);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        mTextCount.setText("총 " + String.valueOf(mArray.size()) + "건");
        mAdapter.notifyDataSetChanged();

        mEditLocation.requestFocus();

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

    private void moveDetailActivity(int position) {
        Intent i = new Intent();
        i.setClass(mContext, InventoryInquiryDetailActivity.class);
        i.putExtra(ScrnParam.INFO_DETAIL, mArray.get(position));
        i.putExtra(ScrnParam.INFO_POSITION, position);

        startActivityForResult(i, REQ_DETAIL);
    }

    private void moveCreateActivity() {
        Intent i = new Intent();
        i.setClass(mContext, InventoryCreateActivity.class);
        i.putExtra(ScrnParam.INVEN_ACTUALITY_CATEGORY, mActualityCategory);

        startActivityForResult(i, REQ_CREATE);
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
    private void reqInvenListInquiry() {
        String number = mEditOrderNumber.getText().toString();
        if (number == null || number.length() <= 0) {
            showPopup(getString(R.string.not_exist_order_number));
            return ;
        }

        String location = mEditLocation.getText().toString();
        if (location == null || location.length() <= 0) {
            location = "";
        }

        String goods_code = mEditGoodsCode.getText().toString();
        if (goods_code == null || goods_code.length() <= 0) {
            goods_code = "";
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();
        String user_id = config.getUserId();

        InvenInquiryInfo info = new InvenInquiryInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());
        info.setUserId(user_id);

        info.setOrderNumber(number);
        info.setGoodsCode(goods_code);
        info.setLocation(location);

        showProgress(true, getString(R.string.progress_req_list));

        INetworkManager network = new NetworkManager(mContext, mInvenInQuiryListNetworkListener);
        network.reqInvenInquiryList(info);
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
    private INetworkListener mInvenInQuiryListNetworkListener = new INetworkListener() {
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

            nextState(HANDLER_SHOW_INVENTORY_LIST, json);
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
                if (mEditOrderNumber.isFocused()) {
                    mEditLocation.requestFocus();
                    imm.hideSoftInputFromWindow(mEditOrderNumber.getWindowToken(), 0);
                }
                else if (mEditLocation.isFocused()) {
                    mEditGoodsCode.requestFocus();
                    imm.hideSoftInputFromWindow(mEditLocation.getWindowToken(), 0);
                }
                else if (mEditGoodsCode.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditOrderNumber.isFocused()) {
                        mEditLocation.requestFocus();
                        imm.hideSoftInputFromWindow(mEditOrderNumber.getWindowToken(), 0);
                    }
                    else if (mEditLocation.isFocused()) {
                        mEditGoodsCode.requestFocus();
                        imm.hideSoftInputFromWindow(mEditLocation.getWindowToken(), 0);
                    }
                    else if (mEditGoodsCode.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
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
            if (id == R.id.inventory_inquiry_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.inventory_inquiry_btn_search) {
                reqInvenListInquiry();
            }
            else if (id == R.id.inventory_inquiry_btn_regi) {
                moveCreateActivity();
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

                case HANDLER_SHOW_INVENTORY_LIST :
                    showInventoryList((JSONObject) msg.obj);
                    break;

                case HANDLER_SHOW_SCAN_RESULT :
                    showScanResult((String) msg.obj);
                    break;
            }
        }
    };
}
