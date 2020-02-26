package com.vertexid.wms.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vertexid.wms.LogInActivity;

import java.util.List;

/**
 * 단말에서 방송하는 액션 수신 리시버
 */
public class DeviceEvtReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        String action = intent != null ? intent.getAction() : "";
//        if (action == null || action.length() <= 0) {
//            return ;
//        }
//
//        // 단말 재부팅 완료
//        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
//            onStartActivity(context);
//        }
//        // 설치된 앱 중 하나가 업데이트 됨
//        else if (action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
//            onUpdated(context, intent);
//        }
    }

    /**
     * 액티비티 실행
     * @param context Context
     */
    private void onStartActivity(Context context) {
        boolean is_running = isRunningApp(context);
        if (is_running) {
            return ;
        }

        Intent i = new Intent();
        i.setClass(context, LogInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    /**
     * 새로 업데이트가 되었다. 이럴 경우에는 앱이 종료되어 있는 상태이므로 시작을 시켜주어야 한다.
     * @param context Context
     * @param i Intent
     */
    private void onUpdated(Context context, Intent i) {
        if (context == null) {
            return ;
        }

        if (i == null) {
            return ;
        }

        // 나의 패키지 명을 확인
        String my_name = context.getPackageName();
        if (my_name == null || my_name.length() <= 0) {
            return ;
        }

        // 업데이트 된 패키지 명 확인
        String package_name = i.getData().getSchemeSpecificPart();
        if (package_name == null || package_name.length() <= 0) {
            return ;
        }

        // 업데이트 된 패키지는 바로 나 자신이다.
        if (my_name.equals(package_name)) {
            onStartActivity(context);
        }
    }

    /**
     * 나 자신이 현재 실행 중인지 확인
     * @param context Context
     * @return true : 현재 실행 중, false : 현재 미 실행 중.
     */
    private boolean isRunningApp(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
        if (processes == null || processes.size() <= 0) {
            return false;
        }

        ActivityManager.RunningAppProcessInfo process = processes.get(0);
        if (process == null) {
            return false;
        }

        String my_name = context.getPackageName();
        if (my_name.equals(process.processName)) {
            return true;
        }

        return false;
    }
}
