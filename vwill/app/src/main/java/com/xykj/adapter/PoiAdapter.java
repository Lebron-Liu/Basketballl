package com.xykj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.xykj.vwill.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PoiAdapter extends ItemAdapter<PoiInfo> {
    public PoiAdapter(Context context) {
        super(context);
    }

    @Override
    protected ViewHolder createHolder(int t) {
        View layout = LayoutInflater.from(context).inflate(R.layout.item_poi_layout, null);
        return new PoiHolder(layout);
    }

    @Override
    protected void bindView(PoiInfo poiInfo, ViewHolder holder) {
        PoiHolder h = (PoiHolder) holder;
        if(poiInfo == null){
            //自定义位置的显示
            h.itemName.setText("自定义位置");
            h.itemAddress.setText("在地图上自己选择位置");
        }else {
            h.itemName.setText(poiInfo.name);
            h.itemAddress.setText(poiInfo.address);
        }
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public PoiInfo getItem(int position) {
        if (position == 0) {
            return null;
        }
        return list.get(position - 1);
    }

    static class PoiHolder extends ViewHolder {
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_address)
        TextView itemAddress;

        PoiHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
