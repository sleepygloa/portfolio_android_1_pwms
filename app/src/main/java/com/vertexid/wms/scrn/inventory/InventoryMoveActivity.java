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
import com.vertexid.wms.info.InvenMoveInfo;
import com.vertexid.wms.network.INetworkListener;
import com.vertexid.wms.network.INetworkManager;
import com.vertexid.wms.network.NError;
import com.vertexid.wms.network.NetworkManager;
import com.vertexid.wms.network.NetworkParam;
import com.vertexid.wms.scrn.BaseActivity;
import com.vertexid.wms.scrn.ScrnParam;
import com.vertexid.wms.scrn.carryin.CarryInActivity;
import com.vertexid.wms.scrn.carryout.CarryOutActivity;
import com.vertexid.wms.scrn.inventory.adapter.InventoryMoveItemAdapter;
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
 * 재고이동 화면
 */
public class InventoryMoveActivity extends BaseActivity {
    private final int REQ_DETAIL = 100;

    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_INVENTORY_LIST = HANDLER_SHOW_LOGOUT_RESULT + 1;
    private final int HANDLER_SHOW_SCAN_RESULT = HANDLER_SHOW_INVENTORY_LIST + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;
    private TextView mTextTotalCount = null;

    private EditText mEditOrderNumber = null;
    private EditText mEditGoodsCode = null;

    private ProgressDialog mPrgDlg = null;

    private InventoryMoveItemAdapter mAdapter = null;
    private ArrayList<InvenMoveInfo> mArray = null;

    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_move_activity);

        mContext = this;

        mScan = ScannerManager.getInst(mContext);

        mArray = new ArrayList<>();
        mArray.clear();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.inventory_move_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.inventory_move_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.inventory_move_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        mEditOrderNumber = (EditText) findViewById(R.id.inventory_move_edit_order_number);
        mEditGoodsCode = (EditText) findViewById(R.id.inventory_move_edit_goods_code);

        mEditOrderNumber.setOnEditorActionListener(mEditorActionListener);
        mEditGoodsCode.setOnEditorActionListener(mEditorActionListener);

        mEditOrderNumber.requestFocus();

        mTextTotalCount = (TextView) findViewById(R.id.inventory_move_text_total_count);
        mTextTotalCount.setText("총 " + mArray.size() + "건");

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.inventory_move_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        Button btn_inquiry = (Button) findViewById(R.id.inventory_move_btn_inquiry);
        btn_inquiry.setOnClickListener(mClickListener);

        mAdapter = new InventoryMoveItemAdapter(mContext, mArray);
        ListView list_view = (ListView) findViewById(R.id.inventory_move_list);
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

            InvenMoveInfo info = (InvenMoveInfo) data.getSerializableExtra(ScrnParam.INFO_DETAIL);
            if (info == null) {
                return ;
            }

            int position = data.getIntExtra(ScrnParam.INFO_POSITION, 0);
            mArray.set(position, info);
            mAdapter.notifyDataSetChanged();

            reqInventoryMoveList();
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
     * 로그아웃 결과 처리
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
     * 재고목록 요청 결과 처리
     * @param json 재고목록 요청 결과
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

        JSONArray json_array = (JSONArray) VUtil.getValue(json, NetworkParam.ARRAY_LIST);
        if (json_array == null || json_array.length() == 0) {
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
                JSONObject json_object = json_array.getJSONObject(i);
                if (json_object == null || json_object.length() == 0) {
                    continue;
                }

                // 대상 로케이션
                Object obj1 = VUtil.getValue(json_object, NetworkParam.FROM_LOCATION);
                String from_location = "";
                if (obj1 != null && !obj1.toString().equals("null")) {
                    from_location = obj1.toString();
                }

                // 이동 로케이션
                Object obj2 = VUtil.getValue(json_object, NetworkParam.TO_LOCATION);
                String to_location = "";
                if (obj2 != null && !obj2.toString().equals("null")) {
                    to_location = obj2.toString();
                }

                // 제품코드
                Object obj3 = VUtil.getValue(json_object, NetworkParam.GOODS_CODE);
                String goods_code = "";
                if (obj3 != null && !obj3.toString().equals("null")) {
                    goods_code = obj3.toString();
                }

                // 제품명
                Object obj4 = VUtil.getValue(json_object, NetworkParam.GOODS_NAME);
                String goods_name = "";
                if (obj4 != null && !obj4.toString().equals("null")) {
                    goods_name = obj4.toString();
                }

                // 지시수량
                Object obj5 = VUtil.getValue(json_object, NetworkParam.ORDER_COUNT);
                int order_count = 0;
                if (obj5 != null && !obj5.toString().equals("null")) {
                    order_count = Integer.parseInt(obj5.toString());
                }

                // 지시수량 - BOX
                Object obj6 = VUtil.getValue(json_object, NetworkParam.ORDER_COUNT_BOX);
                int order_box = 0;
                if (obj6 != null && !obj6.toString().equals("null")) {
                    order_box = Integer.parseInt(obj6.toString());
                }

                // 지시수량 - EA
                Object obj7 = VUtil.getValue(json_object, NetworkParam.ORDER_COUNT_EA);
                int order_ea = 0;
                if (obj7 != null && !obj7.toString().equals("null")) {
                    order_ea = Integer.parseInt(obj7.toString());
                }

                // 대상 PLT ID
                Object obj8 = VUtil.getValue(json_object, NetworkParam.FROM_PLT_ID);
                String from_plt_id = "";
                if (obj8 != null && !obj8.toString().equals("null")) {
                    from_plt_id = obj8.toString();
                }

                // 이동 PLT ID
                Object obj9 = VUtil.getValue(json_object, NetworkParam.TO_PLT_ID);
                String to_plt_id = "";
                if (obj9 != null && !obj9.toString().equals("null")) {
                    to_plt_id = obj9.toString();
                }

                // 입수
                Object obj10 = VUtil.getValue(json_object, NetworkParam.ACQUIRE);
                int acquire = 0;
                if (obj10 != null && !obj10.toString().equals("null")) {
                    acquire = Integer.parseInt(obj10.toString());
                }

                Object obj11 = VUtil.getValue(json_object, NetworkParam.MOVE_DETAIL_NUMBER);
                String detail_number = "";
                if (obj11 != null && !obj11.toString().equals("null")) {
                    detail_number = obj11.toString();
                }

                InvenMoveInfo info = new InvenMoveInfo();
                info.setFromLocation(from_location);
                info.setGoodsCode(goods_code);
                info.setGoodsName(goods_name);
                info.setOrderCount(order_count);
                info.setOrderBoxCount(order_box);
                info.setOrderEaCount(order_ea);
                info.setToLocation(to_location);
                info.setFromPltId(from_plt_id);
                info.setToPltId(to_plt_id);
                info.setMoveBoxCount(order_box);
                info.setMoveEaCount(order_ea);
                info.setAcquireCount(acquire);
                info.setOrderDetailNumber(detail_number);

                EditText edit_number = (EditText) findViewById(R.id.inventory_move_edit_order_number);
                String order_number = edit_number.getText().toString();
                info.setOrderNumber(order_number);

                mArray.add(info);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        mTextTotalCount.setText("총 " + mArray.size() + "건");
        mAdapter.notifyDataSetChanged();

        mEditGoodsCode.requestFocus();

        if (mArray != null && mArray.size() == 1) {
            moveDetailActivity(0);
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

            reqInventoryMoveList();
        }
        else if (mEditGoodsCode.isFocused()) {
            mEditGoodsCode.setText(scan);
            imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);

            if (mArray == null) {
                return ;
            }

            int position = 0;
            boolean is_exist = false;

            for (int i = 0 ; i < mArray.size() ; i++) {
                InvenMoveInfo info = mArray.get(i);
                if (info == null) {
                    continue;
                }

                String goods_code = info.getGoodsCode();
                if (goods_code.equals(scan)) {
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
     * @param position 리스트에서 선택한 항목의 인덱스
     */
    private void moveDetailActivity(int position) {
        Intent i = new Intent();
        i.setClass(mContext, InventoryMoveDetailActivity.class);
        i.putExtra(ScrnParam.INFO_DETAIL, mArray.get(position));
        i.putExtra(ScrnParam.INFO_POSITION, position);

        startActivityForResult(i, REQ_DETAIL);
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
     * 재고이동 목록 요청
     */
    private void reqInventoryMoveList() {
        String number = mEditOrderNumber.getText().toString();
        if (number == null || number.length() <= 0) {
            String text = getString(R.string.not_exist_order_number);
            showPopup(text);
            return ;
        }

//        String code = mEditGoodsCode.getText().toString();
//        if (code == null || code.length() <= 0) {
//            String text = getString(R.string.not_exist_goods_code);
//            showPopup(text);
//            return ;
//        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        InvenMoveInfo info = new InvenMoveInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());

        info.setOrderNumber(number);
//        info.setGoodsCode(code);

        showProgress(true, getString(R.string.progress_req_list));

        INetworkManager network = new NetworkManager(mContext, mInventoryListNetworkListener);
        network.reqInvenMoveList(info);
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
     * 재고이동 목록 요청 결과 수신 리스너
     */
    private INetworkListener mInventoryListNetworkListener = new INetworkListener() {
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

    /**
     * 리스트 항목 선택 리스너
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            moveDetailActivity(position);
        }
    };

    private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mEditOrderNumber.isFocused()) {
                    mEditGoodsCode.requestFocus();
                }
                else if (mEditGoodsCode.isFocused()) {
                    imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
                }
            }
            else if (event != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mEditOrderNumber.isFocused()) {
                        mEditGoodsCode.requestFocus();
                    }
                    else if (mEditGoodsCode.isFocused()) {
                        imm.hideSoftInputFromWindow(mEditGoodsCode.getWindowToken(), 0);
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
            if (id == R.id.inventory_move_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.inventory_move_btn_inquiry) {
                reqInventoryMoveList();
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
