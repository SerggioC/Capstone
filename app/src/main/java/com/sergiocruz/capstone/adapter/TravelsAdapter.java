package com.sergiocruz.capstone.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.model.TravelData;

import java.util.List;

public class TravelsAdapter extends BaseAdapter {
    private List<TravelData> travels;

    @SuppressWarnings("unchecked")
    public TravelsAdapter(OnItemClickListener itemClickListener, OnItemTouchListener itemTouchListener) {
        super(itemClickListener, itemTouchListener);
    }

    public void swapTravelsData(List<TravelData> data) {
        this.travels = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String transitionName = getObjectForPosition(position).getTravel().getID();
        holder.itemView.findViewById(R.id.image).setTransitionName(transitionName);
    }

    @Override
    protected TravelData getObjectForPosition(int position) {
        return travels.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(@LayoutRes int position) {
        return R.layout.item_travel_layout;
    }

    @Override
    public int getItemCount() {
        return travels == null ? 0 : travels.size();
    }

}
