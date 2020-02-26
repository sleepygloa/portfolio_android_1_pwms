package com.vertexid.wms.network.comm;

import com.vertexid.wms.utils.VErrors;

/**
 * 서버와의 접속 동작을 위한 외부 공개 API를 구현한 클래스
 */
public class CommManager implements ICommManager {
    private ICommManager mInterface = null;

    public CommManager(String name) {
        initComm(name);
    }

    private void initComm(String name) {
        Class<?> cls = null;

        try {
            String pkg = this.getClass().getPackage().getName();
            String cls_path = pkg + "." + name;
            cls = Class.forName(cls_path);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (cls != null) {
            try {
                mInterface = (ICommManager) cls.newInstance();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int connect(String addr) {
        if (mInterface == null) {
            return VErrors.E_ETC;
        }

        return mInterface.connect(addr);
    }

    @Override
    public int send(String payload) {
        if (mInterface == null) {
            return VErrors.E_ETC;
        }

        return mInterface.send(payload);
    }

    @Override
    public int recv() {
        if (mInterface == null) {
            return VErrors.E_ETC;
        }

        return mInterface.recv();
    }

    @Override
    public int close() {
        if (mInterface == null) {
            return VErrors.E_ETC;
        }

        return mInterface.close();
    }

    @Override
    public int getRespCode() {
        if (mInterface == null) {
            return VErrors.E_ETC;
        }

        return mInterface.getRespCode();
    }

    @Override
    public String getRespPayload() {
        if (mInterface == null) {
            return null;
        }

        return mInterface.getRespPayload();
    }
}
