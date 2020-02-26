package com.vertexid.wms.network.comm;

import com.vertexid.wms.utils.VErrors;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HTTP 방식으로 서버와 통신을 하는 클래스
 */
public class CommHttp implements ICommManager {
    private final int MAX_TIME_OUT = 10 * 1000;

    private HttpURLConnection mHttp = null;
    private String mResponse = "";

    @Override
    public int connect(String addr) {
        int error = VErrors.E_NONE;

        try {
            URL url = new URL(addr);

            mHttp = (HttpURLConnection) url.openConnection();

            mHttp.setUseCaches(false);
            mHttp.setDoInput(true);
            mHttp.setDoOutput(true);
            mHttp.setConnectTimeout(MAX_TIME_OUT);
            mHttp.setReadTimeout(MAX_TIME_OUT);
            mHttp.setRequestMethod("POST");
            mHttp.setRequestProperty("Content-Language", "UTF-8");
            // 서버 Response Data를 JSON 형식의 타입으로 요청.
            mHttp.setRequestProperty("Accept", "application/json");
//			// 서버 Response Data를 xml 형식의 타입으로 요청.
//			mHttp.setRequestProperty("Accept", "application/xml");
//			// 타입설정(text/html) 형식으로 전송 (Request Body 전달 시 text/html로 서버에 전달.)
//			mHttp.setRequestProperty("Content-Type", "text/html");
//			// 타입설정(text/html) 형식으로 전송 (Request Body 전달 시 application/xml로 서버에 전달.)
//			mHttp.setRequestProperty("Content-Type", "application/xml");
            // 타입설정(application/json) 형식으로 전송 (Request Body 전달 시 application/json으로 서버에 전달.)
            mHttp.setRequestProperty("Content-Type", "application/json");
            // 컨트롤 캐쉬 설정
            mHttp.setRequestProperty("Cache-Control","no-cache");
            mHttp.connect();
        }
        catch (Exception e) {
            e.printStackTrace();
            error = VErrors.E_NOT_CONNECTED;
        }

        return error;
    }

    @Override
    public int send(String text) {
        int error = VErrors.E_NONE;
        DataOutputStream output = null;

        try {
            output = new DataOutputStream(mHttp.getOutputStream());
            output.write(text.getBytes("UTF-8"));
            output.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
            error = VErrors.E_SEND_TO_SERVER;
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
            buffer = new BufferedReader(new InputStreamReader(mHttp.getInputStream()));
            for ( ; ; ) {
                String line = buffer.readLine();
                if (line == null) {
                    break;
                }

                builder.append(line);
            }
        }
        catch (Exception e) {
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
            if (mHttp != null) {
                mHttp.disconnect();
                mHttp = null;
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
            code = mHttp.getResponseCode();
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
}
