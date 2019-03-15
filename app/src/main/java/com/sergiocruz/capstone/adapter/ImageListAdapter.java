package com.sergiocruz.capstone.adapter;

import android.support.annotation.NonNull;

import com.sergiocruz.capstone.R;

import java.util.List;

public class ImageListAdapter extends BaseAdapter {
    private List<String> imagesList;

    @SuppressWarnings("unchecked")
    public ImageListAdapter(List<String> imagesList, OnItemInteraction itemInteraction) {
        super(itemInteraction);
        this.imagesList = imagesList;
    }

    @Override
    protected String getObjectForPosition(int position) {
        return imagesList.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_image_layout;
    }

    @Override
    public int getItemCount() {
        return imagesList == null ? 0 : imagesList.size();
    }

}
