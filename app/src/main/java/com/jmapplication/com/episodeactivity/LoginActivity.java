package com.jmapplication.com.episodeactivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by jm on 2017-03-08.
 */

public class LoginActivity extends AppCompatActivity implements View.OnKeyListener{
    EditText idEditText, pwEditText;
    ImageButton loginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.passwordEditText);
        loginBtn = (ImageButton) findViewById(R.id.loginBtn);
        idEditText.setOnKeyListener(this);
        pwEditText.setOnKeyListener(this);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_ENTER){
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
        task.execute(idEditText.getText().toString(), pwEditText.getText().toString());
    }

    public String parseBody(String html) {
        String bodyStart = "<body>";
        String bodyEnd = "</body>";
        return html.substring(html.indexOf(bodyStart) + bodyStart.length(), html.indexOf(bodyEnd));
    }

    private class LoginTask extends AsyncTask<String, Boolean, Integer> {
        CustomProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginBtn.setEnabled(false);
            progressDialog = new CustomProgressDialog(LoginActivity.this);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... values) {
            try {
                String urlParameters = "id=" + values[0] + "&pw=" + values[1];
                String response = "";
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;
                String request = "http://10.0.2.2:8080/Login.jsp";
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
                }
                return Integer.parseInt(parseBody(response));
            } catch (IOException ioex) {
                ioex.printStackTrace();
                return null;
            }finally {
                publishProgress(true);
            }
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            //Dialog 닫는 행동을 onPostExecute에 지정했더니 WindowManager에서 오류떠서 onProgressUpdate에 넣었다.
            progressDialog.dismiss();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            loginBtn.setEnabled(true);
            if (integer == Integer.valueOf(1)) {
                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                LoginActivity.this.finish();
            } else if (integer == Integer.valueOf(2)) {
                Toast.makeText(LoginActivity.this, "로그인 성공(성인)", Toast.LENGTH_SHORT).show();
                LoginActivity.this.finish();
            } else {
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }
}