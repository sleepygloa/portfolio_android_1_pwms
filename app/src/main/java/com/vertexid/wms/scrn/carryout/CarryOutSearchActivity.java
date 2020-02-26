package com.vertexid.wms.scrn.carryout;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CarryNumberInfo;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.network.INetworkListener;
import com.vertexid.wms.network.INetworkManager;
import com.vertexid.wms.network.NError;
import com.vertexid.wms.network.NetworkManager;
import com.vertexid.wms.network.NetworkParam;
import com.vertexid.wms.scrn.BaseActivity;
import com.vertexid.wms.scrn.ScrnParam;
import com.vertexid.wms.scrn.carryin.CarryInActivity;
import com.vertexid.wms.scrn.carryout.adapter.CarryOutSearchItemAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 반출번호 조회 화면
 */
public class CarryOutSearchActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_CARRY_OUT_LIST = HANDLER_SHOW_LOGOUT_RESULT + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;
    private TextView mTextTotalCount = null;

    private EditText mEditDate = null;
    private EditText mEditCategory = null;

    private ProgressDialog mPrgDlg = null;

    private CarryOutSearchItemAdapter mAdapter = null;

    private ArrayList<CarryNumberInfo> mArray = null;
    private ArrayList<CodeInfo> mArrayCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carry_out_search_activity);

        mContext = this;

        mArray = new ArrayList<>();
        mArray.clear();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.carry_out_search_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.carry_out_search_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.carry_out_search_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        mEditCategory = (EditText) findViewById(R.id.carry_out_search_edit_category);

        mEditDate = (EditText) findViewById(R.id.carry_out_search_edit_date);
        mEditDate.setOnClickListener(mClickListener);
        mEditDate.setInputType(InputType.TYPE_NULL);
        mEditDate.requestFocus();

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.carry_out_search_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
//        Button btn_date = (Button) findViewById(R.id.carry_out_search_btn_inquiry);
//        btn_date.setOnClickListener(mClickListener);
        Button btn_category = (Button) findViewById(R.id.carry_out_search_btn_category_inquiry);
        btn_category.setOnClickListener(mClickListener);
        Button btn_search = (Button) findViewById(R.id.carry_out_search_btn_search);
        btn_search.setOnClickListener(mClickListener);

        mTextTotalCount = (TextView) findViewById(R.id.carry_out_search_text_total_count);
        mTextTotalCount.setText("총 " + String.valueOf(mArray.size()) + "건");

        mAdapter = new CarryOutSearchItemAdapter(mContext, mArray);
        ListView list_view = (ListView) findViewById(R.id.carry_out_search_list);
        list_view.setOnItemClickListener(mItemClickListener);
        list_view.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar date = Calendar.getInstance();
        date.set(year, month, day_of_month);

        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String format = date_format.format(date.getTime());

        mEditDate.setText(format);

        mArrayCategory = config.getCodeList(CodeInfo.CODE_TYPE_CARRY_OUT);
        if (mArrayCategory == null) {
            return ;
        }

        mEditCategory.setText(mArrayCategory.get(0).getText());

        reqCarryOutNumber();
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
     * 달력 Display
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
     * 입고 구분 Display
     */
    private void showCategory() {
        if (mArrayCategory == null) {
            mArrayCategory = new ArrayList<>();
        }

        mArrayCategory.clear();

        IConfigManager config = new ConfigManager(mContext);
        mArrayCategory = config.getCodeList(CodeInfo.CODE_TYPE_CARRY_OUT);

        String [] category = new String[mArrayCategory.size()];
        for (int i = 0 ; i < mArrayCategory.size() ; i++) {
            category[i] = mArrayCategory.get(i).getText();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("반출구분을 선택해주세요.");
        builder.setItems(category, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = mArrayCategory.get(which).getText();
                mEditCategory.setText(name);
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
     * 반입 번호 목록 요청 결과 처리
     * @param json 반입 번호 목록 요청 결과
     */
    private void showCarryNumberList(JSONObject json) {
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

        if (mArray == null) {
            mArray = new ArrayList<>();
        }

        mArray.clear();

        try {
            for (int i = 0 ; i < json_array.length() ; i++) {
                JSONObject json_obj = json_array.getJSONObject(i);
                String number = "";
                Object obj1 = VUtil.getValue(json_obj, NetworkParam.CARRY_OUT_NUMBER);
                if (obj1 != null && !obj1.toString().equals("null")) {
                    number = (String) obj1;
                }

                String supplier = "";
                Object obj2 = VUtil.getValue(json_obj, NetworkParam.SUPPLIER_NAME);
                if (obj2 != null && !obj2.toString().equals("null")) {
                    supplier = (String) obj2;
                }

                CarryNumberInfo info = new CarryNumberInfo();
                info.setCarryNumber(number);
                info.setSource(supplier);

                mArray.add(info);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        mTextTotalCount.setText("총 " + mArray.size() + "건");
        mAdapter.notifyDataSetChanged();
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
    private void moveNextActivity(int position) {
        if (mArray == null || mArray.size() <= 0) {
            return ;
        }

        CarryNumberInfo info = mArray.get(position);
        if (info == null) {
            return ;
        }

        Intent i = new Intent();
        i.setClass(mContext, CarryOutActivity.class);
        i.putExtra(ScrnParam.NUMBER, info.getCarryNumber());
        i.putExtra(ScrnParam.SUPPLIER, info.getSource());

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
     * 반출번호 요청
     */
    private void reqCarryOutNumber() {
        String date = mEditDate.getText().toString();
        if (date == null || date.length() == 0) {
            return ;
        }

        String code = "";
        String category = mEditCategory.getText().toString();
        if (category == null || category.length() == 0) {
            return ;
        }
        else {
            for (int i = 0 ; i < mArrayCategory.size() ; i++) {
                CodeInfo info = mArrayCategory.get(i);
                String text = info.getText();
                if (text.equals(category)) {
                    code = info.getCode();
                    break;
                }
            }
        }

        IConfigManager config = new ConfigManager(mContext);
        CodeInfo company = config.getCompanyInfo();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        CarryNumberInfo info = new CarryNumberInfo();
        info.setCompanyCode(company.getCode());
        info.setCenterCode(center.getCode());
        info.setCustomerCode(customer.getCode());

        info.setCarryDate(date.replaceAll("-", ""));
        info.setCarryCategory(code);

        showProgress(true, getString(R.string.progress_req_carry_out_number));
        INetworkManager network = new NetworkManager(mContext, mCarryOutNumberInquiryNetworkListener);
        network.reqCarryOutNumberList(info);
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
     * 로그아웃 요청 결과 수신 리스너
     */
    private INetworkListener mCarryOutNumberInquiryNetworkListener = new INetworkListener() {
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
     * 리스트 항목 선택 리스너
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            moveNextActivity(position);
        }
    };

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar date = Calendar.getInstance();
            date.set(year, monthOfYear, dayOfMonth);

            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            String format = date_format.format(date.getTime());

            EditText edit_date = (EditText) findViewById(R.id.carry_out_search_edit_date);
            edit_date.setText(format);
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
            if (id == R.id.carry_out_search_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.carry_out_search_edit_date) {
                showCalendar();
            }
            else if (id == R.id.carry_out_search_btn_category_inquiry) {
                showCategory();
            }
            else if (id == R.id.carry_out_search_btn_search) {
                reqCarryOutNumber();
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
                    showCarryNumberList((JSONObject) msg.obj);
                    break;
            }
        }
    };
}
