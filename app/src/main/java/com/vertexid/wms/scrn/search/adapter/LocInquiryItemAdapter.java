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
 * 제품위치 목록
 */
public class LocInquiryItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<InquiryInfo> mArray = null;

    public LocInquiryItemAdapter(Context context, ArrayList<InquiryInfo> array) {
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
        TextView text_goods;
        TextView text_goods_state;
        TextView text_inventory_count;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.loc_inquiry_listview_item, null);
        }

        text_goods = (TextView) convertView.findViewById(R.id.loc_inquiry_item_text_goods);
        text_goods_state = (TextView) convertView.findViewById(R.id.loc_inquiry_item_text_goods_state);
        text_inventory_count = (TextView) convertView.findViewById(R.id.loc_inquiry_item_text_inventory_count);

        InquiryInfo info = mArray.get(position);
        String goods = info.getGoodsName();
        int count = info.getInvenCount();
        String state = info.getGoodsState();

        text_goods.setText(goods);
        text_goods_state.setText(state);
        text_inventory_count.setText(String.valueOf(count));

        return convertView;
    }
}
