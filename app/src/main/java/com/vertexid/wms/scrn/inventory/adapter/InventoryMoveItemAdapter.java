package com.vertexid.wms.scrn.inventory.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.InvenMoveInfo;

import java.util.ArrayList;

/**
 * 재고이동 목록 어댑터
 */
public class InventoryMoveItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<InvenMoveInfo> mArray = null;

    public InventoryMoveItemAdapter(Context context, ArrayList<InvenMoveInfo> array) {
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
        TextView text_from_loc;
        TextView text_goods;
        TextView text_goods_name;
        TextView text_order_count;
        TextView text_to_loc;
//        TextView text_plt_id;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.inventory_move_listview_item, null);
        }

        text_from_loc = (TextView) convertView.findViewById(R.id.inventory_move_item_text_from_loc);
        text_goods = (TextView) convertView.findViewById(R.id.inventory_move_item_text_goods);
        text_goods_name = (TextView) convertView.findViewById(R.id.inventory_move_item_text_goods_name);
        text_order_count = (TextView) convertView.findViewById(R.id.inventory_move_item_text_order_count);
        text_to_loc = (TextView) convertView.findViewById(R.id.inventory_move_item_text_to_loc);
//        text_plt_id = (TextView) convertView.findViewById(R.id.inventory_move_item_text_plt_id);

        InvenMoveInfo info = mArray.get(position);
        if (info == null) {
            return convertView;
        }

        String from_loc = info.getFromLocation();
        String goods = info.getGoodsCode();
        String goods_name = info.getGoodsName();
        int order_count = info.getOrderCount();
        String to_location = info.getToLocation();
//        String plt_id = info.getToPltId();

        text_from_loc.setText(from_loc);
        text_goods.setText(goods);
        text_goods_name.setText(goods_name);
        text_order_count.setText(String.valueOf(order_count));
        text_to_loc.setText(to_location);
//        text_plt_id.setText(plt_id);

        int process_state = info.getProcessState();
        if (process_state == InvenMoveInfo.STATE_DONE) {
//            convertView.setBackgroundColor(Color.GRAY);
        }

        return convertView;
    }
}
