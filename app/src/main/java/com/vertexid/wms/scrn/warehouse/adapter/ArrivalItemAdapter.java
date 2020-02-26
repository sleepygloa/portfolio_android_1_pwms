package com.vertexid.wms.scrn.warehouse.adapter;

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
import com.vertexid.wms.info.WareHouseInfo;

import java.util.ArrayList;

/**
 * 입하검수 목록 어댑터
 */
public class ArrivalItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<WareHouseInfo> mArray = null;

    private ICheckBoxListener mListener = null;
    private IContentsClickListener mContentsClickListener = null;

    public ArrivalItemAdapter(Context context, ArrayList<WareHouseInfo> array, ICheckBoxListener listener, IContentsClickListener contentsClickListener) {
        mContext = context;
        mArray = array;
        mListener = listener;
        mContentsClickListener = contentsClickListener;
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
        CheckBox check_selected;
        TextView text_goods;
        TextView text_goods_name;
        TextView text_approval_count;
        TextView text_check_count;

        LinearLayout layout_contents;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.arrival_listview_item, null);
            check_selected = (CheckBox) convertView.findViewById(R.id.arrival_item_check_select);
            check_selected.setOnCheckedChangeListener(mCheckedChangeListener);
            layout_contents = (LinearLayout) convertView.findViewById(R.id.arrival_item_layout_text);
            layout_contents.setOnClickListener(mClickListener);
        }

        layout_contents = (LinearLayout) convertView.findViewById(R.id.arrival_item_layout_text);
        layout_contents.setTag(position);
        check_selected = (CheckBox) convertView.findViewById(R.id.arrival_item_check_select);
        text_goods = (TextView) convertView.findViewById(R.id.arrival_item_text_goods);
        text_goods_name = (TextView) convertView.findViewById(R.id.arrival_item_text_goods_name);
        text_approval_count = (TextView) convertView.findViewById(R.id.arrival_item_text_approval_count);
        text_check_count = (TextView) convertView.findViewById(R.id.arrival_item_text_check_count);

        WareHouseInfo info = mArray.get(position);
        boolean is_selected = info.isSelected();
        String goods = info.getGoods();
        String goods_name = info.getGoodsName();
        int approval_count = info.getApprovalCount();
        int check_count = info.getCheckCount();

        check_selected.setChecked(is_selected);
        check_selected.setTag(position);
        text_goods.setText(goods);
        text_goods_name.setText(goods_name);
        text_approval_count.setText(String.valueOf(approval_count));
        text_check_count.setText(String.valueOf(check_count));

        int state = info.getArrivalCheckState();
        // 검수 완료 상태
        if (state == WareHouseInfo.CHECK_COMPLETE) {
        }
        // 확정 완료 상태
        else if (state == WareHouseInfo.CONFIRM_COMPLETE) {
        }

        return convertView;
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mListener != null) {
                int position = (Integer) buttonView.getTag();
                mListener.onCheckBoxChecked(position, isChecked);
            }
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.arrival_item_layout_text) {
                if (mContentsClickListener != null) {
                    int position = (Integer) v.getTag();
                    mContentsClickListener.onClick(position);
                }
            }
        }
    };

    public interface ICheckBoxListener {
        public void onCheckBoxChecked(int position, boolean isChecked);
    }

    public interface IContentsClickListener {
        public void onClick(int position);
    }
}
