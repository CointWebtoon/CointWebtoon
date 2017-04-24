<%--
  User: EunJoo Choi
  Date: 2017-02-05
  Time: ���� 8:02
  To change this template use File | Settings | File Templates.
--%>

<%--
        �ȵ���̵忡�� json�Ľ��Ҷ� <html>���� �±׺κе� ���� ������� �ٶ���
         contentType�� application/json���� �ٲ��־� �Ľ� ������ �ذ��߽��ϴ�.

        jsp �ּ� ���� : http://localhost:8080/Likes.jsp?weekday=3
--%>
<%@ page contentType="application/json;charset=EUC-KR" language="java" %>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %><%@ page import="importClasses.DBAuthentication"%>
<%-- JSP���� JDBC�� ��ü�� ����ϱ� ���� java.sql ��Ű���� import�Ѵ�--%>
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

      ResultSet resultSet = statement.executeQuery();                         // sql ������ ���� ����� resultSet�� ����

      int i=0;
      while(resultSet.next()){

        JSONObject object = new JSONObject();                                        // JSON������ ���� ��ü

        object.put("id",resultSet.getString("Id_G"));
        object.put("genre",resultSet.getString("Genre"));

        jsonArray.add(i,object);
        jsonObject.put("result",jsonArray);                                                 // JSON�� ���� ����

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
