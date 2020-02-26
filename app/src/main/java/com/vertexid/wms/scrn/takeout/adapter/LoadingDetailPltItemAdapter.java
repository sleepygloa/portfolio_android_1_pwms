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
import com.vertexid.wms.info.TakeOutLoadingInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 출하상차 파렛트 목록 항목 생성 어댑터
 */
public class LoadingDetailPltItemAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<TakeOutLoadingInfo.PltInfo> mArray = null;

    private HashMap<Integer, String> mLoadingHash = null;

    public LoadingDetailPltItemAdapter(Context context, ArrayList<TakeOutLoadingInfo.PltInfo> array) {
        mContext = context;
        mArray = array;

        mLoadingHash = new HashMap<>();
        mLoadingHash.clear();
    }

    public HashMap<Integer, String> getLoadingInfo() {
        return mLoadingHash;
    }

    public void clear() {
        if (mLoadingHash != null) {
            for (int i = 0 ; i < mLoadingHash.size() ; i++) {
                mLoadingHash.remove(i);
            }

            mLoadingHash.clear();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.take_out_loading_listview_btm_item, null);
            EditText edit_loading_plt_id = (EditText) convertView.findViewById(R.id.take_out_loading_btm_item_edit_loading_pallet);
            edit_loading_plt_id.setOnFocusChangeListener(mFocusChangeListener);
            edit_loading_plt_id.addTextChangedListener(mTextWatcher);
        }

        TakeOutLoadingInfo.PltInfo info = mArray.get(position);
        String picking_plt_id = info.getPickingPltId();
        String loading_plt_id = info.getLoadingPltId();

        TextView text_picking_plt_id = (TextView) convertView.findViewById(R.id.take_out_loading_btm_item_picking_pallet);
        EditText edit_loading_plt_id = (EditText) convertView.findViewById(R.id.take_out_loading_btm_item_edit_loading_pallet);

        text_picking_plt_id.setText(picking_plt_id);
        edit_loading_plt_id.setText(loading_plt_id);
        edit_loading_plt_id.setTag(position);

        return convertView;
    }

    private int mPosition = 0;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mLoadingHash == null) {
                mLoadingHash = new HashMap<>();
                mLoadingHash.clear();
            }

            mLoadingHash.put(mPosition, s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mPosition = (int) v.getTag();
            }
        }
    };
}
