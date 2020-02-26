package com.vertexid.wms.config.sqlite;

import android.provider.BaseColumns;

/**
 * DB 에서 사용할 각종 값들을 정의한다.
 */
public class ConfigDBSettings implements BaseColumns {
    /** 스키마 */
    public static final String SCHEME			= "content://";
    /** PATH 정보 */
    public static final String PATH_INFO		= "/WmsConfig/";
    /** PATH 아이디 */
    public static final String PATH_INFO_ID		= "/WmsDB";
    /** AUTH */
    public static final String AUTH_TAIL		= ".ConfigInfo";

    /** Column의 이름 */
    public static final int NAME				= 0;
    /** Column의 종류 (TEXT, INTEGER등등) */
    public static final int TYPE				= 1;

    /** 각 테이블 Column의 기본 인덱스 */
    protected static final int INDEX_BASE		= 0;

    /** 사용자 정보 테이블 */
    public static final int TABLE_INDEX_MEMBER		= 0;
    /** 회사, 물류센터, 고객사, 제품상태 정보 목록 테이블 */
    public static final int TABLE_INDEX_CODE_INFO		= TABLE_INDEX_MEMBER + 1;
    /** 존재하지 않는 테이블 */
    public static final int TABLE_INDEX_MAX			= TABLE_INDEX_CODE_INFO + 1;

    /**
     * 사용자 정보를 담는 테이블에 대한 정의
     */
    public static class MEMBER_INFO {
        /** 사용자 정보를 담는 테이블의 이름 */
        public static final String TABLE_NAME = "member_info";

        public static final String[][] COLUMN = {
                {"user_id", "TEXT"},
                {"user_password", "TEXT"},
                {"user_name", "TEXT"},
                {"user_company_code", "TEXT"},
                {"user_company_name", "TEXT"},
                {"user_center_code", "TEXT"},
                {"user_center_name", "TEXT"},
                {"user_customer_code", "TEXT"},
                {"user_customer_name", "TEXT"},
        };

        public static final int INDEX_USER_ID		= INDEX_BASE;
        public static final int INDEX_USER_PASSWORD	= INDEX_USER_ID + 1;
        public static final int INDEX_USER_NAME 	= INDEX_USER_PASSWORD + 1;
        public static final int INDEX_COMPANY_CODE	= INDEX_USER_NAME + 1;
        public static final int INDEX_COMPANY_NAME	= INDEX_COMPANY_CODE + 1;
        public static final int INDEX_CENTER_CODE	= INDEX_COMPANY_NAME + 1;
        public static final int INDEX_CENTER_NAME	= INDEX_CENTER_CODE + 1;
        public static final int INDEX_CUSTOMER_CODE	= INDEX_CENTER_NAME + 1;
        public static final int INDEX_CUSTOMER_NAME	= INDEX_CUSTOMER_CODE + 1;
    }

    /**
     * 고객사 정보, 회사 정보, 물류센터 정보, 제품 상태 정보를 담는 테이블에 대한 정의
     */
    public static class CODE_INFO {
        /** 고객사 정보를 담는 테이블의 이름 */
        public static final String TABLE_NAME = "code_info";

        public static final String[][] COLUMN = {
                {"code_type", "INTEGER"},
                {"code_code", "TEXT"},
                {"code_text", "TEXT"},
        };

        public static final int INDEX_TYPE = INDEX_BASE;
        public static final int INDEX_CODE	= INDEX_TYPE + 1;
        public static final int INDEX_TEXT	= INDEX_CODE + 1;
    };

    /**
     * Column 이 존재하는지를 반환한다.
     * @param table table 인덱스.
     * @param column Column 이름
     * @return true : 있음, false : 없음.
     */
    public static boolean isExist(int table, String column) {
        if (column == null) {
            return false;
        }

        boolean ret = false;
        String[][] _column = null;

        switch(table) {
            case TABLE_INDEX_MEMBER :
                _column = MEMBER_INFO.COLUMN;
                break;

            case TABLE_INDEX_CODE_INFO :
                _column = CODE_INFO.COLUMN;
                break;
        }

        if (_column == null) {
            return false;
        }

        for (int i = 0; i < _column.length; i++) {
            if (_column[i][NAME].equals(column)) {
                ret = true;
                break;
            }
        }

        return ret;
    }

    /**
     * 테이블 이름을 반환한다.
     * @param table_index 테이블 인덱스
     * <ul>
     * 	<li/> {@link ConfigDBSettings#TABLE_INDEX_MEMBER}
     * </ul>
     * @return 테이블 이름. 없을 경우 null.
     */
    public static String getTableName(int table_index) {
        String table = null;

        switch (table_index) {
            case TABLE_INDEX_MEMBER :
                table = MEMBER_INFO.TABLE_NAME;
                break;

            case TABLE_INDEX_CODE_INFO :
                table = CODE_INFO.TABLE_NAME;
                break;
        }

        return table;
    }

    /**
     * 칼럼 명 반환
     * @param table 테이블 인덱스
     * @param column_index 칼럼 인덱스
     * @return 칼럼 명
     */
    public static String getColumnName(int table, int column_index) {
        String[][] _column = null;

        switch(table) {
            case TABLE_INDEX_MEMBER :
                _column = MEMBER_INFO.COLUMN;
                break;

            case TABLE_INDEX_CODE_INFO :
                _column = CODE_INFO.COLUMN;
                break;
        }

        return _column[column_index][NAME];
    }
}
