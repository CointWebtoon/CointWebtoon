package com.kwu.cointwebtoon;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kwu.cointwebtoon.DataStructure.Comment;

import java.util.ArrayList;

public class ViewerCommentAdapter extends RecyclerView.Adapter {
    private ArrayList<Comment> commentList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;
    private SharedPreferences commentLikePref;
    private boolean isMine = false;


    public ViewerCommentAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        commentLikePref = mContext.getSharedPreferences("comment_like", Context.MODE_PRIVATE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.viewer_comment_item, parent, false);
        ViewerCommentAdapter.ViewHolder holder = new ViewerCommentAdapter.ViewHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Comment currentItem = commentList.get(position);
        ViewerCommentAdapter.ViewHolder myHolder = (ViewerCommentAdapter.ViewHolder) holder;
        if (position < 10 && currentItem.getLikes() > 0) {
            myHolder.bestTV.setVisibility(View.VISIBLE);
        } else {
            myHolder.bestTV.setVisibility(View.GONE);
        }
        myHolder.likeTV.setText(String.valueOf(currentItem.getLikes()));
        myHolder.contentTV.setText(currentItem.getContent());
        myHolder.nicknameTV.setText(currentItem.getNickname());
        myHolder.nicknameTV.setTag(currentItem);
        myHolder.likeIB.setTag(R.id.comment_like, currentItem);
        myHolder.deleteIB.setTag(R.id.comment_delete, currentItem);
        if (isMine) {
            myHolder.deleteIB.setVisibility(View.VISIBLE);
        } else {
            myHolder.deleteIB.setVisibility(View.GONE);
        }
        if (commentLikePref.getBoolean(String.valueOf(currentItem.getComment_id()), false)) {
            myHolder.likeIB.setImageDrawable(mContext.getDrawable(R.drawable.episode_heart_active));
        } else {
            myHolder.likeIB.setImageDrawable(mContext.getDrawable(R.drawable.episode_heart_inactive));
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void changeItem(ArrayList<Comment> comments, boolean isMine) {
        this.isMine = isMine;
        this.commentList = comments;
        notifyDataSetChanged();
    }

    public void removeItem(Comment comment) {
        commentList.remove(comment);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nicknameTV, contentTV, likeTV, timeTV, bestTV;
        public ImageButton likeIB, deleteIB;
        public View v;

        public ViewHolder(View view) {
            super(view);
            this.v = view;
            nicknameTV = (TextView) view.findViewById(R.id.comment_nickname);
            contentTV = (TextView) view.findViewById(R.id.comment_content);
            likeTV = (TextView) view.findViewById(R.id.comment_like_text);
            timeTV = (TextView) view.findViewById(R.id.comment_time);
            bestTV = (TextView) view.findViewById(R.id.comment_best);
            likeIB = (ImageButton) view.findViewById(R.id.comment_like);
            deleteIB = (ImageButton) view.findViewById(R.id.comment_delete);
        }
    }
}
