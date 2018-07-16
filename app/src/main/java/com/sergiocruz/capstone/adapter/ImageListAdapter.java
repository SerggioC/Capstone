package com.sergiocruz.capstone.adapter;

import android.support.annotation.NonNull;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.util.Utils;

import java.util.List;

public class ImageListAdapter extends BaseAdapter {
    private List<String> imagesList;

    @SuppressWarnings("unchecked")
    public ImageListAdapter(List<String> imagesList, OnItemClickListener itemClickListener, OnItemTouchListener itemTouchListener) {
        super(itemClickListener, itemTouchListener);
        this.imagesList = imagesList;
    }

    @Override
    protected Object getObjectForPosition(int position) {
        return imagesList.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.image_item_layout;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Utils.setItemViewAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return imagesList == null ? 0 : imagesList.size();
    }

}
