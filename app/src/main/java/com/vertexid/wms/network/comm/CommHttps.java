package com.vertexid.wms.network.comm;

import android.annotation.SuppressLint;

import com.vertexid.wms.utils.VErrors;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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
 * HTTPS 방식으로 서버와 통신을 하는 클래스
 */
public class CommHttps implements ICommManager {
    private final int MAX_TIME_OUT = 10 * 1000;

    private HttpsURLConnection mHttps = null;
    private String mResponse = "";

    @Override
    public int connect(String svr_addr) {
        if (svr_addr == null || svr_addr.length() <= 0) {
            return VErrors.E_INVALID_PARAM;
        }

        try {
            URL url = new URL(svr_addr);

            trustAllHosts();

            mHttps = (HttpsURLConnection) url.openConnection();
            mHttps.setHostnameVerifier(DO_NOT_VERIFY);

            mHttps.setUseCaches(false);
            mHttps.setDoInput(true);
            mHttps.setDoOutput(true);
            mHttps.setConnectTimeout(MAX_TIME_OUT);
            mHttps.setReadTimeout(MAX_TIME_OUT);
            mHttps.setRequestMethod("POST");
            mHttps.setRequestProperty("Content-Language", "UTF-8");
            mHttps.setRequestProperty("Content-Type", "application/json");

            mHttps.connect();
        }
        catch (Exception e) {
            e.printStackTrace();
            return VErrors.E_NOT_CONNECTED;
        }

        return VErrors.E_NONE;
    }

    @Override
    public int send(String text) {
        int error = VErrors.E_NONE;
        DataOutputStream output = null;

        try {
            output = new DataOutputStream(mHttps.getOutputStream());
            output.write(text.getBytes());
            output.flush();
        }
        catch (Exception e) {
            error = VErrors.E_SEND_TO_SERVER;
            e.printStackTrace();
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return error;
    }

    @Override
    public int recv() {
        int error = VErrors.E_NONE;

        StringBuilder builder = new StringBuilder();
        BufferedReader buffer = null;

        try {
            buffer = new BufferedReader(new InputStreamReader(mHttps.getInputStream()));
            for ( ; ; ) {
                String line = buffer.readLine();
                if (line == null) {
                    break;
                }

                builder.append(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            error = VErrors.E_RECV_FROM_SERVER;
        }
        finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        mResponse = builder.toString().trim();
        return error;
    }

    @Override
    public int close() {
        try {
            if (mHttps != null) {
                mHttps.disconnect();
                mHttps = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return VErrors.E_NONE;
    }

    @Override
    public int getRespCode() {
        int code = 0;

        try {
            code = mHttps.getResponseCode();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return code;
    }

    @Override
    public String getRespPayload() {
        return mResponse;
    }

    @SuppressLint("TrulyRandom")
    private void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate [] getAcceptedIssuers() {
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
