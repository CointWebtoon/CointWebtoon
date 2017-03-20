<%@ page contentType="application/json;charset=EUC-KR" language="java" %>
<%@ page import="importClasses.NaverLogin" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.jsoup.Jsoup" %>
<%@ page import="org.jsoup.nodes.Document" %>
<%@ page import="java.util.Map" %>

<%!
private String RESULT = "SUCCESS";
private String ADULT = "NO";
private String id = "", pw = "";
private Map<String, String> cookieMap = null;
private JSONObject loginResult;
%>

<%
    try{
        if(!request.getParameter("info").equals("TEAM_COINT")){
            out.print("권한이 없습니다.");
            throw new Exception("BAD ACCESS");
        }
        loginResult = new JSONObject();
        id = request.getParameter("id");
        pw = request.getParameter("pw");
        NaverLogin login = new NaverLogin(id,pw, true);
        if(login.isLogin()) {
            cookieMap = login.getCookies();
            Document doc = Jsoup.connect("http://comic.naver.com/webtoon/detail.nhn?titleId=674209&no=1").cookies(cookieMap).get();
            if(doc.select(".wrt_nm").first() != null){   //성인 확인
                System.out.println(id + " LOGIN SUCCESS, ADULT");
                ADULT = "YES";
            }else {
                System.out.println(id + " LOGIN SUCCESS, CHILD");
            }
        }else {
            RESULT = "FAIL";
            System.out.println(id + " LOGIN FAILURE");
        }
    }catch (Exception e){
        e.printStackTrace();
        RESULT = "FAIL";
        System.out.println(id + " LOGIN FAILURE");
    }
    loginResult.put("RESULT", RESULT);
    loginResult.put("ADULT", ADULT);
    if(cookieMap != null){
        for(String key : cookieMap.keySet()){
            loginResult.put(key, cookieMap.get(key));
        }
    }
    out.print(loginResult);
    out.flush();
%>