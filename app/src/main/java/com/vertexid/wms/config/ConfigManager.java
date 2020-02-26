package com.vertexid.wms.config;

import android.content.Context;

import com.vertexid.wms.config.sqlite.ConfigDBSQLiteHelper;
import com.vertexid.wms.info.CodeInfo;

import java.util.ArrayList;

/**
 * 동작에 필요한 내용 설정을 위한 외부 공개 API를 구현한 클래스
 */
public class ConfigManager implements IConfigManager {
    private ConfigDBSQLiteHelper mHelper;

    public ConfigManager(Context context) {
        mHelper = new ConfigDBSQLiteHelper(context);
    }

    @Override
    public void clear() {
//        setUserId(context, "");
//        setPassword(context, "");
    }

    @Override
    public void setUserId(String user_id) {
        if (user_id == null) {
            user_id = "";
        }

        mHelper.setUserId(user_id);
//        if (context == null) {
//            return ;
//        }
//
////        StringBuilder builder = new StringBuilder();
////        builder.append(ConfigDBSettings.SCHEME);
////        builder.append(context.getPackageName());
////        builder.append(ConfigDBSettings.AUTH_TAIL);
////        builder.append("/");
////        builder.append(ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER));
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_ID][ConfigDBSettings.NAME];
//
//        ContentValues values = new ContentValues();
//        values.put(column, user_id);
//        context.getContentResolver().update(uri, values, null, null);
    }

    @Override
    public String getUserId() {
        return mHelper.getUserId();
////        StringBuilder builder = new StringBuilder();
////        builder.append(ConfigDBSettings.SCHEME);
////        builder.append(context.getPackageName());
////        builder.append(ConfigDBSettings.AUTH_TAIL);
////        builder.append("/");
////        builder.append(ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER));
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_ID][ConfigDBSettings.NAME];
//        String[] projection = { column };
//
//        Cursor cursor = context.getContentResolver().query(
//                uri,		// content://로 시작하는 content table uri - 테이블 명
//                projection,	// 테이블 column. null이면 모든 열을 출력.
//                null,		// 어떤 값을 출력할 것인지
//                null,
//                null);		// 정렬 방식. null이면 정렬 안함
//
//        if (cursor == null) {
//            return null;
//        }
//
//        String value = "";
//        if (cursor.moveToFirst()) {
//            int index = cursor.getColumnIndex(column);
//            value = cursor.getString(index);
//        }
//
//        cursor.close();
//        return value;
    }

    @Override
    public void setPassword(String password) {
        if (password == null) {
            password = "";
        }

        mHelper.setPassword(password);
//        if (context == null) {
//            return ;
//        }
//
////        StringBuilder builder = new StringBuilder();
////        builder.append(ConfigDBSettings.SCHEME);
////        builder.append(context.getPackageName());
////        builder.append(ConfigDBSettings.AUTH_TAIL);
////        builder.append("/");
////        builder.append(ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER));
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_PASSWORD][ConfigDBSettings.NAME];
//
//        ContentValues values = new ContentValues();
//        values.put(column, password);
//        context.getContentResolver().update(uri, values, null, null);
    }

    @Override
    public String getPassword() {
        return mHelper.getPassword();
//        if (context == null) {
//            return null;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_PASSWORD][ConfigDBSettings.NAME];
//        String[] projection = { column };
//
//        Cursor cursor = context.getContentResolver().query(
//                uri,		// content://로 시작하는 content table uri - 테이블 명
//                projection,	// 테이블 column. null이면 모든 열을 출력.
//                null,		// 어떤 값을 출력할 것인지
//                null,
//                null);		// 정렬 방식. null이면 정렬 안함
//
//        if (cursor == null) {
//            return null;
//        }
//
//        String value = "";
//        if (cursor.moveToFirst()) {
//            int index = cursor.getColumnIndex(column);
//            value = cursor.getString(index);
//        }
//
//        cursor.close();
//        return value;
    }

    @Override
    public void setUserName(String name) {
        if (name == null) {
            name = "";
        }

        mHelper.setUserName(name);
//        if (context == null) {
//            return ;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_NAME][ConfigDBSettings.NAME];
//
//        ContentValues values = new ContentValues();
//        values.put(column, name);
//        context.getContentResolver().update(uri, values, null, null);
    }

    @Override
    public String getUserName() {
        return mHelper.getUserName();
//        if (context == null) {
//            return null;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_NAME][ConfigDBSettings.NAME];
//        String[] projection = { column };
//
//        Cursor cursor = context.getContentResolver().query(
//                uri,		// content://로 시작하는 content table uri - 테이블 명
//                projection,	// 테이블 column. null이면 모든 열을 출력.
//                null,		// 어떤 값을 출력할 것인지
//                null,
//                null);		// 정렬 방식. null이면 정렬 안함
//
//        if (cursor == null) {
//            return null;
//        }
//
//        String value = "";
//        if (cursor.moveToFirst()) {
//            int index = cursor.getColumnIndex(column);
//            value = cursor.getString(index);
//        }
//
//        cursor.close();
//        return value;
    }

    @Override
    public void setCompanyInfo(CodeInfo info) {
        if (mHelper != null) {
            mHelper.setCompanyInfo(info);
        }
//        if (context == null) {
//            return ;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        if (uri == null) {
//            return ;
//        }
//
//        int COLUMN_NAME = ConfigDBSettings.NAME;
//        int CODE = ConfigDBSettings.MEMBER_INFO.INDEX_COMPANY_CODE;
//        int NAME = ConfigDBSettings.MEMBER_INFO.INDEX_COMPANY_NAME;
//
//        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[CODE][COLUMN_NAME];
//        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[NAME][COLUMN_NAME];
//
//        String code = "";
//        String name = "";
//        if (info != null) {
//            code = info.getCode();
//            name = info.getText();
//        }
//
//        ContentValues values = new ContentValues();
//        values.put(column_code, code);
//        values.put(column_name, name);
//        context.getContentResolver().update(uri, values, null, null);
    }

    @Override
    public CodeInfo getCompanyInfo() {
        return mHelper.getCompanyInfo();
//        if (context == null) {
//            return null;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        if (uri == null) {
//            return null;
//        }
//
//        int COLUMN_NAME = ConfigDBSettings.NAME;
//        int CODE = ConfigDBSettings.MEMBER_INFO.INDEX_COMPANY_CODE;
//        int NAME = ConfigDBSettings.MEMBER_INFO.INDEX_COMPANY_NAME;
//
//        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[CODE][COLUMN_NAME];
//        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[NAME][COLUMN_NAME];
//        String[] projection = { column_code, column_name };
//
//        Cursor cursor = context.getContentResolver().query(
//                uri,		// content://로 시작하는 content table uri - 테이블 명
//                projection,	// 테이블 column. null이면 모든 열을 출력.
//                null,		// 어떤 값을 출력할 것인지
//                null,
//                null);		// 정렬 방식. null이면 정렬 안함
//
//        if (cursor == null) {
//            return null;
//        }
//
//        CodeInfo info = new CodeInfo();
//
//        if (cursor.moveToFirst()) {
//            int index_code = cursor.getColumnIndex(column_code);
//            int index_name = cursor.getColumnIndex(column_name);
//
//            String code = cursor.getString(index_code);
//            String name = cursor.getString(index_name);
//
//            info.setCode(code);
//            info.setText(name);
//        }
//
//        cursor.close();
//        return info;
    }

    @Override
    public void setCenterInfo(CodeInfo info) {
        if (mHelper != null) {
            mHelper.setCenterInfo(info);
        }
//        if (context == null) {
//            return ;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        if (uri == null) {
//            return ;
//        }
//
//        int COLUMN_NAME = ConfigDBSettings.NAME;
//        int CODE = ConfigDBSettings.MEMBER_INFO.INDEX_CENTER_CODE;
//        int NAME = ConfigDBSettings.MEMBER_INFO.INDEX_CENTER_NAME;
//
//        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[CODE][COLUMN_NAME];
//        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[NAME][COLUMN_NAME];
//
//        String code = "";
//        String name = "";
//        if (info != null) {
//            code = info.getCode();
//            name = info.getText();
//        }
//
//        ContentValues values = new ContentValues();
//        values.put(column_code, code);
//        values.put(column_name, name);
//        context.getContentResolver().update(uri, values, null, null);
    }

    @Override
    public CodeInfo getCenterInfo() {
        return mHelper.getCenterInfo();
//        if (context == null) {
//            return null;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        if (uri == null) {
//            return null;
//        }
//
//        int COLUMN_NAME = ConfigDBSettings.NAME;
//        int CODE = ConfigDBSettings.MEMBER_INFO.INDEX_CENTER_CODE;
//        int NAME = ConfigDBSettings.MEMBER_INFO.INDEX_CENTER_NAME;
//
//        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[CODE][COLUMN_NAME];
//        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[NAME][COLUMN_NAME];
//        String[] projection = { column_code, column_name };
//
//        Cursor cursor = context.getContentResolver().query(
//                uri,		// content://로 시작하는 content table uri - 테이블 명
//                projection,	// 테이블 column. null이면 모든 열을 출력.
//                null,		// 어떤 값을 출력할 것인지
//                null,
//                null);		// 정렬 방식. null이면 정렬 안함
//
//        if (cursor == null) {
//            return null;
//        }
//
//        CodeInfo info = new CodeInfo();
//
//        if (cursor.moveToFirst()) {
//            int index_code = cursor.getColumnIndex(column_code);
//            int index_name = cursor.getColumnIndex(column_name);
//
//            String code = cursor.getString(index_code);
//            String name = cursor.getString(index_name);
//
//            info.setCode(code);
//            info.setText(name);
//        }
//
//        cursor.close();
//        return info;
    }

    @Override
    public void setCustomerInfo(CodeInfo info) {
        if (mHelper != null) {
            mHelper.setCustomerInfo(info);
        }
//        if (context == null) {
//            return ;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        if (uri == null) {
//            return ;
//        }
//
//        int COLUMN_NAME = ConfigDBSettings.NAME;
//        int CODE = ConfigDBSettings.MEMBER_INFO.INDEX_CUSTOMER_CODE;
//        int NAME = ConfigDBSettings.MEMBER_INFO.INDEX_CUSTOMER_NAME;
//
//        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[CODE][COLUMN_NAME];
//        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[NAME][COLUMN_NAME];
//
//        String code = "";
//        String name = "";
//        if (info != null) {
//            code = info.getCode();
//            name = info.getText();
//        }
//
//        ContentValues values = new ContentValues();
//        values.put(column_code, code);
//        values.put(column_name, name);
//        context.getContentResolver().update(uri, values, null, null);
    }

    @Override
    public CodeInfo getCustomerInfo() {
        return mHelper.getCustomerInfo();
//        if (context == null) {
//            return null;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_MEMBER);
//
//        Uri uri = Uri.parse(uri_text);
//        if (uri == null) {
//            return null;
//        }
//
//        int COLUMN_NAME = ConfigDBSettings.NAME;
//        int CODE = ConfigDBSettings.MEMBER_INFO.INDEX_CUSTOMER_CODE;
//        int NAME = ConfigDBSettings.MEMBER_INFO.INDEX_CUSTOMER_NAME;
//
//        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[CODE][COLUMN_NAME];
//        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[NAME][COLUMN_NAME];
//        String[] projection = { column_code, column_name };
//
//        Cursor cursor = context.getContentResolver().query(
//                uri,		// content://로 시작하는 content table uri - 테이블 명
//                projection,		// 테이블 column 명. null이면 모든 열을 출력.
//                null,	// where 절
//                null,	// where 절에 들어갈 값
//                null);		// 정렬 방식. null이면 정렬 안함
//
//        if (cursor == null) {
//            return null;
//        }
//
//        CodeInfo info = new CodeInfo();
//
//        if (cursor.moveToFirst()) {
//            int index_code = cursor.getColumnIndex(column_code);
//            int index_name = cursor.getColumnIndex(column_name);
//
//            String code = cursor.getString(index_code);
//            String name = cursor.getString(index_name);
//
//            info.setCode(code);
//            info.setText(name);
//        }
//
//        cursor.close();
//        return info;
    }

    @Override
    public void setCodeList(int code_type, ArrayList<CodeInfo> list) {
        mHelper.setCodeList(code_type, list);
//        if (context == null) {
//            return;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_CODE_INFO);
//
//        Uri uri = Uri.parse(uri_text);
//        if (uri == null) {
//            return ;
//        }
//
//        // 기존 내용 삭제
//        context.getContentResolver().delete(
//                uri,
//                "code_type=?",
//                new String[]{ String.valueOf(code_type) });
//
//        int COLUMN_NAME = ConfigDBSettings.NAME;
//        int TYPE = ConfigDBSettings.CODE_INFO.INDEX_TYPE;
//        int CODE = ConfigDBSettings.CODE_INFO.INDEX_CODE;
//        int TEXT = ConfigDBSettings.CODE_INFO.INDEX_TEXT;
//
//        String column_type = ConfigDBSettings.CODE_INFO.COLUMN[TYPE][COLUMN_NAME];
//        String column_code = ConfigDBSettings.CODE_INFO.COLUMN[CODE][COLUMN_NAME];
//        String column_text = ConfigDBSettings.CODE_INFO.COLUMN[TEXT][COLUMN_NAME];
//
//        ContentValues values = new ContentValues();
//
//        for(int i = 0; i < list.size(); i++) {
//            CodeInfo data = list.get(i);
//
//            values.put(column_type, data.getCodeInfoType());
//            values.put(column_code, data.getCode());
//            values.put(column_text, data.getText());
//
//            context.getContentResolver().insert(uri, values);
//
//            values.clear();
//        }
    }

    @Override
    public ArrayList<CodeInfo> getCodeList(int code_type) {
        return mHelper.getCodeList(code_type);
//        if (context == null) {
//            return null;
//        }
//
//        String uri_text = ConfigDBSettings.SCHEME +
//                context.getPackageName() +
//                ConfigDBSettings.AUTH_TAIL +
//                "/" +
//                ConfigDBSettings.getTableName(ConfigDBSettings.TABLE_INDEX_CODE_INFO);
//
//        Uri uri = Uri.parse(uri_text);
//
//        Cursor cursor = context.getContentResolver().query(
//                uri,		// content://로 시작하는 content table uri - 테이블 명
//                null,		// 테이블 column 명. null이면 모든 열을 출력.
//                "code_type=?",		// where 절
//                new String[]{ String.valueOf(code_type) },		// where 절에 들어갈 값
//                null);		// 정렬 방식. null이면 정렬 안함
//
//        if (cursor == null) {
//            return null;
//        }
//
//        int COLUMN_NAME = ConfigDBSettings.NAME;
//        int TYPE = ConfigDBSettings.CODE_INFO.INDEX_TYPE;
//        int CODE = ConfigDBSettings.CODE_INFO.INDEX_CODE;
//        int TEXT = ConfigDBSettings.CODE_INFO.INDEX_TEXT;
//
//        String column_type = ConfigDBSettings.CODE_INFO.COLUMN[TYPE][COLUMN_NAME];
//        String column_code = ConfigDBSettings.CODE_INFO.COLUMN[CODE][COLUMN_NAME];
//        String column_text = ConfigDBSettings.CODE_INFO.COLUMN[TEXT][COLUMN_NAME];
//
//        ArrayList<CodeInfo> list = new ArrayList<>();
//        list.clear();
//
//        if (cursor.moveToFirst()) {
//            int index_type = cursor.getColumnIndex(column_type);
//            int index_code = cursor.getColumnIndex(column_code);
//            int index_text = cursor.getColumnIndex(column_text);
//
//            do {
//                CodeInfo info = new CodeInfo();
//
//                int type = cursor.getInt(index_type);
//                String code = cursor.getString(index_code);
//                String text = cursor.getString(index_text);
//
//                info.setCodeInfoType(type);
//                info.setCode(code);
//                info.setText(text);
//
//                list.add(info);
//            } while(cursor.moveToNext());
//        }
//
//        cursor.close();
//
//        return list;
    }
}
