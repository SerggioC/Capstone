package com.sergiocruz.capstone.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sergiocruz.capstone.BR;
import com.sergiocruz.capstone.util.Utils;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder> {

    private final OnItemInteraction<T> itemInteractionListener;

    BaseAdapter(OnItemInteraction<T> itemInteractionListener) {
        this.itemInteractionListener = itemInteractionListener;
    }

    private boolean onTouchItem(View v, MotionEvent event) {
        return itemInteractionListener.onItemTouch(v, event);
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
        if (itemInteractionListener != null)
            holder.itemView.setOnClickListener(v -> itemInteractionListener.onItemClick(object, holder.itemView, position)); /* TODO move to BaseViewHolder? */
        if (itemInteractionListener != null)
            holder.itemView.setOnTouchListener(this::onTouchItem);
        Utils.setItemViewAnimation(holder.itemView, position);
        holder.bindVariable(object);
    }

    @Override
    public int getItemViewType(@LayoutRes int position) {
        return getLayoutIdForPosition(position);
    }

    protected abstract T getObjectForPosition(int position);

    protected abstract int getLayoutIdForPosition(int position);

    public interface OnItemInteraction<T> {
        void onItemClick(T object, View view, Integer position);

        boolean onItemTouch(View v, MotionEvent event);
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        BaseViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        ViewDataBinding getBinding() {
            return binding;
        }

        void bindVariable(T object) {
            binding.setVariable(BR.object, object);
            binding.executePendingBindings();
        }

    }

}