package com.vertexid.wms.scrn.warehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.WareHouseNumberInfo;

import java.util.ArrayList;

/**
 * 입고번호 조회 결과 아이템 어댑터
 */
public class WareHouseNumberItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<WareHouseNumberInfo> mArray = null;

    public WareHouseNumberItemAdapter(Context context, ArrayList<WareHouseNumberInfo> array) {
        mContext = context;
        mArray = array;
    }

    @Override
    public int getCount() {
        return mArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text_number;
        TextView text_supplier;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.warehouse_number_listview_item, null);
        }

        text_number = (TextView) convertView.findViewById(R.id.warehouse_number_item_text_number);
        text_supplier = (TextView) convertView.findViewById(R.id.warehouse_number_item_text_supplier);

        WareHouseNumberInfo info = mArray.get(position);
        String number = info.getWareHouseNumber();
        String supplier = info.getSupplier();

        text_number.setText(number);
        text_supplier.setText(supplier);

        return convertView;
    }
}
