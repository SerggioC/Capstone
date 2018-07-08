package com.sergiocruz.capstone.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sergiocruz.capstone.BR;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder> {

    private final OnItemClickListener<T> itemClickListener;

    BaseAdapter(OnItemClickListener<T> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T object);
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
        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(object));
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