package com.vertexid.wms.scrn.inventory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.InvenInquiryInfo;

import java.util.ArrayList;

/**
 * 재고조사 리스트뷰 항목 어댑터
 */
public class InventoryInquiryItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<InvenInquiryInfo> mArray = null;

    public InventoryInquiryItemAdapter(Context context, ArrayList<InvenInquiryInfo> array) {
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
        TextView text_loc;
        TextView text_goods_code;
        TextView text_goods_name;
        TextView text_order_count;
        TextView text_plt_id;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.inventory_inquiry_listview_item, null);
        }

        text_loc = (TextView) convertView.findViewById(R.id.inventory_inquiry_item_text_loc);
        text_goods_code = (TextView) convertView.findViewById(R.id.inventory_inquiry_item_text_goods_code);
        text_goods_name = (TextView) convertView.findViewById(R.id.inventory_inquiry_item_text_goods_name);
        text_order_count = (TextView) convertView.findViewById(R.id.inventory_inquiry_item_text_order_count);
        text_plt_id = (TextView) convertView.findViewById(R.id.inventory_inquiry_item_text_plt_id);

        InvenInquiryInfo info = mArray.get(position);
        if (info == null) {
            return convertView;
        }

        String loc = info.getLocation();
        String code = info.getGoodsCode();
        String name = info.getGoodsName();
        String plt_id = info.getPltId();
        int count = info.getInvenCount();
        int process_state = info.getProcessState();

        text_loc.setText(loc);
        text_goods_code.setText(code);
        text_goods_name.setText(name);
        text_order_count.setText(String.valueOf(count));
        text_plt_id.setText(plt_id);

        if (process_state == InvenInquiryInfo.STATE_DONE) {
        }

        return convertView;
    }
}
