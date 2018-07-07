package com.sergiocruz.capstone.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sergiocruz.capstone.BR;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.TravelItemLayoutBinding;
import com.sergiocruz.capstone.model.Travel;

import java.util.List;

public class TravelsAdapter extends RecyclerView.Adapter<TravelsAdapter.TravelsViewHolder> {
    private List<Travel> travels;

    public TravelsAdapter(List<Travel> travels) {
        this.travels = travels;
    }

    @NonNull
    @Override
    public TravelsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TravelItemLayoutBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.travel_item_layout, parent, false);
        return new TravelsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelsViewHolder holder, int position) {
        Travel travel = travels.get(position);
        holder.bind(travel);
    }

    @Override
    public int getItemCount() {
        return travels != null ? travels.size() : 0;
    }

    public class TravelsViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        public TravelsViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Object object) {
            binding.setVariable(BR.object, object);
            binding.executePendingBindings();
        }
    }


}
