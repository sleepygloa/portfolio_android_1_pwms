package com.vertexid.wms.config.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;

import java.util.ArrayList;

/**
 * SQLite 데이터베이스 접근 관리 클래스
 */
public class ConfigDBSQLiteHelper extends SQLiteOpenHelper implements IConfigManager {
    /** database 이름 */
    private static final String DB_NAME = "vertexid_wms_config.db";
    /** database 버전 */
    private static final int DB_VERSION = 1;

    public ConfigDBSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0 ; i < ConfigDBSettings.TABLE_INDEX_MAX ; i++) {
            createTable(db, i);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			if (oldVersion < newVersion) {
//				int current_ver = oldVersion + 1;
//				while(current_ver <= newVersion) {
//					switch(current_ver) {
//					case 2:
//						// 애플리케이션 설치/삭제 정보를 서버에 전달할 때 필요한 테이블 생성.
//						createTable(db, ConfigDBSettings.TABLE_INDEX_SERVER);
//						break;
//					}
//				}
//			}
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public void clear() {
    }

    @Override
    public void setUserId(String user_id) {
        if (user_id == null) {
            user_id = "";
        }

        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_ID][ConfigDBSettings.NAME];

        ContentValues values = new ContentValues();
        values.put(column, user_id);

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.update(
                    ConfigDBSettings.MEMBER_INFO.TABLE_NAME,
                    values,
                    null,
                    null);
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public String getUserId() {
        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_ID][ConfigDBSettings.NAME];
        String[] projection = { column };

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                false,
                ConfigDBSettings.MEMBER_INFO.TABLE_NAME,		// content://로 시작하는 content table uri - 테이블 명
                projection,		// 테이블 column 명. null이면 모든 열을 출력.
                null,		// where 절
                null,		// where 절에 들어갈 값
                null,   // 정렬 방식. null이면 정렬 안함
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }

        String value = "";
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(column);
            value = cursor.getString(index);
        }

        cursor.close();
        return value;
    }

    @Override
    public void setPassword(String password) {
        if (password == null) {
            password = "";
        }

        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_PASSWORD][ConfigDBSettings.NAME];

        ContentValues values = new ContentValues();
        values.put(column, password);

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.update(
                    ConfigDBSettings.MEMBER_INFO.TABLE_NAME,
                    values,
                    null,
                    null);
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public String getPassword() {
        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_PASSWORD][ConfigDBSettings.NAME];
        String[] projection = { column };

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                false,
                ConfigDBSettings.MEMBER_INFO.TABLE_NAME,		// content://로 시작하는 content table uri - 테이블 명
                projection,		// 테이블 column 명. null이면 모든 열을 출력.
                null,		// where 절
                null,		// where 절에 들어갈 값
                null,   // 정렬 방식. null이면 정렬 안함
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }

        String value = "";
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(column);
            value = cursor.getString(index);
        }

        cursor.close();
        return value;
    }

    @Override
    public void setUserName(String name) {
        if (name == null) {
            name = "";
        }

        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_NAME][ConfigDBSettings.NAME];

        ContentValues values = new ContentValues();
        values.put(column, name);

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.update(
                    ConfigDBSettings.MEMBER_INFO.TABLE_NAME,
                    values,
                    null,
                    null);
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public String getUserName() {
        String column = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_USER_NAME][ConfigDBSettings.NAME];
        String[] projection = { column };

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                false,
                ConfigDBSettings.MEMBER_INFO.TABLE_NAME,		// content://로 시작하는 content table uri - 테이블 명
                projection,		// 테이블 column 명. null이면 모든 열을 출력.
                null,		// where 절
                null,		// where 절에 들어갈 값
                null,   // 정렬 방식. null이면 정렬 안함
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }

        String value = "";
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(column);
            value = cursor.getString(index);
        }

        cursor.close();
        return value;
    }

    @Override
    public void setCompanyInfo(CodeInfo info) {
        if (info == null) {
            return ;
        }

        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_COMPANY_CODE][ConfigDBSettings.NAME];
        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_COMPANY_NAME][ConfigDBSettings.NAME];

        ContentValues values = new ContentValues();
        values.put(column_code, info.getCode());
        values.put(column_name, info.getText());

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.update(
                    ConfigDBSettings.MEMBER_INFO.TABLE_NAME,
                    values,
                    null,
                    null);
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public CodeInfo getCompanyInfo() {
        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_COMPANY_CODE][ConfigDBSettings.NAME];
        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_COMPANY_NAME][ConfigDBSettings.NAME];
        String[] projection = { column_code, column_name };

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                false,
                ConfigDBSettings.MEMBER_INFO.TABLE_NAME,		// content://로 시작하는 content table uri - 테이블 명
                projection,		// 테이블 column 명. null이면 모든 열을 출력.
                null,		// where 절
                null,		// where 절에 들어갈 값
                null,   // 정렬 방식. null이면 정렬 안함
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }

        CodeInfo info = new CodeInfo();

        if (cursor.moveToFirst()) {
            int index_code = cursor.getColumnIndex(column_code);
            int index_name = cursor.getColumnIndex(column_name);

            String code = cursor.getString(index_code);
            String name = cursor.getString(index_name);

            info.setCode(code);
            info.setText(name);
        }

        cursor.close();
        return info;
    }

    @Override
    public void setCenterInfo(CodeInfo info) {
        if (info == null) {
            return ;
        }

        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_CENTER_CODE][ConfigDBSettings.NAME];
        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_CENTER_NAME][ConfigDBSettings.NAME];

        ContentValues values = new ContentValues();
        values.put(column_code, info.getCode());
        values.put(column_name, info.getText());

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.update(
                    ConfigDBSettings.MEMBER_INFO.TABLE_NAME,
                    values,
                    null,
                    null);
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public CodeInfo getCenterInfo() {
        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_CENTER_CODE][ConfigDBSettings.NAME];
        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_CENTER_NAME][ConfigDBSettings.NAME];
        String[] projection = { column_code, column_name };

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                false,
                ConfigDBSettings.MEMBER_INFO.TABLE_NAME,		// content://로 시작하는 content table uri - 테이블 명
                projection,		// 테이블 column 명. null이면 모든 열을 출력.
                null,		// where 절
                null,		// where 절에 들어갈 값
                null,   // 정렬 방식. null이면 정렬 안함
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }

        CodeInfo info = new CodeInfo();

        if (cursor.moveToFirst()) {
            int index_code = cursor.getColumnIndex(column_code);
            int index_name = cursor.getColumnIndex(column_name);

            String code = cursor.getString(index_code);
            String name = cursor.getString(index_name);

            info.setCode(code);
            info.setText(name);
        }

        cursor.close();
        return info;
    }

    @Override
    public void setCustomerInfo(CodeInfo info) {
        if (info == null) {
            return ;
        }

        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_CUSTOMER_CODE][ConfigDBSettings.NAME];
        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_CUSTOMER_NAME][ConfigDBSettings.NAME];

        ContentValues values = new ContentValues();
        values.put(column_code, info.getCode());
        values.put(column_name, info.getText());

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.update(
                    ConfigDBSettings.MEMBER_INFO.TABLE_NAME,
                    values,
                    null,
                    null);
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public CodeInfo getCustomerInfo() {
        String column_code = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_CUSTOMER_CODE][ConfigDBSettings.NAME];
        String column_name = ConfigDBSettings.MEMBER_INFO.COLUMN[ConfigDBSettings.MEMBER_INFO.INDEX_CUSTOMER_NAME][ConfigDBSettings.NAME];
        String[] projection = { column_code, column_name };

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                false,
                ConfigDBSettings.MEMBER_INFO.TABLE_NAME,		// content://로 시작하는 content table uri - 테이블 명
                projection,		// 테이블 column 명. null이면 모든 열을 출력.
                null,		// where 절
                null,		// where 절에 들어갈 값
                null,   // 정렬 방식. null이면 정렬 안함
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }

        CodeInfo info = new CodeInfo();

        if (cursor.moveToFirst()) {
            int index_code = cursor.getColumnIndex(column_code);
            int index_name = cursor.getColumnIndex(column_name);

            String code = cursor.getString(index_code);
            String name = cursor.getString(index_name);

            info.setCode(code);
            info.setText(name);
        }

        cursor.close();
        return info;
    }

    @Override
    public void setCodeList(int code_type, ArrayList<CodeInfo> list) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        // 기존 내용 삭제
        db.delete(
                ConfigDBSettings.CODE_INFO.TABLE_NAME,
                "code_type=?",
                new String[]{ String.valueOf(code_type) });

        int COLUMN_NAME = ConfigDBSettings.NAME;
        int TYPE = ConfigDBSettings.CODE_INFO.INDEX_TYPE;
        int CODE = ConfigDBSettings.CODE_INFO.INDEX_CODE;
        int TEXT = ConfigDBSettings.CODE_INFO.INDEX_TEXT;

        String column_type = ConfigDBSettings.CODE_INFO.COLUMN[TYPE][COLUMN_NAME];
        String column_code = ConfigDBSettings.CODE_INFO.COLUMN[CODE][COLUMN_NAME];
        String column_text = ConfigDBSettings.CODE_INFO.COLUMN[TEXT][COLUMN_NAME];

        ContentValues values = new ContentValues();

        try {
            for(int i = 0; i < list.size(); i++) {
                CodeInfo data = list.get(i);

                values.put(column_type, data.getCodeInfoType());
                values.put(column_code, data.getCode());
                values.put(column_text, data.getText());

                db.insert(ConfigDBSettings.CODE_INFO.TABLE_NAME, null, values);

                values.clear();
            }

            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public ArrayList<CodeInfo> getCodeList(int code_type) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                false,
                ConfigDBSettings.CODE_INFO.TABLE_NAME,		// content://로 시작하는 content table uri - 테이블 명
                null,		// 테이블 column 명. null이면 모든 열을 출력.
                "code_type=?",		// where 절
                new String[]{ String.valueOf(code_type) },		// where 절에 들어갈 값
                null,   // 정렬 방식. null이면 정렬 안함
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }

        int COLUMN_NAME = ConfigDBSettings.NAME;
        int TYPE = ConfigDBSettings.CODE_INFO.INDEX_TYPE;
        int CODE = ConfigDBSettings.CODE_INFO.INDEX_CODE;
        int TEXT = ConfigDBSettings.CODE_INFO.INDEX_TEXT;

        String column_type = ConfigDBSettings.CODE_INFO.COLUMN[TYPE][COLUMN_NAME];
        String column_code = ConfigDBSettings.CODE_INFO.COLUMN[CODE][COLUMN_NAME];
        String column_text = ConfigDBSettings.CODE_INFO.COLUMN[TEXT][COLUMN_NAME];

        ArrayList<CodeInfo> list = new ArrayList<>();
        list.clear();

        if (cursor.moveToFirst()) {
            int index_type = cursor.getColumnIndex(column_type);
            int index_code = cursor.getColumnIndex(column_code);
            int index_text = cursor.getColumnIndex(column_text);

            do {
                CodeInfo info = new CodeInfo();

                int type = cursor.getInt(index_type);
                String code = cursor.getString(index_code);
                String text = cursor.getString(index_text);

                info.setCodeInfoType(type);
                info.setCode(code);
                info.setText(text);

                list.add(info);
            } while(cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

//    @Override
//    public String getType(@NonNull Uri uri) {
////        String type = null, DEFAULT = "vnd.android.cursor.dir/vnd." + getContext().getPackageName();
//        String type = null;
//        String DEFAULT = "vnd.android.cursor.dir/vnd." + mContext.getPackageName();
//        switch(mUriMatcher.match(uri)) {
//            case ConfigDBSettings.TABLE_INDEX_MEMBER :
//                type = DEFAULT + ".provider." + ConfigDBSettings.MEMBER_INFO.TABLE_NAME;
//                break;
//
//            case ConfigDBSettings.TABLE_INDEX_CODE_INFO :
//                type = DEFAULT + ".provider." + ConfigDBSettings.CODE_INFO.TABLE_NAME;
//                break;
//        }
//
//        return type;
//    }

    /**
     * 테이블에 대한 Column을 반환한다.
     * @param table_index 테이블 인덱스
     * @return Column 배열. 없을 경우 null.
     */
    private String[][] getColumns(int table_index) {
        String[][] column = null;

        switch(table_index) {
            case ConfigDBSettings.TABLE_INDEX_MEMBER :
                column = ConfigDBSettings.MEMBER_INFO.COLUMN;
                break;

            case ConfigDBSettings.TABLE_INDEX_CODE_INFO :
                column = ConfigDBSettings.CODE_INFO.COLUMN;
                break;
        }

        return column;
    }

    /**
     * 테이블을 생성한다.
     * @param db SQLite fd
     * @param table_index 테이블 인덱스
     */
    private void createTable(SQLiteDatabase db, int table_index) {
        ContentValues values = new ContentValues();
        String table_name = ConfigDBSettings.getTableName(table_index);
        String[][] column = getColumns(table_index);

        StringBuilder builder = new StringBuilder();

        // 가끔씩 테이블을 생성하지도 않았는데 생성했다고 죽는 경우가 있다.
        // 이를 방지하기 위해 SQL문에 IF NOT EXISTS를 삽입하여 테이블을 생성하자
        builder.append("CREATE TABLE IF NOT EXISTS " + table_name + " (" + ConfigDBSettings._ID + " INTEGER PRIMARY KEY,");

        for (int i = 0; i < column.length; i++) {
            builder.append(column[i][ConfigDBSettings.NAME] + " " + column[i][ConfigDBSettings.TYPE]);
            if (i < column.length - 1) {
                builder.append(",");
            }

            if (table_index == ConfigDBSettings.TABLE_INDEX_MEMBER) {
                values.put(column[i][ConfigDBSettings.NAME], "");
            }
            // 코드정보는 리스트이므로 일단 초기화는 보류
            else if (table_index == ConfigDBSettings.TABLE_INDEX_CODE_INFO) {
            }
//                // 카테고리는 리스트이므로 일단 초기화는 보류
//                else if (table_index == ConfigDBSettings.TABLE_INDEX_CATEGORY) {
//                }
            else {
                values.put(column[i][ConfigDBSettings.NAME], "");
            }
        }

        builder.append(");");

        try {
            db.execSQL(builder.toString());
        }
        catch (SQLException e) {
            return ;
        }

        db.insert(table_name, null, values);
        // 리스트의 경우에는 처음에 default 값을 넣지 말자.
//			if (table_index == ConfigDBSettings.TABLE_INDEX_APP_MANAGE) {
//			}
//			else {
//				db.insert(table_name, null, values);
//			}
    }
}
