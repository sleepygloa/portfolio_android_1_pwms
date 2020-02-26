package com.vertexid.wms.utils;

import android.util.Log;

/**
 * 로그 출력
 */
public class VLog {
    private static final String TAG = "WMS";
    private static final boolean IS_PRINT = true;

    public static void d(String msg) {
        if (IS_PRINT) {
            Log.d(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (IS_PRINT) {
            Log.w(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (IS_PRINT) {
            Log.i(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (IS_PRINT) {
            Log.e(TAG, msg);
        }
    }
}
