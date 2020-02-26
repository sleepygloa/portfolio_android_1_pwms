package com.vertexid.wms.scrn.takeout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.TakeOutInfo;
import com.vertexid.wms.scrn.takeout.info.WaveNumberInfo;

import java.util.ArrayList;

/**
 * 출고번호 조회 어댑터
 */
public class TakeOutWaveNumSearchItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<WaveNumberInfo> mArray = null;

    public TakeOutWaveNumSearchItemAdapter(Context context, ArrayList<WaveNumberInfo> array) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.take_out_wave_num_search_listview_item, null);
        }

        TextView text_wave_number = (TextView) convertView.findViewById(R.id.take_out_wave_num_search_item_text_wave_number);
        TextView text_standard_number = (TextView) convertView.findViewById(R.id.take_out_wave_num_search_item_text_standard_number);
        TextView text_standard_desc = (TextView) convertView.findViewById(R.id.take_out_wave_num_search_item_text_standard_desc);

        WaveNumberInfo info = mArray.get(position);
        String wave_number = info.getWaveNumber();
        String standard_number = info.getStandardNumber();
        String standrad_desc = info.getStandardText();

        text_wave_number.setText(wave_number);
        text_standard_number.setText(standard_number);
        text_standard_desc.setText(standrad_desc);

        return convertView;
    }
}
