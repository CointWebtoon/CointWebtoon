package com.kwu.cointwebtoon;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.nhn.android.naverlogin.OAuthLogin;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * 현재 로그인 되어 있는 instance 를 통해서 API 를 호출해 사용자의 정보를 얻어오는 Task
 */
public class LoginRequestApiTask extends AsyncTask<Void, Void, String> {
    private Context mContext;
    private OAuthLogin loginInstance;
    private Application_UserInfo userInfo;

    /**
     * Constructor
     */
    public LoginRequestApiTask(Context mContext, Application_UserInfo application) {
        userInfo = application;
        this.mContext = mContext;
        loginInstance = userInfo.getLoginInstance();
    }

    @Override
    protected String doInBackground(Void... params) {
        String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
        String at = loginInstance.getAccessToken(mContext);
        return loginInstance.requestApi(mContext, at, url);
    }

    protected void onPostExecute(String content) {
        XMLDOMParser parser = new XMLDOMParser();
        if (parser.parse(content)) {
            Log.i("coint", "유저 정보 저장 성공");
            Log.i("coint", "닉네임 : " + userInfo.getUserName() + " 성인 여부 : " + userInfo.isUserAdult() + " 성별 : " + userInfo.getUserGender());
            if (mContext instanceof LoginActivity) {
                ((LoginActivity) mContext).finish();
            }
        } else {
            userInfo.initUserInfo();
        }
    }

    /**
     * API 호출을 통해 받아온 XML Data 에서 필요한 정보를 꺼내는 Parser
     */
    private class XMLDOMParser {
        private DocumentBuilderFactory builderFactory;
        private DocumentBuilder builder;
        private Document doc;

        public XMLDOMParser() {
            super();
            builderFactory = DocumentBuilderFactory.newInstance();
            doc = null;
            try {
                builder = builderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        public boolean parse(String xmlData) {
            try {
                String id, nickname, age, gender;
                doc = builder.parse(new InputSource(new StringReader(xmlData)));
                doc.getDocumentElement().normalize();
                Element root = doc.getDocumentElement();
                Element resultElement = (Element) root.getElementsByTagName("result").item(0);
                resultElement = (Element) resultElement.getElementsByTagName("resultcode").item(0);
                if (resultElement == null | !resultElement.getTextContent().equals("00"))
                    throw new ResultCodeNotSuccessException("ResultCode : " + resultElement);

                Element responseElement = (Element) root.getElementsByTagName("response").item(0);
                id = responseElement.getElementsByTagName("id").item(0).getTextContent();
                nickname = responseElement.getElementsByTagName("nickname").item(0).getTextContent();
                age = responseElement.getElementsByTagName("age").item(0).getTextContent();
                gender = responseElement.getElementsByTagName("gender").item(0).getTextContent();
                int startAge = Integer.parseInt(age.split("-")[0]);
                userInfo.onLogIn(id, nickname, startAge > 19 ? true : false, gender.charAt(0));

                Log.i("coint", userInfo.getUserName() + " " + userInfo.isUserAdult() + " " + userInfo.getUserGender());
                return true;
            } catch (ResultCodeNotSuccessException e) {
                Log.e("coint", "Result Code is not SUCCESS : 로그인 안 된 상태");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("coint", "Parsing Failed");
                return false;
            }
        }

        public class ResultCodeNotSuccessException extends Exception {
            public ResultCodeNotSuccessException(String detailMessage) {
                super(detailMessage);
            }
        }
    }
}
