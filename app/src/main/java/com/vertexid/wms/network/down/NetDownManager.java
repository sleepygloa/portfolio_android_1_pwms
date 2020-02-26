package com.vertexid.wms.network.down;

import com.vertexid.wms.utils.VErrors;

/**
 * 서버에 접속하여 서버로부터 파일 다운로드 동작을 위한 외부 공개 API를 구현한 클래스
 */
public class NetDownManager implements INetDownManager {
    private INetDownManager mInterface = null;

    public NetDownManager(String name) {
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
                mInterface = (INetDownManager) cls.newInstance();
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
    public int recv(String path, INetDownListener listener) {
        if (mInterface == null) {
            return VErrors.E_ETC;
        }

        return mInterface.recv(path, listener);
    }

    @Override
    public int close() {
        if (mInterface == null) {
            return VErrors.E_ETC;
        }

        return mInterface.close();
    }
}
