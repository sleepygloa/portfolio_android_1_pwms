package com.vertexid.wms.scrn.inventory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.InvenMoveInfo;

import java.util.ArrayList;

/**
 * 임의재고 이동 LOC 리스트 항목 생성
 */
public class TempMoveItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<InvenMoveInfo> mArray = null;

    public TempMoveItemAdapter(Context context, ArrayList<InvenMoveInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.inventory_temp_move_loc_listview_item, null);
        }

        InvenMoveInfo info = mArray.get(position);
        if (info == null) {
            return convertView;
        }

        String loc = info.getFromLocation();
        String plt_id = info.getFromPltId();
        int box_count = info.getInvenBoxCount();
        int ea_count = info.getInvenEaCount();

        TextView text_loc = (TextView) convertView.findViewById(R.id.inventory_temp_move_loc_item_text_loc);
        TextView text_plt_id = (TextView) convertView.findViewById(R.id.inventory_temp_move_loc_item_text_plt_id);
        TextView text_box_count = (TextView) convertView.findViewById(R.id.inventory_temp_move_loc_item_text_box_count);
        TextView text_ea_count = (TextView) convertView.findViewById(R.id.inventory_temp_move_loc_item_text_ea_count);

        text_loc.setText(loc);
        text_plt_id.setText(plt_id);
        text_box_count.setText(String.valueOf(box_count));
        text_ea_count.setText(String.valueOf(ea_count));

        return convertView;
    }
}
