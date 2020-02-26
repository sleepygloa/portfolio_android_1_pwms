package com.vertexid.wms.scrn.setting;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.network.INetworkListener;
import com.vertexid.wms.network.INetworkManager;
import com.vertexid.wms.network.NetworkManager;
import com.vertexid.wms.scrn.BaseActivity;
import com.vertexid.wms.scrn.carryin.CarryInActivity;
import com.vertexid.wms.scrn.carryout.CarryOutActivity;
import com.vertexid.wms.scrn.inventory.InventoryActivity;
import com.vertexid.wms.scrn.search.SearchActivity;
import com.vertexid.wms.scrn.takeout.TakeOutActivity;
import com.vertexid.wms.scrn.warehouse.WareHouseActivity;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 환경설정 화면
 */
public class SettingActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;

    private EditText mEditDistribution = null;
    private EditText mEditCustomer = null;
    private EditText mEditWorkingDate = null;

    private ProgressDialog mPrgDlg = null;

    private ArrayList<CodeInfo> mArrayCenter = null;
    private ArrayList<CodeInfo> mArrayCustomer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        mContext = this;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.setting_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.setting_navi_view);
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

        TextView text_user = (TextView) findViewById(R.id.setting_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        mEditDistribution = (EditText) findViewById(R.id.setting_edit_distribution);
        mEditDistribution.setText(center != null ? center.getText() : "");
        mEditCustomer = (EditText) findViewById(R.id.setting_edit_customer);
        mEditCustomer.setText(customer != null ? customer.getText() : "");

        mEditWorkingDate = (EditText) findViewById(R.id.setting_edit_working_date);
        mEditWorkingDate.setOnClickListener(mClickListener);
        mEditWorkingDate.setInputType(InputType.TYPE_NULL);
        mEditWorkingDate.requestFocus();

        FrameLayout btn_menu = (FrameLayout) findViewById(R.id.setting_layout_btn_menu);
        btn_menu.setOnClickListener(mClickListener);
        Button btn_center = (Button) findViewById(R.id.setting_btn_distribution);
        btn_center.setOnClickListener(mClickListener);
        Button btn_customer = (Button) findViewById(R.id.setting_btn_customer);
        btn_customer.setOnClickListener(mClickListener);
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
     * 물류센터 목록을 리스트 형식으로 표현
     */
    private void showDistributionList() {
        if (mArrayCenter == null) {
            mArrayCenter = new ArrayList<>();
        }

        mArrayCenter.clear();

        // DB에서 물류센터 목록을 가져온다.
        IConfigManager config = new ConfigManager(mContext);
        mArrayCenter = config.getCodeList(CodeInfo.CODE_TYPE_CENTER);

        String [] distribution = new String[mArrayCenter.size()];
        for (int i = 0 ; i < mArrayCenter.size() ; i++) {
            distribution[i] = mArrayCenter.get(i).getText();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("물류센터를 선택해주세요.");
        builder.setItems(distribution, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = mArrayCenter.get(which).getText();
                String code = mArrayCenter.get(which).getCode();

                CodeInfo info = new CodeInfo();
                info.setCode(code);
                info.setText(name);

                IConfigManager config = new ConfigManager(mContext);
                config.setCenterInfo(info);

                mEditDistribution.setText(name);
            }
        });

        builder.show();
    }

    /**
     * 고객사 목록을 리스트 형식으로 표현
     */
    private void showCustomerList() {
        if (mArrayCustomer == null) {
            mArrayCustomer = new ArrayList<>();
        }

        mArrayCustomer.clear();

        IConfigManager config = new ConfigManager(mContext);
        mArrayCustomer = config.getCodeList(CodeInfo.CODE_TYPE_CUSTOMER);

        String [] customer = new String[mArrayCustomer.size()];
        for (int i = 0 ; i < mArrayCustomer.size() ; i++) {
            customer[i] = mArrayCustomer.get(i).getText();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("고객사를 선택해주세요.");
        builder.setItems(customer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = mArrayCustomer.get(which).getText();
                String code = mArrayCustomer.get(which).getCode();

                CodeInfo info = new CodeInfo();
                info.setCode(code);
                info.setText(name);

                IConfigManager config = new ConfigManager(mContext);
                config.setCustomerInfo(info);

                mEditCustomer.setText(name);
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
     * 사용자에게 알릴 내용을 팝업으로 Display
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

        Intent closeActivity = new Intent(ACTION_ACTIVITY_CLOSE);
        closeActivity.setPackage(getPackageName());
        sendBroadcast(closeActivity);
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

            mEditWorkingDate.setText(format);
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

    /**
     * 버튼 클릭 이벤트 수신 리스너
     */
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if (id == R.id.setting_layout_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.setting_btn_distribution) {
                showDistributionList();
            }
            else if (id == R.id.setting_btn_customer) {
                showCustomerList();
            }
            else if (id == R.id.setting_edit_working_date) {
                showCalendar();
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
            }
        }
    };
}
