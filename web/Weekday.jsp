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
<%@ page import="org.json.simple.JSONArray" %>
<%-- JSP에서 JDBC의 객체를 사용하기 위해 java.sql 패키지를 import한다--%>
<%
  try {
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();

    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection connection = null;
    connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/coint_webtoon","root","dmswnqt1");          //나중에 수정해야 할 부분
    if(connection != null) {

      String weekday = request.getParameter("weekday");

      String sql = "SELECT  * FROM WEBTOON WHERE ID IN (SELECT ID_W FROM WEEKDAY WHERE Weekday=?)";
      PreparedStatement statement = connection.prepareStatement(sql);

      statement.setString(1,weekday);
      ResultSet resultSet = statement.executeQuery();                         // sql 쿼리문 실행 결과를 resultSet에 저장

      int i=0;
      while(resultSet.next()){

        JSONObject object = new JSONObject();                                        // JSON내용을 담을 객체

        object.put("id",resultSet.getString("Id"));
        object.put("title",resultSet.getString("Title"));
        object.put("artist",resultSet.getString("Artist"));
        object.put("starscore",resultSet.getString("Starscore"));
        object.put("hits",resultSet.getString("Hits"));
        object.put("url",resultSet.getString("Thumburl"));
        object.put("toontype",resultSet.getString("Toontype"));
        object.put("is_adult",resultSet.getString("Is_adult"));
        object.put("is_charged",resultSet.getString("Is_charged"));

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
