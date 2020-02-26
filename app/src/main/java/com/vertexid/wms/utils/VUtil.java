package com.vertexid.wms.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 각 유틸 모음
 */
public class VUtil {
    /**
     * 단말 Device ID를 반환한다.
     * @param context Context
     * @return 단말 Device ID. 없으면 null
     */
    public static String getDeviceId(Context context) {
        if (context == null) {
            return null;
        }

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager == null) {
            return null;
        }

        return manager.getDeviceId();
    }

    /**
     * 버전 명을 반환한다.
     * @param context Context
     * @return 버전
     */
    public static String getPackageVersion(Context context) {
        if (context == null) {
            return null;
        }

        PackageManager manager = context.getPackageManager();
        if (manager == null) {
            return null;
        }

        PackageInfo pi;
        try {
            pi = manager.getPackageInfo(context.getPackageName(), 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return pi.versionName;
    }

    /**
     * JSON 형태의 문자열을 JSON 객체로 변환하여 반환한다.
     * @param text JSON 객체로 변환하기 위한 JSON 형태의 문자열
     * @return JSON 객체
     */
    public static JSONObject convertToJSON(String text) {
        if (text == null || text.length() <= 0) {
            return null;
        }

        JSONObject json = null;
        try {
            json = new JSONObject(text);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * JSON 객체로부터 값을 찾아 반환한다.
     * @param json JSON 객체
     * @param key JSON 객체에 있는 키
     * @return JSON 객체로부터 얻어낸 값
     */
    public static Object getValue(JSONObject json, String key) {
        if (json == null) {
            return null;
        }

        if (key == null || key.length() <= 0) {
            return null;
        }

        Object object;
        try {
            object = json.get(key);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return object;
    }

    /**
     * 사용자에게 알림
     * @param text 사용자에게 알릴 내용
     */
    public static void showPopup(Context context, String text) {
        if (text == null || text.length() <= 0) {
            return ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text);
        builder.setPositiveButton("확인", null);
        builder.show();
    }
}
