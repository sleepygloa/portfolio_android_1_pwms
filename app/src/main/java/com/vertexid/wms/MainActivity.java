package com.vertexid.wms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vertexid.wms.barcode.ScannerManager;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.network.INetworkListener;
import com.vertexid.wms.network.INetworkManager;
import com.vertexid.wms.network.NetworkManager;
import com.vertexid.wms.scrn.BaseActivity;
import com.vertexid.wms.scrn.ScrnParam;
import com.vertexid.wms.scrn.carryin.CarryInActivity;
import com.vertexid.wms.scrn.carryout.CarryOutActivity;
import com.vertexid.wms.scrn.inventory.InventoryActivity;
import com.vertexid.wms.scrn.search.SearchActivity;
import com.vertexid.wms.scrn.setting.SettingActivity;
import com.vertexid.wms.scrn.takeout.TakeOutActivity;
import com.vertexid.wms.scrn.warehouse.WareHouseActivity;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONObject;

import java.io.File;

/**
 * 사용자에게 보여질 주 화면
 */
public class MainActivity extends BaseActivity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGOUT_RESULT = HANDLER_SHOW_NET_ERROR + 1;
    private final int HANDLER_SHOW_APK_DOWNLOAD_RESULT = HANDLER_SHOW_LOGOUT_RESULT + 1;

    private Context mContext = null;
    private DrawerLayout mDrawerLayout = null;
    private ProgressDialog mPrgDlg = null;
    private ProgressDialog mPrg = null;

    private ScannerManager mScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mContext = this;

        mScan = ScannerManager.getInst(mContext);
        mScan.init();

        IConfigManager config = new ConfigManager(mContext);
        String user_id = config.getUserId();
        CodeInfo center = config.getCenterInfo();
        CodeInfo customer = config.getCustomerInfo();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        NavigationView navi_view = (NavigationView) findViewById(R.id.main_navi_view);
        navi_view.setNavigationItemSelectedListener(mNavigationListener);
        View nav_header_view = navi_view.getHeaderView(0);

        TextView text_user = (TextView) findViewById(R.id.main_text_user);
        text_user.setText(user_id + "(" + center.getText() + "/" + customer.getText() + ")");

        TextView text_menu_user = (TextView) nav_header_view.findViewById(R.id.menu_text_user);
        TextView text_menu_center = (TextView) nav_header_view.findViewById(R.id.menu_text_center);
        TextView text_menu_customer = (TextView) nav_header_view.findViewById(R.id.menu_text_customer);
        text_menu_user.setText(user_id);
        text_menu_center.setText(center.getText());
        text_menu_customer.setText(customer.getText());

        Button btn_warehouse = (Button) findViewById(R.id.main_btn_warehouse);
        Button btn_takeout = (Button) findViewById(R.id.main_btn_takeout);
        Button btn_carry_in = (Button) findViewById(R.id.main_btn_carry_in);
        Button btn_carry_out = (Button) findViewById(R.id.main_btn_carry_out);
        Button btn_inventory = (Button) findViewById(R.id.main_btn_inventory);
        Button btn_search = (Button) findViewById(R.id.main_btn_search);
        Button btn_setting = (Button) findViewById(R.id.main_btn_setting);

        btn_warehouse.setOnClickListener(mClickListener);
        btn_takeout.setOnClickListener(mClickListener);
        btn_carry_in.setOnClickListener(mClickListener);
        btn_carry_out.setOnClickListener(mClickListener);
        btn_inventory.setOnClickListener(mClickListener);
        btn_search.setOnClickListener(mClickListener);
        btn_setting.setOnClickListener(mClickListener);

        FrameLayout frameLayout_menu = (FrameLayout) findViewById(R.id.main_frame_btn_menu);
        frameLayout_menu.setOnClickListener(mClickListener);

        Intent intent = getIntent();
        if (intent != null) {
            String version = intent.getStringExtra(ScrnParam.APK_SVR_VERSION);
            checkVersion(version);
        }
    }

    @Override
    protected void closeActivity() {
        if (mScan != null) {
            mScan.release();
            mScan = null;
        }

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

        mScan.setListener(null);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(text);
        builder.setPositiveButton("확인", null);
        builder.show();
    }

    private void showProgress(boolean is_show) {
        if (is_show) {
            if (mPrg != null) {
                if (mPrg.isShowing()) {
                    mPrg.dismiss();
                }

                mPrg = null;
            }

            mPrg = new ProgressDialog(mContext);
            mPrg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mPrg.setMessage(getString(R.string.progress_update));
            mPrg.setCanceledOnTouchOutside(false);
            mPrg.setCancelable(false);
            mPrg.show();
        }
        else {
            if (mPrg != null) {
                if (mPrg.isShowing()) {
                    mPrg.dismiss();
                }

                mPrg = null;
            }
        }



//        progressDialog = new ProgressDialog(context);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setMessage("업데이트 중입니다...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
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
            return ;
        }

        Intent closeActivity = new Intent(ACTION_ACTIVITY_CLOSE);
        closeActivity.setPackage(getPackageName());
        sendBroadcast(closeActivity);
    }

    /**
     * 로그아웃 요청
     */
    private void reqLogOut() {
        IConfigManager config = new ConfigManager(mContext);
        String user_id = config.getUserId();

        INetworkManager network = new NetworkManager(mContext, mLogOutNetworkListener);
        network.reqLogOut(user_id);
    }

    /**
     * 버전 검사
     * @param svr_version 서버로부터 수신된 최신 버전
     */
    private void checkVersion(String svr_version) {
        if (svr_version == null || svr_version.length() <= 0) {
            return ;
        }

        String current_version = VUtil.getPackageVersion(mContext);
        if (current_version == null || current_version.length() <= 0) {
            return ;
        }

        String svr_replace = svr_version.replaceAll(".", "");
        String current_replace = current_version.replaceAll(".", "");

        int svr_value = 0;
        try {
            svr_value = Integer.parseInt(svr_replace);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        int current_value = 0;
        try {
            current_value = Integer.parseInt(current_replace);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // 버전이 같지 않을 때 업그레이드 진행
        if (svr_value != current_value) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.notify_update));
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String path = Environment.getExternalStorageDirectory()  + File.separator + "wms";
                    makeFile(path);

                    showProgress(true);

                    INetworkManager network = new NetworkManager(mContext, mDownLoadNetworkListener);
                    network.reqApkDownLoad(path);
                }
            });
            builder.show();
        }
    }

    private void makeFile(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

//        if (dir.isDirectory()) {
//            File file = new File(path + "/" + file_name);
//            if (file != null && !file.exists()) {
//                try {
//                    file.createNewFile();
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    /**
     * 새로운 apk 설치
     */
    public void installNewApk() {
        File apkfile = new File(Environment.getExternalStorageDirectory() + File.separator + "wms" + File.separator + "wms.apk");
        Uri apkUri = Uri.fromFile(apkfile);

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(apkUri, "application/vnd.android.package-archive");
        mContext.startActivity(i);
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
     */
    private void nextState(int state) {
        Message msg = new Message();
        msg.what = state;

        mHandler.sendMessage(msg);
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
     * 서버와의 통신 결과를 수신하는 리스너
     */
    private INetworkListener mDownLoadNetworkListener = new INetworkListener() {
        @Override
        public void onRespResult(int error, String payload) {
        }

        @Override
        public void onProgress(int progress) {
//            showProgress(progress);
            if (mPrg != null) {
                mPrg.setProgress(progress);
            }
        }

        @Override
        public void onResult(int error) {
            showProgress(false);

            if (error != VErrors.E_NONE) {
                return ;
            }

            nextState(HANDLER_SHOW_APK_DOWNLOAD_RESULT);
        }
    };

    /**
     * 로그아웃 요청 통신 결과를 수신하는 리스너
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

    /**
     * 타이틀 좌측 상단 메뉴 클릭 리스너
     */
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

            if (id == R.id.main_frame_btn_menu) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else if (id == R.id.main_btn_warehouse) {
                moveNextActivity(WareHouseActivity.class);
            }
            else if (id == R.id.main_btn_takeout) {
                moveNextActivity(TakeOutActivity.class);
            }
            else if (id == R.id.main_btn_carry_in) {
                moveNextActivity(CarryInActivity.class);
            }
            else if (id == R.id.main_btn_carry_out) {
                moveNextActivity(CarryOutActivity.class);
            }
            else if (id == R.id.main_btn_inventory) {
                moveNextActivity(InventoryActivity.class);
            }
            else if (id == R.id.main_btn_search) {
                moveNextActivity(SearchActivity.class);
            }
            else if (id == R.id.main_btn_setting) {
                moveNextActivity(SettingActivity.class);
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

                case HANDLER_SHOW_APK_DOWNLOAD_RESULT :
                    installNewApk();
                    break;
            }
        }
    };
}
