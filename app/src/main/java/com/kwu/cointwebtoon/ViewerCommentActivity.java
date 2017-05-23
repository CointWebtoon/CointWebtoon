package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

public class ViewerCommentActivity extends TypeKitActivity implements Observer, View.OnTouchListener {
    /**
     * UI Components
     */
    private RecyclerView recycler;
    private ViewerCommentAdapter adapter;
    private Button allComment, myComment;
    private CointProgressDialog dialog;

    /**
     * List data
     */
    private GetServerData getServerData;
    private ArrayList<Comment> comments;
    private ArrayList<Comment> myComments = new ArrayList<>();

    /**
     * Members
     */
    private Application_UserInfo userInfo;
    private SharedPreferences commentPreference;
    private int id, ep_id, cutNumber;
    private float x, y;
    private int currentList = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_comment);
        initView();
        initData();
    }

    /**
     * UI Components
     */
    private void initView() {
        dialog = new CointProgressDialog(this);
        recycler = (RecyclerView) findViewById(R.id.comment_list);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new SimpleDividerItemDecoration(this));
        recycler.setOnTouchListener(this);
        adapter = new ViewerCommentAdapter(this);
        recycler.setAdapter(adapter);
        registerForContextMenu(recycler);
        allComment = (Button) findViewById(R.id.allcomment);
        myComment = (Button) findViewById(R.id.mycomment);
    }

    /**
     * Data
     */
    private void initData() {
        commentPreference = getSharedPreferences("comment_like", MODE_PRIVATE);
        userInfo = (Application_UserInfo) getApplication();
        Intent getIntent = getIntent();
        id = getIntent.getIntExtra("id", -1);
        ep_id = getIntent.getIntExtra("ep_id", -1);
        cutNumber = getIntent.getIntExtra("cutnum", -1);

        if (id == -1 | ep_id == -1) {
            Toast.makeText(this, "존재하지 않는 에피소드입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        getServerData = new GetServerData(this);
        getServerData.registerObserver(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            float gapX = event.getX() - x, gapY = event.getY() - y;
            if (Math.abs(gapX) > 200 && Math.abs(gapY) < 200) {
                if (gapX < 0) {//다음
                    myComment.performClick();
                }else {//이전
                    allComment.performClick();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog.show();
        String requestURL = "http://coint.iptime.org:8080/Comment_Get.jsp?id=" + id + "&ep_id=" + ep_id;
        if (cutNumber != -1) {
            requestURL += "&cutnumber=" + cutNumber;
        }
        getServerData.getComments(requestURL);
    }

    public void onRecyclerItemClick(View v) {
        ViewerCommentAdapter.ViewHolder holder = (ViewerCommentAdapter.ViewHolder) v.getTag();
        Comment target = (Comment) holder.nicknameTV.getTag();
        v.showContextMenu();
    }

    public void onRecyclerItemChildClick(View v) {
        Comment target = (Comment) v.getTag(v.getId());
        switch (v.getId()) {
            case R.id.comment_delete:
                getServerData.deleteComment(target.getComment_id());
                adapter.removeItem(target);
                comments.remove(target);
                Toast.makeText(this, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.comment_like:
                if (!userInfo.isLogin()) {
                    new AlertDialog.Builder(this)
                            .setTitle("댓글 공감")
                            .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ViewerCommentActivity.this, LoginActivity.class));
                                }
                            }).setNegativeButton("아니요", null).show();
                    return;
                }
                if (commentPreference.getBoolean(String.valueOf(target.getComment_id()), false)) {
                    Toast.makeText(this, "이미 공감한 댓글입니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    SharedPreferences.Editor editor = commentPreference.edit();
                    getServerData.likeComment(target.getComment_id());
                    editor.putBoolean(String.valueOf(target.getComment_id()), true);
                    editor.commit();
                    ((ImageButton) v).setImageDrawable(getDrawable(R.drawable.episode_heart_active));
                    target.setLikes(target.getLikes() + 1);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public void CommentTitleClick(View v) {
        switch (v.getId()) {
            case R.id.allcomment:
                if (currentList == 1) {
                    adapter.changeItem(comments, false);
                    allComment.setBackgroundColor(Color.parseColor("#28DCBE"));
                    allComment.setTextColor(Color.WHITE);
                    myComment.setBackgroundColor(Color.WHITE);
                    myComment.setTextColor(getResources().getColor(R.color.D2Gray));
                    currentList = 0;
                }
                break;
            case R.id.mycomment:
                if (currentList == 0) {
                    if (!userInfo.isLogin()) {
                        new AlertDialog.Builder(this)
                                .setTitle("내가 쓴 댓글")
                                .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(ViewerCommentActivity.this, LoginActivity.class));
                                    }
                                }).setNegativeButton("아니요", null).show();
                        return;
                    }
                    adapter.changeItem(myComments, true);
                    myComment.setBackgroundColor(Color.parseColor("#28DCBE"));
                    myComment.setTextColor(Color.WHITE);
                    allComment.setBackgroundColor(Color.WHITE);
                    allComment.setTextColor(getResources().getColor(R.color.D2Gray));
                    currentList = 1;
                }
                break;
            case R.id.comment_write:
                if (!userInfo.isLogin()) {
                    new AlertDialog.Builder(this)
                            .setTitle("댓글 쓰기")
                            .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ViewerCommentActivity.this, LoginActivity.class));
                                }
                            }).setNegativeButton("아니요", null).show();
                    return;
                }
                Intent writeIntent = new Intent(this, ViewerCommentWriteActivity.class);
                writeIntent.putExtra("id", id);
                writeIntent.putExtra("ep_id", ep_id);
                writeIntent.putExtra("cutnumber", cutNumber);
                writeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(writeIntent);
                break;
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        new updateViewTask().execute(o);
        dialog.dismiss();
    }

    public void Comment_BackBtn(View v) {
        this.finish();
    }

    public void initMyComment() {
        for (Comment comment : comments) {
            if (comment.getWriter().equals(userInfo.getUserID())) {
                myComments.add(comment);
            }
        }
    }

    @Override
    protected void onDestroy() {
        getServerData.removeObserver(this);
        super.onDestroy();
    }

    private class updateViewTask extends AsyncTask<Object, Void, Void> {
        ArrayList<Comment> callbackList;

        @Override
        protected Void doInBackground(Object... params) {
            callbackList = (ArrayList<Comment>) params[0];
            ArrayList<Comment> copyList = new ArrayList<>();
            ArrayList<Comment> bestList = new ArrayList<>();
            copyList.addAll(callbackList);
            Collections.sort(copyList);
            for (int i = 0; i < 10 && i < copyList.size(); i++) {
                Comment currentItem = copyList.get(i);
                if (currentItem.getLikes() > 0) {
                    currentItem.setBest(true);
                    callbackList.remove(currentItem);
                    bestList.add(currentItem);
                }
            }
            for (int i = bestList.size() - 1; i >= 0; i--) {
                callbackList.add(0, bestList.get(i));
            }
            comments = callbackList;
            if (userInfo.isLogin()) {
                initMyComment();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callbackList.size() == 0) {
                Toast.makeText(ViewerCommentActivity.this, "표시할 댓글이 없습니다.", Toast.LENGTH_LONG).show();
            }
            Log.i("coint", String.valueOf(callbackList.size()));
            adapter.changeItem(callbackList, false);
        }
    }

    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.viewer_comment_line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kakao:
                break;
            case R.id.line:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
