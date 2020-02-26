package com.vertexid.wms.scrn.carryin.adapter;

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
 * 반입검수 관련 반입번호 검색 리스트뷰 어댑터
 */
public class CarryInCheckSearchItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<CarryNumberInfo> mArray = null;

    public CarryInCheckSearchItemAdapter(Context context, ArrayList<CarryNumberInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.carry_in_check_search_listview_item, null);
        }

        TextView text_number = (TextView) convertView.findViewById(R.id.carry_in_check_search_item_text_number);
        TextView text_supplier = (TextView) convertView.findViewById(R.id.carry_in_check_search_item_text_supplier);

        CarryNumberInfo info = mArray.get(position);
        if (info == null) {
            return convertView;
        }

        String number = info.getCarryNumber();
        String supplier = info.getSource();

        text_number.setText(number);
        text_supplier.setText(supplier);

        return convertView;
    }
}
