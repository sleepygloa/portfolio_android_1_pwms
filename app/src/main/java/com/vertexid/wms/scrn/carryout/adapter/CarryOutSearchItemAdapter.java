package com.vertexid.wms.scrn.carryout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.CarryNumberInfo;

import java.util.ArrayList;

/**
 * 반출번호 조회 리스트 항목 어댑터
 */
public class CarryOutSearchItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<CarryNumberInfo> mArray = null;

    public CarryOutSearchItemAdapter(Context context, ArrayList<CarryNumberInfo> array) {
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.carry_out_search_listview_item, null);
        }

        TextView text_number = (TextView) convertView.findViewById(R.id.carry_out_search_item_text_number);
        TextView text_supplier = (TextView) convertView.findViewById(R.id.carry_out_search_item_text_supplier);

        CarryNumberInfo info = mArray.get(position);
        String number = info.getCarryNumber();
        String supplier = info.getSource();
        text_number.setText(number);
        text_supplier.setText(supplier);

        return convertView;
    }
}
