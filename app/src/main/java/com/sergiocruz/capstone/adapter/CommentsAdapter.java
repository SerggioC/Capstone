package com.sergiocruz.capstone.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.ItemCommentsListBinding;
import com.sergiocruz.capstone.model.Comment;

import java.util.List;

;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private final OnEditClickListener editClickListener;
    public interface OnEditClickListener {
        void onEditClick(Comment comment);
    }

    private String currentUserID;
    private List<Comment> commentList;

    public CommentsAdapter(String currentUserID, OnEditClickListener editClickListener) {
        this.currentUserID = currentUserID;
        this.editClickListener = editClickListener;
    }

    public void swapData(List<Comment> commentList) {
        this.commentList = commentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCommentsListBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_comments_list, parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemCommentsListBinding binding;

        CommentViewHolder(ItemCommentsListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.comment.setOnClickListener(this);
        }

        void bind(int position) {
            binding.setComment(commentList.get(position));
            binding.setCurrentUser(currentUserID);
            binding.edit.setOnClickListener(v -> editClickListener.onEditClick(commentList.get(position)));
        }

        @Override
        public void onClick(View v) {
            int maxLines = binding.comment.getMaxLines();
            int maxLinesRes = v.getContext().getResources().getInteger(R.integer.maxLines);
            maxLines = maxLines < Integer.MAX_VALUE ? Integer.MAX_VALUE : maxLinesRes;
            binding.comment.setMaxLines(maxLines);
        }
    }
}

