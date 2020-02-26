package com.vertexid.wms.network.down;

import android.annotation.SuppressLint;

import com.vertexid.wms.utils.VErrors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import java.security.SecureRandom;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * HTTPS 방식으로 서버와 통신을 하여 파일을 받는 클래스
 */
public class DownHttps implements INetDownManager {
    private final int MAX_TIME_OUT = 10 * 1000;
    private final int BUFFER_SIZE = 1024;

    private HttpsURLConnection mHttps = null;

    @Override
    public int connect(String addr) {
        int error = VErrors.E_NONE;

        try {
            URL url = new URL(addr);

            trustAllHosts();

            mHttps = (HttpsURLConnection) url.openConnection();
            mHttps.setHostnameVerifier(DO_NOT_VERIFY);
            mHttps.setUseCaches(false);
            mHttps.setDoInput(true);
            mHttps.setDoOutput(false);
            mHttps.setConnectTimeout(MAX_TIME_OUT);
            mHttps.setReadTimeout(MAX_TIME_OUT);
            mHttps.setRequestMethod("GET");
            // 컨트롤 캐쉬 설정
            mHttps.setRequestProperty("Cache-Control", "no-cache");
            mHttps.connect();

            mHttps.getResponseCode();
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

        int content_length = mHttps.getContentLength();
        if (content_length <= 0) {
            return VErrors.E_ETC;
        }

        BufferedInputStream bis = null;
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(path + File.separator + "wms.apk");
            bis = new BufferedInputStream(mHttps.getInputStream());

            int count;
            int total = 0;
            byte [] data = new byte[BUFFER_SIZE];

            while ((count = bis.read(data)) != -1) {
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
            mHttps.disconnect();
            mHttps = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return VErrors.E_NONE;
    }

    @SuppressLint("TrulyRandom")
    private void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
