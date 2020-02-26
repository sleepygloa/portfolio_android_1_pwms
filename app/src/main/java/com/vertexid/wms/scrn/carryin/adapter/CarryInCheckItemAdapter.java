package com.vertexid.wms.scrn.carryin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.CarryInInfo;

import java.util.ArrayList;

/**
 * 반입검수 리스트뷰 아이템 어댑터
 */
public class CarryInCheckItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<CarryInInfo> mArray = null;

    private IContentsListener mListener = null;

    public CarryInCheckItemAdapter(Context context, ArrayList<CarryInInfo> array, IContentsListener listener) {
        mContext = context;
        mArray = array;
        mListener = listener;
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
        LinearLayout layout_contents;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.carry_in_check_listview_item, null);

            CheckBox check_selected = (CheckBox) convertView.findViewById(R.id.carry_in_check_item_check_select);
            check_selected.setOnCheckedChangeListener(mCheckedChangeListener);

            layout_contents = (LinearLayout) convertView.findViewById(R.id.carry_in_check_item_layout_text);
            layout_contents.setOnClickListener(mClickListener);
        }

        layout_contents = (LinearLayout) convertView.findViewById(R.id.carry_in_check_item_layout_text);
        layout_contents.setTag(position);
        CheckBox check_selected = (CheckBox) convertView.findViewById(R.id.carry_in_check_item_check_select);
        TextView text_code = (TextView) convertView.findViewById(R.id.carry_in_check_item_text_goods_code);
        TextView text_name = (TextView) convertView.findViewById(R.id.carry_in_check_item_text_goods_name);
        TextView text_check = (TextView) convertView.findViewById(R.id.carry_in_check_item_text_check_count);

        CarryInInfo info = mArray.get(position);
        if (info == null) {
            return convertView;
        }

        boolean is_checked = info.isChecked();
        String code = info.getGoodsCode();
        String goods_name = info.getGoodsName();
        int check_count = info.getCheckCount();

        check_selected.setChecked(is_checked);
        check_selected.setTag(position);
        text_code.setText(code);
        text_name.setText(goods_name);
        text_check.setText(String.valueOf(check_count));

        return convertView;
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mListener != null) {
                mListener.onCheckBoxChecked((Integer) buttonView.getTag(), isChecked);
            }
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.carry_in_check_item_layout_text) {
                if (mListener != null) {
                    mListener.onClick((Integer) v.getTag());
                }
            }
        }
    };

    public interface IContentsListener {
        public void onCheckBoxChecked(int position, boolean isChecked);
        public void onClick(int position);
    }
}
