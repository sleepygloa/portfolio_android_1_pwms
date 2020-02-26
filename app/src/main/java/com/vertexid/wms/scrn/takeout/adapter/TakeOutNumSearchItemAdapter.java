package com.vertexid.wms.scrn.takeout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.scrn.takeout.info.TakeOutNumberInfo;

import java.util.ArrayList;

/**
 * 출고번호 조회 어댑터
 */
public class TakeOutNumSearchItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<TakeOutNumberInfo> mArray = null;

    public TakeOutNumSearchItemAdapter(Context context, ArrayList<TakeOutNumberInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.take_out_num_search_listview_item, null);
        }

        TextView text_number = (TextView) convertView.findViewById(R.id.take_out_num_search_item_text_take_out_number);
        TextView text_delivery = (TextView) convertView.findViewById(R.id.take_out_num_search_item_text_delivery_address);

        TakeOutNumberInfo info = mArray.get(position);
        String number = info.getTakeOutNumber();
        String delivery = info.getDestination();

        text_number.setText(number);
        text_delivery.setText(delivery);

        return convertView;
    }
}
