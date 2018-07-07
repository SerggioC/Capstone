package com.sergiocruz.capstone.adapter;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.model.Travel;

import java.util.List;

public class MyTravelsAdapter extends BaseAdapter {
    private List<Travel> travels;

    public MyTravelsAdapter(List<Travel> travels, OnItemClickListener itemClickListener, OnItemTouchListener itemTouchListener) {
        super(itemClickListener, itemTouchListener);
        this.travels = travels;
    }

    @Override
    protected Object getObjectForPosition(int position) {
        return travels.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.travel_item_layout;
    }

    @Override
    public int getItemCount() {
        return travels == null ? 0 : travels.size();
    }
}
