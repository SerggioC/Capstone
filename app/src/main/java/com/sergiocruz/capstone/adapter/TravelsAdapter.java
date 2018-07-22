package com.sergiocruz.capstone.adapter;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.sergiocruz.capstone.BR;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.model.Star;
import com.sergiocruz.capstone.model.Travel;

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

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String transitionName = getObjectForPosition(position).getID();
        holder.itemView.findViewById(R.id.image).setTransitionName(transitionName);


        holder.getBinding().setVariable(BR.star, star);
        holder.getBinding().setVariable(BR.numComments, numComments);
    }

    @Override
    protected Travel getObjectForPosition(int position) {
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




    class TravelViewHolder extends BaseAdapter.BaseViewHolder {
        private final ViewDataBinding binding;

        TravelViewHolder(ViewDataBinding binding) {
            super(binding);
            this.binding = binding;
        }

        void bindStars(Star star) {
            binding.setVariable(BR.star, star);
            binding.notifyPropertyChanged(BR.star);
        }

        void bindNumComments(Long numComments) {
            binding.setVariable(BR.numComments, numComments);
            binding.notifyPropertyChanged(BR.numComments);
        }

    }


}
