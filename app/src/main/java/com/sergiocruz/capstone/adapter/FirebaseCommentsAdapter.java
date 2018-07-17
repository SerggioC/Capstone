package com.sergiocruz.capstone.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.CommentsListItemBinding;
import com.sergiocruz.capstone.model.Comment;

public class FirebaseCommentsAdapter extends FirebaseRecyclerAdapter<Comment, FirebaseCommentsAdapter.CommentViewHolder> {

    public FirebaseCommentsAdapter(@NonNull FirebaseRecyclerOptions<Comment> options) {
        super(options);
    }

    @NonNull
    @Override
    public FirebaseCommentsAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CommentsListItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.comments_list_item, parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment comment) {
        holder.bind(comment);
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private final CommentsListItemBinding binding;

        CommentViewHolder(CommentsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;;
        }

        void bind(Comment comment) {
            binding.setComment(comment);
        }
    }

}
