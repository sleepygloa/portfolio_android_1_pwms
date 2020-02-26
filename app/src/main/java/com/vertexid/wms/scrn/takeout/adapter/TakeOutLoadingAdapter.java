package com.vertexid.wms.scrn.takeout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.TakeOutInfo;
import com.vertexid.wms.info.TakeOutLoadingInfo;

import java.util.ArrayList;

/**
 * 출고상차 목록 어댑터
 */
public class TakeOutLoadingAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<TakeOutLoadingInfo> mArray = null;

    public TakeOutLoadingAdapter(Context context, ArrayList<TakeOutLoadingInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.take_out_loading_listview_item, null);
        }

        TextView text_take_out_number = (TextView) convertView.findViewById(R.id.take_out_loading_item_text_take_out_number);
        TextView text_delievery_address = (TextView) convertView.findViewById(R.id.take_out_loading_item_text_delievery_address);
        TextView text_car_number = (TextView) convertView.findViewById(R.id.take_out_loading_item_text_car_number);

        TakeOutLoadingInfo info = mArray.get(position);
        String take_out_number = info.getTakeOutNumber();
        String delivery_address = info.getDelivery();
        String car_number = info.getCarNumber();

        text_take_out_number.setText(take_out_number);
        text_delievery_address.setText(delivery_address);
        text_car_number.setText(car_number);

        return convertView;
    }
}
