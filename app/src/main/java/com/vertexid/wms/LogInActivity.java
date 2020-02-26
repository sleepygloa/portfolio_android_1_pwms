package com.vertexid.wms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.network.INetworkListener;
import com.vertexid.wms.network.INetworkManager;
import com.vertexid.wms.network.NError;
import com.vertexid.wms.network.NetworkManager;
import com.vertexid.wms.network.NetworkParam;
import com.vertexid.wms.scrn.ScrnParam;
import com.vertexid.wms.utils.VErrors;
import com.vertexid.wms.utils.VLog;
import com.vertexid.wms.utils.VUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 사용자 인증 화면
 */
public class LogInActivity extends Activity {
    private final int HANDLER_NONE = 0;
    private final int HANDLER_SHOW_NET_ERROR = HANDLER_NONE + 1;
    private final int HANDLER_SHOW_LOGIN_RESULT = HANDLER_SHOW_NET_ERROR + 1;

    private Context mContext = null;
    private ProgressDialog mPrgDlg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mContext = this;

        Button btn_login = (Button) findViewById(R.id.login_btn_certify);
        btn_login.setOnClickListener(mClickListener);
    }

    /**
     * 사용자에게 팝업으로 알림
     * @param text 사용자에게 보여줄 내용
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
     * 로그인 결과를 사용자에게 보여준다.
     * @param json 서버로부터 수신된 로그인 결과
     */
    private void showLogInResult(JSONObject json) {
        if (json == null) {
            String text = getString(R.string.net_error_invalid_payload);
            showPopup(text);
            return ;
        }

        // 오류코드 값 검사
        int error = (int) VUtil.getValue(json, NetworkParam.RESULT_CODE);
        if (error != NError.SUCCESS) {
            String text = (String) VUtil.getValue(json, NetworkParam.RESULT_MSG);
            showPopup(text);
            return ;
        }

        // 사용자가 소속된 회사정보
        String company_code = (String) VUtil.getValue(json, NetworkParam.COMPANY_CODE);
//        String company_name = (String) VUtil.getValue(json, NetworkParam.COMPANY_NAME);
        CodeInfo company = new CodeInfo();
        company.setCodeInfoType(CodeInfo.CODE_TYPE_COMPANY);
        company.setCode(company_code);
//        company.setText(company_name);

        // 사용자가 소속된 물류센터 정보
        String center_code = (String) VUtil.getValue(json, NetworkParam.CENTER_CODE_PRIOORD);
        String center_name = (String) VUtil.getValue(json, NetworkParam.CENTER_NAME_PRIOORD);
        CodeInfo center = new CodeInfo();
        center.setCodeInfoType(CodeInfo.CODE_TYPE_CENTER);
        center.setCode(center_code);
        center.setText(center_name);

        // 사용자의 고객사 정보
        String customer_code = (String) VUtil.getValue(json, NetworkParam.CUSTOMER_CODE_PRIOORD);
        String customer_name = (String) VUtil.getValue(json, NetworkParam.CUSTOMER_NAME_PRIOORD);
        CodeInfo customer = new CodeInfo();
        customer.setCodeInfoType(CodeInfo.CODE_TYPE_CUSTOMER);
        customer.setCode(customer_code);
        customer.setText(customer_name);

        // 고객사 목록
        ArrayList<CodeInfo> array_customer = new ArrayList<>();
        array_customer.clear();
        JSONArray json_array_customer = (JSONArray) VUtil.getValue(json, NetworkParam.CUSTOMER_LIST);
        if (json_array_customer != null) {
            try {
                for (int i = 0 ; i < json_array_customer.length() ; i++) {
                    JSONObject json_obj = json_array_customer.getJSONObject(i);
                    if (json_obj == null) {
                        continue;
                    }

                    String code = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_CODE);
                    String name = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_NAME);

                    CodeInfo info = new CodeInfo();
                    info.setCodeInfoType(CodeInfo.CODE_TYPE_CUSTOMER);
                    info.setCode(code);
                    info.setText(name);

                    array_customer.add(info);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 물류센터 목록
        ArrayList<CodeInfo> array_center = new ArrayList<>();
        array_center.clear();
        JSONArray json_array_center = (JSONArray) VUtil.getValue(json, NetworkParam.CENTER_LIST);
        if (json_array_center != null) {
            try {
                for (int i = 0 ; i < json_array_center.length() ; i++) {
                    JSONObject json_obj = json_array_center.getJSONObject(i);
                    if (json_obj == null) {
                        continue;
                    }

                    String code = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_CODE);
                    String name = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_NAME);

                    CodeInfo info = new CodeInfo();
                    info.setCodeInfoType(CodeInfo.CODE_TYPE_CENTER);
                    info.setCode(code);
                    info.setText(name);

                    array_center.add(info);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 제품상태 목록
        ArrayList<CodeInfo> array_goods_state = new ArrayList<>();
        array_goods_state.clear();
        JSONArray json_array_goods_state = (JSONArray) VUtil.getValue(json, NetworkParam.GOODS_STATE_LIST);
        if (json_array_goods_state != null) {
            try {
                for (int i = 0 ; i < json_array_goods_state.length() ; i++) {
                    JSONObject json_obj = json_array_goods_state.getJSONObject(i);
                    if (json_obj == null) {
                        continue;
                    }

                    String code = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_CODE);
                    String name = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_NAME);

                    CodeInfo info = new CodeInfo();
                    info.setCodeInfoType(CodeInfo.CODE_TYPE_GOODS_STATE);
                    info.setCode(code);
                    info.setText(name);

                    array_goods_state.add(info);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 미출고 사유 목록
        ArrayList<CodeInfo> array_not_take_out_reason = new ArrayList<>();
        array_not_take_out_reason.clear();
        JSONArray json_array_not_take_out_reason = (JSONArray) VUtil.getValue(json, NetworkParam.NOT_TAKE_OUT_LIST);
        if (json_array_not_take_out_reason != null) {
            try {
                for (int i = 0 ; i < json_array_not_take_out_reason.length() ; i++) {
                    JSONObject json_obj = json_array_not_take_out_reason.getJSONObject(i);
                    if (json_obj == null) {
                        continue;
                    }

                    String code = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_CODE);
                    String name = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_NAME);

                    CodeInfo info = new CodeInfo();
                    info.setCodeInfoType(CodeInfo.CODE_TYPE_NOT_TAKE_OUT_REASON);
                    info.setCode(code);
                    info.setText(name);

                    array_not_take_out_reason.add(info);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 입고구분
        ArrayList<CodeInfo> array_ware_category = new ArrayList<>();
        array_ware_category.clear();
        JSONArray json_array_ware_category = (JSONArray) VUtil.getValue(json, NetworkParam.WARE_CATEGORY);
        if (json_array_ware_category != null) {
            try {
                for (int i = 0 ; i < json_array_ware_category.length() ; i++) {
                    JSONObject json_obj = json_array_ware_category.getJSONObject(i);
                    if (json_obj == null) {
                        continue;
                    }

                    String code = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_CODE);
                    String name = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_NAME);

                    CodeInfo info = new CodeInfo();
                    info.setCodeInfoType(CodeInfo.CODE_TYPE_WAREHOUSE);
                    info.setCode(code);
                    info.setText(name);

                    array_ware_category.add(info);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 출고구분 목록
        ArrayList<CodeInfo> array_out_category = new ArrayList<>();
        array_out_category.clear();
        JSONArray json_array_out_category = (JSONArray) VUtil.getValue(json, NetworkParam.TAKEOUT_CATEGORY);
        if (json_array_out_category != null) {
            try {
                for (int i = 0 ; i < json_array_out_category.length() ; i++) {
                    JSONObject json_obj = json_array_out_category.getJSONObject(i);
                    if (json_obj == null) {
                        continue;
                    }

                    String code = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_CODE);
                    String name = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_NAME);

                    CodeInfo info = new CodeInfo();
                    info.setCodeInfoType(CodeInfo.CODE_TYPE_TAKE_OUT);
                    info.setCode(code);
                    info.setText(name);

                    array_out_category.add(info);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 반입구분
        ArrayList<CodeInfo> array_carry_in_category = new ArrayList<>();
        array_carry_in_category.clear();
        JSONArray json_array_carry_in_category = (JSONArray) VUtil.getValue(json, NetworkParam.CARRY_IN_CATEGORY_LIST);
        if (json_array_carry_in_category != null) {
            try {
                for (int i = 0 ; i < json_array_carry_in_category.length() ; i++) {
                    JSONObject json_obj = json_array_carry_in_category.getJSONObject(i);
                    if (json_obj == null) {
                        continue;
                    }

                    String code = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_CODE);
                    String name = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_NAME);

                    CodeInfo info = new CodeInfo();
                    info.setCodeInfoType(CodeInfo.CODE_TYPE_CARRY_IN);
                    info.setCode(code);
                    info.setText(name);

                    array_carry_in_category.add(info);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 반출구분
        ArrayList<CodeInfo> array_carry_out_category = new ArrayList<>();
        array_carry_out_category.clear();
        JSONArray json_array_carry_out_category = (JSONArray) VUtil.getValue(json, NetworkParam.CARRY_OUT_CATEGORY_LIST);
        if (json_array_carry_out_category != null) {
            try {
                for (int i = 0 ; i < json_array_carry_out_category.length() ; i++) {
                    JSONObject json_obj = json_array_carry_out_category.getJSONObject(i);
                    if (json_obj == null) {
                        continue;
                    }

                    String code = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_CODE);
                    String name = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_NAME);

                    CodeInfo info = new CodeInfo();
                    info.setCodeInfoType(CodeInfo.CODE_TYPE_CARRY_OUT);
                    info.setCode(code);
                    info.setText(name);

                    array_carry_out_category.add(info);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 반출 사유
        ArrayList<CodeInfo> array_carry_out_reason = new ArrayList<>();
        array_carry_out_reason.clear();
        JSONArray json_array_carry_out_reason = (JSONArray) VUtil.getValue(json, NetworkParam.CARRY_OUT_REASON_LIST);
        if (json_array_carry_out_reason != null) {
            try {
                for (int i = 0 ; i < json_array_carry_out_reason.length() ; i++) {
                    JSONObject json_obj = json_array_carry_out_reason.getJSONObject(i);
                    if (json_obj == null) {
                        continue;
                    }

                    String code = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_CODE);
                    String name = (String) VUtil.getValue(json_obj, NetworkParam.CATEGORY_NAME);

                    CodeInfo info = new CodeInfo();
                    info.setCodeInfoType(CodeInfo.CODE_TYPE_CARRY_OUT_REASON);
                    info.setCode(code);
                    info.setText(name);

                    array_carry_out_reason.add(info);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Object obj = VUtil.getValue(json, NetworkParam.VERSION);
        String svr_version = "";
        if (obj != null && !obj.toString().equals("null")) {
            svr_version = obj.toString();
        }

        EditText edit_id = (EditText) findViewById(R.id.login_edit_id);
        EditText edit_pass = (EditText) findViewById(R.id.login_edit_pass);

        IConfigManager config = new ConfigManager(mContext);
        config.setUserId(edit_id.getText().toString());
//        config.setUserId("jhlee");
        config.setPassword(edit_pass.getText().toString());

        config.setCompanyInfo(company);
        config.setCenterInfo(center);
        config.setCustomerInfo(customer);

        config.setCodeList(CodeInfo.CODE_TYPE_CENTER, array_center);
        config.setCodeList(CodeInfo.CODE_TYPE_CUSTOMER, array_customer);
        config.setCodeList(CodeInfo.CODE_TYPE_GOODS_STATE, array_goods_state);
        config.setCodeList(CodeInfo.CODE_TYPE_NOT_TAKE_OUT_REASON, array_not_take_out_reason);
        config.setCodeList(CodeInfo.CODE_TYPE_WAREHOUSE, array_ware_category);
        config.setCodeList(CodeInfo.CODE_TYPE_TAKE_OUT, array_out_category);
        config.setCodeList(CodeInfo.CODE_TYPE_CARRY_IN, array_carry_in_category);
        config.setCodeList(CodeInfo.CODE_TYPE_CARRY_OUT, array_carry_out_category);
        config.setCodeList(CodeInfo.CODE_TYPE_CARRY_OUT_REASON, array_carry_out_reason);

        // 다음 화면으로 이동
        moveNextActivity(svr_version);
    }

    /**
     * 다음 화면으로 이동
     * @param version 서버로부터 수신된 APK 버전
     */
    private void moveNextActivity(String version) {
        Intent i = new Intent();
        i.setClass(mContext, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(ScrnParam.APK_SVR_VERSION, version);

        startActivity(i);
        finish();
    }

    /**
     * 수행해야 할 동작으로 이동
     * @param state 수행해야 할 동작
     * @param  object 수행해야 할 동작에 필요한 값
     */
    private void nextState(int state, Object object) {
        Message msg = new Message();
        msg.what = state;
        msg.obj = object;

        mHandler.sendMessage(msg);
    }

    /**
     * 사용자 인증 요청
     */
    private void reqLogIn() {
        EditText edit_id = (EditText) findViewById(R.id.login_edit_id);
        String user_id = edit_id.getText().toString();
        if (user_id == null || user_id.length() <= 0) {
            showPopup(getString(R.string.not_exist_input_id));
            return ;
        }

        EditText edit_pass = (EditText) findViewById(R.id.login_edit_pass);
        String user_pass = edit_pass.getText().toString();
        if (user_pass == null || user_pass.length() <= 0) {
            showPopup(getString(R.string.not_exist_input_pass));
            return ;
        }

        showProgress(true, getString(R.string.progress_login));

        INetworkManager network = new NetworkManager(mContext, mLogInNetworkListener);
        network.reqLogIn(user_id, user_pass);
//        network.reqLogIn("A", "1");
    }

    /**
     * 서버와의 통신 결과를 수신하는 리스너
     */
    private INetworkListener mLogInNetworkListener = new INetworkListener() {
        @Override
        public void onRespResult(int error, String payload) {
            showProgress(false, "");

            // 통신 실패
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

            nextState(HANDLER_SHOW_LOGIN_RESULT, json);
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onResult(int error) {
        }
    };

    /**
     * 버튼 클릭 이벤트 수신 리스너
     */
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.login_btn_certify) {
                reqLogIn();
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

            case HANDLER_SHOW_LOGIN_RESULT :
                showLogInResult((JSONObject) msg.obj);
                break;
            }
        }
    };
}
