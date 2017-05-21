package com.kwu.cointwebtoon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Comment;

public class ViewerCommentWriteActivity extends TypeKitActivity {
    /**
     * Members
     */
    private int id, ep_id, cutNumber;
    private Application_UserInfo userInfo;
    private TextView userNickname;
    private EditText content;
    private CointProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_write_activity);
        userInfo = (Application_UserInfo)getApplication();
        Intent getIntent = getIntent();
        id = getIntent.getIntExtra("id", -1);
        ep_id = getIntent.getIntExtra("ep_id", -1);
        cutNumber = getIntent.getIntExtra("cutnumber", -1);
        userNickname = (TextView)findViewById(R.id.comment_write_userid);
        userNickname.setText(userInfo.getUserName());
        content = (EditText)findViewById(R.id.comment_write_edittext);
        dialog = new CointProgressDialog(this);
    }

    public void CommentWriteClick(View v){
        switch (v.getId()){
            case R.id.comment_write_back:
            case R.id.comment_write_back_text:
                this.finish();
                break;
            case R.id.comment_write_complete:
                //작성 완료
                if(content.getText().toString().equals("")){
                    Toast.makeText(this, "빈 댓글은 작성할 수 없습니다." , Toast.LENGTH_SHORT).show();
                    return;
                }
                Comment comment = new Comment(-1, id, ep_id, userInfo.getUserID(), userInfo.getUserName(), content.getText().toString(),
                        0, null, cutNumber, false);
                GetServerData serverData = new GetServerData(this);
                serverData.addComment(comment);
                dialog.show();
                new Thread(){
                    public void run(){
                        try{Thread.sleep(3000);}catch (InterruptedException e){}
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                ViewerCommentWriteActivity.this.finish();
                            }
                        });
                    }
                }.start();
                break;
        }
    }
}
