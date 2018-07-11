package com.sergiocruz.capstone.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sergiocruz.capstone.BR;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder> {

    private final OnItemClickListener<T> itemClickListener;
    private final OnItemTouchListener itemTouchListener;

    BaseAdapter(OnItemClickListener<T> itemClickListener, OnItemTouchListener itemTouchListener) {
        this.itemClickListener = itemClickListener;
        this.itemTouchListener = itemTouchListener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T object);
    }

    public interface OnItemTouchListener {
        boolean onItemTouch(View v, MotionEvent event);
    }

    private boolean onTouchItem(View v, MotionEvent event) {
        itemTouchListener.onItemTouch(v, event);
        return false;
    }

    @NonNull
    public BaseAdapter<T>.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false);
        return new BaseAdapter<T>.BaseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseAdapter<T>.BaseViewHolder holder, int position) {
        final T object = getObjectForPosition(position);
        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(object)); /* TODO move to BaseViewHolder */
        holder.itemView.setOnTouchListener(this::onTouchItem);
        holder.bind(object);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    protected abstract T getObjectForPosition(int position);

    protected abstract int getLayoutIdForPosition(int position);

    class BaseViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        BaseViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(T object) {
            binding.setVariable(BR.object, object);
            binding.executePendingBindings();
        }
    }
}