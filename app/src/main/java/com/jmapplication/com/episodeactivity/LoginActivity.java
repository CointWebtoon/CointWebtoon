package com.jmapplication.com.episodeactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Observer;

public class LoginActivity extends AppCompatActivity implements View.OnKeyListener{
    private EditText idEditText, pwEditText;
    private ImageButton loginBtn;
    private CheckBox autoLoginCheckBox;
    private COINT_Application mContext;
    private CustomProgressDialog progressDialog = null;
    private String id, pw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().setStatusBarColor(Color.parseColor("#009900"));
        progressDialog = new CustomProgressDialog(this);
        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.passwordEditText);
        autoLoginCheckBox = (CheckBox)findViewById(R.id.chekBox_AutoLogin);
        loginBtn = (ImageButton) findViewById(R.id.loginBtn);
        idEditText.setOnKeyListener(this);
        pwEditText.setOnKeyListener(this);
        mContext = (COINT_Application)getApplicationContext();
    }

    @Override
    protected void onDestroy() {
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_ENTER && loginBtn.isEnabled()){
            loginBtn.callOnClick();
            InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(idEditText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(pwEditText.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    public void loginButtonClick(View v) {
        LoginTask task = new LoginTask();
        id = idEditText.getText().toString();
        pw = pwEditText.getText().toString();
        task.execute(id, pw);
    }

    private class LoginTask extends AsyncTask<String, Boolean, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginBtn.setEnabled(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... values) {
            try {
                String urlParameters = "id=" + values[0] + "&pw=" + values[1] + "&info=TEAM_COINT";
                String response = "";
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;
                String request = "http://coint.iptime.org:8080/Login.jsp";
                URL url = new URL(request);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setUseCaches(false);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                wr.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    response = response.trim();
                    return new JSONObject(response);
                }else {
                    throw new Exception("LOGIN CONNECTION ERROR");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try{
                if(jsonObject == null){
                    Log.i("Login", "로그인 실패 NULL JSON");
                    progressDialog.dismiss();
                    return;
                }
                if(jsonObject.getString("RESULT").equals("SUCCESS")){
                    Log.i("Login", "로그인 성공");
                    Toast.makeText(LoginActivity.this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    if(jsonObject.getString("ADULT").equals("YES")){
                        Log.i("Login", "성인");
                        mContext.setLogin(id, pw, true);
                    }else if(jsonObject.getString("ADULT").equals("NO")){
                        Log.i("Login", "미성년자");
                        mContext.setLogin(id, pw, false);
                    }

                    if(autoLoginCheckBox.isChecked()){
                        mContext.setAutoLoginEnabled(true);
                        mContext.saveLoginToSharedPreference();
                    }

                    LoginActivity.this.finish();
                }else if(jsonObject.getString("RESULT").equals("FAIL")){
                    Log.i("Login", "로그인 실패");
                    Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.i("Login", "로그인 실패, EXCEPTION");
                progressDialog.dismiss();
            }finally {
                loginBtn.setEnabled(true);
            }
        }
    }
}