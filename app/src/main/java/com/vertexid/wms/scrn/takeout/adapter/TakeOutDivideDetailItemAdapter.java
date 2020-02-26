package com.vertexid.wms.scrn.takeout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.TakeOutInfo;

import java.util.ArrayList;

/**
 * 분배 상세 목록 어댑터
 */
public class TakeOutDivideDetailItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<TakeOutInfo> mArray = null;

    public TakeOutDivideDetailItemAdapter(Context context, ArrayList<TakeOutInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.take_out_distribution_list_listview_item, null);
        }

        TextView text_delivery = (TextView) convertView.findViewById(R.id.take_out_divide_detail_item_text_delivery);
        TextView text_box_count = (TextView) convertView.findViewById(R.id.take_out_divide_detail_item_text_picking_box_count);
        TextView text_ea_count = (TextView) convertView.findViewById(R.id.take_out_divide_detail_item_text_picking_ea_count);

        TakeOutInfo info = mArray.get(position);
        if (info == null) {
            return convertView;
        }

        String delivery = info.getDeliveryAddress();
        int box_count = info.getPickingBox();
        int ea_count = info.getPickingEa();

        text_delivery.setText(delivery);
        text_box_count.setText(String.valueOf(box_count));
        text_ea_count.setText(String.valueOf(ea_count));

        return convertView;
    }
}
