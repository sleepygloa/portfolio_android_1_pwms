package com.vertexid.wms.scrn.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.InquiryInfo;

import java.util.ArrayList;

/**
 * 제품조회 목록
 */
public class GoodsInquiryItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<InquiryInfo> mArray = null;

    public GoodsInquiryItemAdapter(Context context, ArrayList<InquiryInfo> array) {
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
        TextView text_location;
        TextView text_inventory_count;
        TextView text_goods_state;
        TextView text_production_date;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.goods_inquiry_listview_item, null);
        }

        text_location = (TextView) convertView.findViewById(R.id.goods_inquiry_item_text_location);
        text_inventory_count = (TextView) convertView.findViewById(R.id.goods_inquiry_item_text_inventory_count);
        text_goods_state = (TextView) convertView.findViewById(R.id.goods_inquiry_item_text_goods_state);
        text_production_date = (TextView) convertView.findViewById(R.id.goods_inquiry_item_text_production_date);

        InquiryInfo info = mArray.get(position);
        String location = info.getLocation();
        int count = info.getInvenCount();
        String state = info.getGoodsState();
        String date = info.getMakeDates();

        text_location.setText(location);
        text_inventory_count.setText(String.valueOf(count));
        text_goods_state.setText(state);
        text_production_date.setText(date);

        return convertView;
    }
}
