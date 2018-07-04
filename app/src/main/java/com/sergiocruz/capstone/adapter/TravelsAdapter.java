package com.sergiocruz.capstone.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.sergiocruz.capstone.model.Travel;

import java.util.List;

public class TravelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Travel> travels;

    public TravelsAdapter(List<Travel> travels) {
        this.travels = travels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return travels != null ? travels.size() : 0;
    }


}
