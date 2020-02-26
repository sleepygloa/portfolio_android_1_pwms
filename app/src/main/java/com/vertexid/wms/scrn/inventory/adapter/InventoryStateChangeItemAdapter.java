package com.vertexid.wms.scrn.inventory.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.config.ConfigManager;
import com.vertexid.wms.config.IConfigManager;
import com.vertexid.wms.info.CodeInfo;
import com.vertexid.wms.info.InventoryStateChangeInfo;

import java.util.ArrayList;

/**
 * 재고상태변경 목록 생성 어댑터
 */
public class InventoryStateChangeItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<InventoryStateChangeInfo> mArray = null;
    private ArrayList<CodeInfo> mArrayCodeInfo = null;

    public InventoryStateChangeItemAdapter(Context context, ArrayList<InventoryStateChangeInfo> array) {
        mContext = context;
        mArray = array;

        IConfigManager config = new ConfigManager(mContext);
        mArrayCodeInfo = config.getCodeList(CodeInfo.CODE_TYPE_GOODS_STATE);
        if (mArrayCodeInfo == null) {
            mArrayCodeInfo = new ArrayList<>();
            mArrayCodeInfo.clear();
        }
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
        TextView text_loc;
        TextView text_code;
        TextView text_name;
        TextView text_state;
        TextView text_count;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.inventory_state_change_listview_item, null);
        }

        text_loc = (TextView) convertView.findViewById(R.id.inventory_state_change_item_text_loc);
        text_code = (TextView) convertView.findViewById(R.id.inventory_state_change_item_text_code);
        text_name = (TextView) convertView.findViewById(R.id.inventory_state_change_item_text_name);
        text_state = (TextView) convertView.findViewById(R.id.inventory_state_change_item_text_state);
        text_count = (TextView) convertView.findViewById(R.id.inventory_state_change_item_text_count);

        InventoryStateChangeInfo info = mArray.get(position);
        if (info == null) {
            return convertView;
        }

        String loc = info.getFromLocation();
        String code = info.getGoodsCode();
        String name = info.getGoodsName();
        String state = info.getFromGoodState();
        int count = info.getInvenCount();
        int confirm_state = info.getConfirmState();

        String state_text = "";
        for (int i = 0 ; i < mArrayCodeInfo.size() ; i++) {
            CodeInfo code_info = mArrayCodeInfo.get(i);
            String state_code = code_info.getCode();
            if (state_code.equals(state)) {
                state_text = code_info.getText();
                break;
            }
        }

        text_loc.setText(loc);
        text_code.setText(code);
        text_name.setText(name);
        text_state.setText(state_text);
        text_count.setText(String.valueOf(count));

        if (confirm_state == InventoryStateChangeInfo.CONFIRM_COMPLETE) {
        }

        return convertView;
    }
}
