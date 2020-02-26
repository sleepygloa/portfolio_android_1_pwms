package com.vertexid.wms.scrn.warehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.WareHouseInfo;

import java.util.ArrayList;

/**
 * 적치화면의 제품조회 화면 리스트 항목 생성 어댑터
 */
public class PileUpGoodsItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<WareHouseInfo> mArray = null;

    public PileUpGoodsItemAdapter(Context context, ArrayList<WareHouseInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.pile_goods_listview_item, null);
        }

        TextView text_location = (TextView) convertView.findViewById(R.id.ware_pile_up_goods_item_text_loction);
        TextView text_code = (TextView) convertView.findViewById(R.id.ware_pile_up_goods_item_text_goods_code);
        TextView text_plt_id = (TextView) convertView.findViewById(R.id.ware_pile_up_goods_item_text_plt_id);
        TextView text_count = (TextView) convertView.findViewById(R.id.ware_pile_up_goods_item_text_count);

        WareHouseInfo info = mArray.get(position);
        String location = info.getLocation();
        String code = info.getGoods();
        String plt_id = info.getPltId();
        int count = info.getOrderCount();

        text_location.setText(location);
        text_code.setText(code);
        text_plt_id.setText(plt_id);
        text_count.setText(String.valueOf(count));

        return convertView;
    }
}
