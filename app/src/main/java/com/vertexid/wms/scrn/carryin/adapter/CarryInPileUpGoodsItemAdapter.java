package com.vertexid.wms.scrn.carryin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.CarryInInfo;

import java.util.ArrayList;

/**
 * 적치제품 리스트 항목 생성
 */
public class CarryInPileUpGoodsItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<CarryInInfo> mArray = null;

    public CarryInPileUpGoodsItemAdapter(Context context, ArrayList<CarryInInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.carry_in_goods_listview_item, null);
        }

        TextView text_location = (TextView) convertView.findViewById(R.id.carry_in_pile_up_goods_item_text_loction);
        TextView text_code = (TextView) convertView.findViewById(R.id.carry_in_pile_up_goods_item_text_goods_code);
        TextView text_name = (TextView) convertView.findViewById(R.id.carry_in_pile_up_goods_item_text_goods_name);
        TextView text_plt_id = (TextView) convertView.findViewById(R.id.carry_in_pile_up_goods_item_text_plt_id);
        TextView text_count = (TextView) convertView.findViewById(R.id.carry_in_pile_up_goods_item_text_count);

        CarryInInfo info = mArray.get(position);
        String loc = info.getLocation();
        String code = info.getGoodsCode();
        String name = info.getGoodsName();
        String plt_id = info.getPltId();
        int order_count = info.getOrderCount();

        text_location.setText(loc);
        text_code.setText(code);
        text_name.setText(name);
        text_plt_id.setText(plt_id);
        text_count.setText(String.valueOf(order_count));

        return convertView;
    }
}
