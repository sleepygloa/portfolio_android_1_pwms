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
 * 피킹(총량) 리스트 항목 생성 어댑터
 */
public class PickingTotalItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<TakeOutInfo> mArray = null;

    public PickingTotalItemAdapter(Context context, ArrayList<TakeOutInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.picking_total_listview_item, null);
        }

        TextView text_goods_code = (TextView) convertView.findViewById(R.id.picking_total_item_text_goods_code);
        TextView text_goods_name = (TextView) convertView.findViewById(R.id.picking_total_item_text_goods_name);
        TextView text_order_count = (TextView) convertView.findViewById(R.id.picking_total_item_text_order_count);
        TextView text_picking_count = (TextView) convertView.findViewById(R.id.picking_total_item_text_picking_count);
        TextView text_location = (TextView) convertView.findViewById(R.id.picking_total_item_text_location);
        TextView text_plt_id = (TextView) convertView.findViewById(R.id.picking_total_item_text_plt_id);

        TakeOutInfo info = mArray.get(position);
        String goods_code = info.getGoods();
        String goods_name = info.getGoodsName();
        int order_count = info.getDirectionCount();
        int picking_count = info.getPickingCount();
        String location = info.getPickingLoc();
        String plt_id = info.getPalletID();

        text_goods_code.setText(goods_code);
        text_goods_name.setText(goods_name);
        text_order_count.setText(String.valueOf(order_count));
        text_picking_count.setText(String.valueOf(picking_count));
        text_location.setText(location);
        text_plt_id.setText(plt_id);

        return convertView;
    }
}
