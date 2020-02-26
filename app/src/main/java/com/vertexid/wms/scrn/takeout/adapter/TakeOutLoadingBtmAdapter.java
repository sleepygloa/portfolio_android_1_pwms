package com.vertexid.wms.scrn.takeout.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.vertexid.wms.R;
import com.vertexid.wms.info.TakeOutInfo;
import com.vertexid.wms.utils.VLog;

import java.util.ArrayList;

/**
 * 출고상차 하단 목록 어댑터
 */
public class TakeOutLoadingBtmAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<TakeOutInfo> mArray = null;
    private String[] arrTemp;
    private TakeOutInfo info = null;

    public TakeOutLoadingBtmAdapter(Context context, ArrayList<TakeOutInfo> array) {
        mContext = context;
        mArray = array;
        arrTemp = new String[mArray.size()];
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

        final ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.take_out_loading_listview_btm_item, null);
            mViewHolder.text_picking_pallet = (TextView) convertView.findViewById(R.id.take_out_loading_btm_item_picking_pallet);
            mViewHolder.edit_loading_pallet = (EditText) convertView.findViewById(R.id.take_out_loading_btm_item_edit_loading_pallet);
            convertView.setTag(mViewHolder);
        } else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.ref = position;

        info = mArray.get(position);
        String picking_pallet = info.getPickingPltId();
        mViewHolder.text_picking_pallet.setText(picking_pallet);

        mViewHolder.edit_loading_pallet.setText(info.getLoadingPlt());

        mViewHolder.edit_loading_pallet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    info.setLoadingPlt(s.toString());
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView text_picking_pallet;
        EditText edit_loading_pallet;
        int ref;
    }
}
