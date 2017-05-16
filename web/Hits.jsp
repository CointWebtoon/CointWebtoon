<%--
        안드로이드에서 json파싱할때 <html>같은 태그부분도 같이 따라오는 바람에
         contentType을 application/json으로 바꿔주어 파싱 문제를 해결했습니다.

        jsp 주소 형식 : http://localhost:8080/Hits.jsp?id=679519
--%>

<%@ page contentType="application/json;charset=EUC-KR" language="java" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.PreparedStatement" %><%@ page import="java.sql.SQLException"%><%@ page import="importClasses.DBAuthentication"%>

<%
  try {
    Connection connection = null;
    Class.forName(DBAuthentication.driverName);
    connection = DriverManager.getConnection(DBAuthentication.url, DBAuthentication.id, DBAuthentication.password);
    if(connection != null) {

      String id = request.getParameter("id");

      //Connection 객체에 대해, 질의문 사용을 위한 statement 객체 생성

      String sql = "UPDATE webtoon SET Hits=Hits+1 WHERE Id=?";
      PreparedStatement statement = connection.prepareStatement(sql);

      statement.setString(1,id);            //  where절의 조건을 설정
      statement.executeUpdate();                      //쿼리 실행
      System.out.println("[Hits Page] ID : " + id  + " Connection successful");
   }
  }catch (SQLException ex){
    System.out.println("SQL Exception "+ex);
  }catch (Exception ex) {
    System.out.println("Exception " + ex);
  }
%>
