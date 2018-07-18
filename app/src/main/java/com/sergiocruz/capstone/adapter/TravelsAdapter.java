package com.sergiocruz.capstone.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.util.Utils;

import java.util.List;

public class TravelsAdapter extends BaseAdapter {
    private List<Travel> travels;

    @SuppressWarnings("unchecked")
    public TravelsAdapter(OnItemClickListener itemClickListener, OnItemTouchListener itemTouchListener) {
        super(itemClickListener, itemTouchListener);
    }

    public void swapTravelsData(List<Travel> data) {
        this.travels = data;
        notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Utils.setItemViewAnimation(holder.itemView, position);
        String transitionName = getObjectForPosition(position).getID();
        holder.itemView.findViewById(R.id.image).setTransitionName(transitionName);
    }

    @Override
    protected Travel getObjectForPosition(int position) {
        return travels.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(@LayoutRes int position) {
        return R.layout.travel_item_layout;
    }

    @Override
    public int getItemCount() {
        return travels == null ? 0 : travels.size();
    }

}
