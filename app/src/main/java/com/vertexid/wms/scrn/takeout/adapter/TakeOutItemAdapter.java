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
 * 출고 목록 어댑터
 */
public class TakeOutItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<TakeOutInfo> mArray = null;

    public TakeOutItemAdapter(Context context, ArrayList<TakeOutInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.take_out_listview_item, null);
        }

        TextView text_goods_code = (TextView) convertView.findViewById(R.id.take_out_item_text_goods_code);
        TextView text_goods_name = (TextView) convertView.findViewById(R.id.take_out_item_text_goods_name);
        TextView text_direct_count = (TextView) convertView.findViewById(R.id.take_out_item_text_direction_count);
        TextView text_picking_count = (TextView) convertView.findViewById(R.id.take_out_item_text_picking_count);
        TextView text_picking_loc = (TextView) convertView.findViewById(R.id.take_out_item_text_picking_loc);
        TextView text_pallet_id = (TextView) convertView.findViewById(R.id.take_out_item_text_pallet_id);

        TakeOutInfo info = mArray.get(position);
        String goods_code = info.getGoods();
        String goods_name = info.getGoodsName();

        int direction_count = info.getDirectionCount();
        int picking_count = info.getPickingCount();
        String picking_loc = info.getPickingLoc();
        String pallet_id = info.getPalletID();

        text_goods_code.setText(goods_code);
        text_goods_name.setText(goods_name);
        text_direct_count.setText(String.valueOf(direction_count));
        text_picking_count.setText(String.valueOf(picking_count));
        text_picking_loc.setText(picking_loc);
        text_pallet_id.setText(pallet_id);

        return convertView;
    }
}
