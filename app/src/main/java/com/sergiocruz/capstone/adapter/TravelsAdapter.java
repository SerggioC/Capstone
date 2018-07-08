package com.sergiocruz.capstone.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.model.Travel;

import java.util.List;

public class TravelsAdapter extends BaseAdapter {
    private List<Travel> travels;

    public TravelsAdapter(OnItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    public void swapData(List<Travel> data) {
        this.travels = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        setItemViewAnimation(holder.itemView);
    }

    @Override
    protected Travel getObjectForPosition(int position) {
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

    private void setItemViewAnimation(View viewToAnimate) {
        Animation topAnimation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_from_top);
        viewToAnimate.startAnimation(topAnimation);
    }
}
