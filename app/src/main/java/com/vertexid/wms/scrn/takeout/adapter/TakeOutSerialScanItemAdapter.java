package com.vertexid.wms.scrn.takeout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.SerialScanInfo;

import java.util.ArrayList;

/**
 * 시리얼 스캔 리스트 항목 어댑터
 */
public class TakeOutSerialScanItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<SerialScanInfo> mArray = null;

    private CheckBoxListener mCheckBoxListener = null;

    public TakeOutSerialScanItemAdapter(Context context, ArrayList<SerialScanInfo> array, CheckBoxListener listener) {
        mContext = context;
        mArray = array;
        mCheckBoxListener = listener;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.serial_scan_listview_item, null);

            CheckBox check_remove = (CheckBox) convertView.findViewById(R.id.serial_scan_item_check_remove);
            check_remove.setOnCheckedChangeListener(mCheckedChangeListener);
        }

        CheckBox check_remove = (CheckBox) convertView.findViewById(R.id.serial_scan_item_check_remove);
        check_remove.setTag(position);
        TextView text_serial_number = (TextView) convertView.findViewById(R.id.serial_scan_item_text_serial_number);

        SerialScanInfo info = mArray.get(position);
        if (info == null) {
            return convertView;
        }

        boolean is_checked = mArray.get(position).isRemoveChecked();
        String number = mArray.get(position).getSerialNumber();
        check_remove.setChecked(is_checked);
        text_serial_number.setText(number);

        return convertView;
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mCheckBoxListener != null) {
                int position = (Integer) buttonView.getTag();
                mCheckBoxListener.onCheckBoxChecked(position, isChecked);
            }
        }
    };

    public interface CheckBoxListener {
        public void onCheckBoxChecked(int position, boolean isChecked);
    }
}
