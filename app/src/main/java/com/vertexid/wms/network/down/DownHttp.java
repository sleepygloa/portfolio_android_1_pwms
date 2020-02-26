package com.vertexid.wms.network.down;

import com.vertexid.wms.utils.VErrors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HTTP 방식으로 서버와 통신을 하는 클래스
 */
public class DownHttp implements INetDownManager {
    private final int MAX_TIME_OUT = 5 * 1000;
    private final int BUFFER_SIZE = 1024;

    private HttpURLConnection mHttp = null;

    @Override
    public int connect(String addr) {
        int error = VErrors.E_NONE;

        try {
            URL url = new URL(addr);

            mHttp = (HttpURLConnection) url.openConnection();

            mHttp.setUseCaches(false);
            mHttp.setDoInput(true);
            mHttp.setDoOutput(false);
            mHttp.setConnectTimeout(MAX_TIME_OUT);
            mHttp.setReadTimeout(MAX_TIME_OUT);
            mHttp.setRequestMethod("GET");
            // 컨트롤 캐쉬 설정
            mHttp.setRequestProperty("Cache-Control", "no-cache");
            mHttp.connect();

            mHttp.getResponseCode();
        }
        catch (Exception e) {
            error = VErrors.E_UNKNOWN_HOST;
            e.printStackTrace();
        }

        return error;
    }

    @Override
    public int recv(String path, INetDownListener listener) {
        if (path == null || path.length() <= 0) {
            return VErrors.E_INVALID_PARAM;
        }

        int error = VErrors.E_NONE;

        int content_length = mHttp.getContentLength();
        if (content_length <= 0) {
            return VErrors.E_ETC;
        }

        BufferedInputStream bis = null;
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(path + File.separator + "wms.apk");
            bis = new BufferedInputStream(mHttp.getInputStream());

            int count;
            int total = 0;
            byte [] data = new byte[BUFFER_SIZE];

            while ((count = bis.read(data)) != -1) {
                // UI에 진행 상황 알림
                if (listener != null) {
                    listener.onProgress(((total * 100) / content_length));
                }

                total += count;
                fos.write(data, 0, count);
            }

            fos.flush();
        }
        catch (Exception e) {
            error = VErrors.E_RECV_FROM_SERVER;
            e.printStackTrace();
        }
        finally {
            try {
                if (bis != null) {
                    bis.close();
                }

                if (fos != null) {
                    fos.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return error;
    }

    @Override
    public int close() {
        try {
            if (mHttp != null) {
                mHttp.disconnect();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return VErrors.E_NONE;
    }
}
