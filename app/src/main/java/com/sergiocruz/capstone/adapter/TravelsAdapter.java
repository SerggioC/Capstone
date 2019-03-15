package com.sergiocruz.capstone.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.model.TravelData;

import java.util.List;

public class TravelsAdapter extends BaseAdapter {
    private List<TravelData> travelData;

    @SuppressWarnings("unchecked")
    public TravelsAdapter(OnItemInteraction itemInteraction) {
        super(itemInteraction);
    }

    public void swapTravelsData(List<TravelData> data) {
        this.travelData = data;
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
        return travelData.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(@LayoutRes int position) {
        return R.layout.item_travel_layout;
    }

    @Override
    public int getItemCount() {
        return travelData == null ? 0 : travelData.size();
    }

}
