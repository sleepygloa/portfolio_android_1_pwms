package com.vertexid.wms.scrn.inventory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;

import java.util.ArrayList;

/**
 * 파렛트 병합 리스트 항목 생성 어댑터
 */

public class InventoryPltMergeItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<String> mArray = null;

    public InventoryPltMergeItemAdapter(Context context, ArrayList<String> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.inventory_plt_merge_listview_item, null);
        }

        TextView text_from_plt_id = (TextView) convertView.findViewById(R.id.inventory_plt_merge_item_text_from_plt_id);
        text_from_plt_id.setText(mArray.get(position));

        return convertView;
    }
}
