<%--
  User: EunJoo Choi
  Date: 2017-02-05
  Time: 오후 8:02
  To change this template use File | Settings | File Templates.
--%>

<%--
        안드로이드에서 json파싱할때 <html>같은 태그부분도 같이 따라오는 바람에
         contentType을 application/json으로 바꿔주어 파싱 문제를 해결했습니다.

        jsp 주소 형식 : http://localhost:8080/Likes.jsp?weekday=3
--%>
<%@ page contentType="application/json;charset=EUC-KR" language="java" %>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %><%@ page import="importClasses.DBAuthentication"%>
<%-- JSP에서 JDBC의 객체를 사용하기 위해 java.sql 패키지를 import한다--%>
<%
  try {
    Connection conn = null;
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();

        /* Corresponding development environment is required for DB access */
        Class.forName(DBAuthentication.driverName);
        conn = DriverManager.getConnection(DBAuthentication.url, DBAuthentication.id, DBAuthentication.password);
        System.out.println("Connection successful");

    if(conn != null) {

      String sql = "SELECT Id_G, Genre FROM GENRE, WEBTOON WHERE Id=Id_G AND Mobile_unsupported=0";
      PreparedStatement statement = conn.prepareStatement(sql);

      ResultSet resultSet = statement.executeQuery();                         // sql 쿼리문 실행 결과를 resultSet에 저장

      int i=0;
      while(resultSet.next()){

        JSONObject object = new JSONObject();                                        // JSON내용을 담을 객체

        object.put("id",resultSet.getString("Id_G"));
        object.put("genre",resultSet.getString("Genre"));

        jsonArray.add(i,object);
        jsonObject.put("result",jsonArray);                                                 // JSON의 제목 지정

        i++;
      }
      out.println(jsonObject);
      out.flush();
    }
  }catch (SQLException ex){
    System.out.println("SQL Exception "+ex);
  }catch (Exception ex) {
    System.out.println("Exception " + ex);
  }
%>
