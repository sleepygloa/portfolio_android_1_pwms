package com.vertexid.wms.scrn.carryout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.CarryOutInfo;

import java.util.ArrayList;

/**
 * 반출 목록 어댑터
 */
public class CarryOutItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<CarryOutInfo> mArray = null;

    public CarryOutItemAdapter(Context context, ArrayList<CarryOutInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.carry_out_listview_item, null);
        }

        TextView text_code = (TextView) convertView.findViewById(R.id.carry_out_item_text_goods_code);
        TextView text_name = (TextView) convertView.findViewById(R.id.carry_out_item_text_goods_name);
        TextView text_order_count = (TextView) convertView.findViewById(R.id.carry_out_item_text_order_count);
        TextView text_loc = (TextView) convertView.findViewById(R.id.carry_out_item_text_loc);
        TextView text_plt = (TextView) convertView.findViewById(R.id.carry_out_item_text_plt);

        CarryOutInfo info = mArray.get(position);
        String code = info.getGoodsCode();
        String goods_name = info.getGoodsName();
        int order_count = info.getOrderCount();
        String loc = info.getLocation();
        String plt_id = info.getPltId();
        int confirm_state = info.getConfirmState();

        text_code.setText(code);
        text_name.setText(goods_name);
        text_order_count.setText(String.valueOf(order_count));
        text_loc.setText(loc);
        text_plt.setText(plt_id);

        if (confirm_state == CarryOutInfo.CONFRIM_STATE_COMPLETE) {
        }

        return convertView;
    }
}
