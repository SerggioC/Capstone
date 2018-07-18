package com.sergiocruz.capstone.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.ItemCommentsListBinding;
import com.sergiocruz.capstone.model.Comment;

public class FirebaseCommentsAdapter extends FirebaseRecyclerAdapter<Comment, FirebaseCommentsAdapter.CommentViewHolder> {
    private String currrentUser;

    public FirebaseCommentsAdapter(@NonNull FirebaseRecyclerOptions<Comment> options, String currentUser) {
        super(options);
        this.currrentUser = currentUser;

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("chats")
                .limitToLast(50);

        FirebaseRecyclerOptions<Comment> optionse =
                new FirebaseRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment.class) // result direct to class or with custom parser
                        .setQuery(query, new SnapshotParser<Comment>() {
                            @NonNull
                            @Override
                            public Comment parseSnapshot(@NonNull DataSnapshot snapshot) {

                                return null;
                            }
                        })
                        .build();


    }

    @NonNull
    @Override
    public FirebaseCommentsAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCommentsListBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_comments_list, parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment comment) {
//        Comment comment = snapshot.getValue(Comment.class);
        holder.bind(comment);
    }

    class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ItemCommentsListBinding binding;

        CommentViewHolder(ItemCommentsListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.comment.setOnClickListener(this);
        }

        void bind(Comment comment) {
            binding.setComment(comment);
            binding.setCurrentUser(currrentUser);
        }

        @Override
        public void onClick(View v) {
            int maxLines = binding.comment.getMaxLines();
            int maxLinesRes = v.getContext().getResources().getInteger(R.integer.maxLines);
            maxLines = maxLines < Integer.MAX_VALUE ? maxLinesRes : Integer.MAX_VALUE;
            binding.comment.setMaxLines(maxLines);
        }
    }

}
