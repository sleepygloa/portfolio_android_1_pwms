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
 * 분배 목록 어댑터
 */
public class TakeOutDistributionItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<TakeOutInfo> mArray = null;

    public TakeOutDistributionItemAdapter(Context context, ArrayList<TakeOutInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.take_out_distribution_listview_item, null);
        }

        TextView text_goods_code = (TextView) convertView.findViewById(R.id.take_out_ditribution_item_text_goods_code);
        TextView text_goods_name = (TextView) convertView.findViewById(R.id.take_out_ditribution_item_text_goods_name);
        TextView text_picking_box_count = (TextView) convertView.findViewById(R.id.take_out_ditribution_item_item_text_picking_box_count);
        TextView text_picking_ea_count = (TextView) convertView.findViewById(R.id.take_out_ditribution_item_item_text_picking_ea_count);

        TakeOutInfo info = mArray.get(position);
        String goods_code = info.getGoods();
        String goods_name = info.getGoodsName();
        int picking_box_count = info.getPickingBox();
        int picking_ea_count = info.getPickingEa();

        text_goods_code.setText(goods_code);
        text_goods_name.setText(goods_name);
        text_picking_box_count.setText(String.valueOf(picking_box_count));
        text_picking_ea_count.setText(String.valueOf(picking_ea_count));

        return convertView;
    }
}
