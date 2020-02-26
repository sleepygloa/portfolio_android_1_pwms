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
 * 출고상차 상단 목록 어댑터
 */
public class TakeOutLoadingTopAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<TakeOutInfo> mArray = null;

    public TakeOutLoadingTopAdapter(Context context, ArrayList<TakeOutInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.take_out_loading_listview_top_item, null);
        }

        TextView text_product = (TextView) convertView.findViewById(R.id.take_out_loading_top_item_text_product);
        TextView text_product_name = (TextView) convertView.findViewById(R.id.take_out_loading_top_item_text_product_name);
        TextView text_picking_box = (TextView) convertView.findViewById(R.id.take_out_loading_top_item_text_picking_box);
        TextView text_picking_ea = (TextView) convertView.findViewById(R.id.take_out_loading_top_item_text_picking_ea);

        TakeOutInfo info = mArray.get(position);
        String product = info.getGoods();
        String product_name = info.getGoodsName();
        String picking_box = String.valueOf(info.getPickingBox());
        String picking_ea = String.valueOf(info.getPickingEa());

        text_product.setText(product);
        text_product_name.setText(product_name);
        text_picking_box.setText(picking_box);
        text_picking_ea.setText(picking_ea);

        return convertView;
    }
}
