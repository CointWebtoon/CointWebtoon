package com.kwu.cointwebtoon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

public class LoginActivity extends TypeKitActivity {
    /**
     *  Login API Instance
     */
    private static OAuthLogin loginInstance;
    private OAuthLoginHandler loginCallBack;

    /**
     *  Views
     */
    private OAuthLoginButton loginBtn;
    private TextView descTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        OAuthLoginDefine.DEVELOPER_VERSION = true;
        initView();
        getLoginInstance();
    }

    private void initView(){
        loginBtn = (OAuthLoginButton)findViewById(R.id.LoginActivity_LoginBtn);
        loginBtn.setOnClickListener(new onClick());
        descTextView = (TextView)findViewById(R.id.LoginActivity_Description);
        descTextView.setText("COINT 웹툰 로그인 화면입니다.\n\n네이버 아이디로 로그인해주세요.");
    }

    private void getLoginInstance(){
        loginInstance = Application_UserInfo.getLoginInstance();
        /**
         *  네아로 로그인 Activity(LoginActivity 말고 실제 로그인 액티비티) 종료 시 행동
         */
        loginCallBack = new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if(success){
                    LoginRequestApiTask apiTask = new LoginRequestApiTask(LoginActivity.this);
                    apiTask.execute();
                }else{
                    Application_UserInfo.initUserInfo();
                }
            }
        };
    }

    private class onClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.LoginActivity_LoginBtn:{
                    loginInstance.startOauthLoginActivity(LoginActivity.this, loginCallBack);
                    break;
                }
            }
        }
    }
}
