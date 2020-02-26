package com.vertexid.wms.scrn;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * Activity 의 부모 Activity
 */
public abstract class BaseActivity extends Activity {
    protected final String ACTION_ACTIVITY_CLOSE = "com.vertexid.wms.scrn.ACTION_ACTIVITY_CLOSE";

    /**
     * 상속받은 Activity들이 Activity Stack에서 사라져야 할 때 호출되는 동적 BroadcastReceiver
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

			/*
			 * Activity 종료 Action만 수신하도록 설계되었으므로
			 * 다른 Action들은 대해서는 전부 무시한다.
			 */
            String action = intent.getAction();
            if (action.equals(ACTION_ACTIVITY_CLOSE)) {
                // 종료를 하자.
                closeActivity();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		/*
		 * Activity 종료 액션 수신을 위한 IntentFilter 설정<br>
		 * 회원 탈퇴하여 App 종료를 한다거나 더 이상 사용하지 않는 Activity 정리를 위하여
		 */
        IntentFilter filter = new IntentFilter(ACTION_ACTIVITY_CLOSE);
        registerReceiver(mReceiver, filter);
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /** Activity 종료를 한다. */
    protected abstract void closeActivity();
}
