package com.vertexid.wms.scrn.inventory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.InvenPltInfo;

import java.util.ArrayList;

/**
 * PLT 분할 항목 생성 어댑터
 */
public class InventoryPltDivideItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<InvenPltInfo> mArray = null;

    public InventoryPltDivideItemAdapter(Context context, ArrayList<InvenPltInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.inventory_plt_divide_listview_item, null);
        }

        TextView text_code = (TextView) convertView.findViewById(R.id.inventory_plt_divide_item_text_code);
        TextView text_name = (TextView) convertView.findViewById(R.id.inventory_plt_divide_item_text_name);
        TextView text_lot_id = (TextView) convertView.findViewById(R.id.inventory_plt_divide_item_text_lot_id);
        TextView text_box_count = (TextView) convertView.findViewById(R.id.inventory_plt_divide_item_text_box_count);
        TextView text_ea_count = (TextView) convertView.findViewById(R.id.inventory_plt_divide_item_text_ea_count);

        InvenPltInfo info = mArray.get(position);
        if (info == null) {
            return convertView;
        }

        String code = info.getGoodsCode();
        String name = info.getGoodsName();
        String lot_id = info.getLotId();
        int box_count = info.getInvenBoxCount();
        int ea_count = info.getInvenEaCount();
        int state = info.getState();

        text_code.setText(code);
        text_name.setText(name);
        text_lot_id.setText(lot_id);
        text_box_count.setText(String.valueOf(box_count));
        text_ea_count.setText(String.valueOf(ea_count));

        if (state == InvenPltInfo.STATE_DONE) {
        }
        else if (state == InvenPltInfo.STATE_SELECT) {
        }

        return convertView;
    }
}
