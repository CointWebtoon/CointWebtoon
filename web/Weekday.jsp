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
<%@ page import="org.json.simple.JSONArray" %>
<%-- JSP���� JDBC�� ��ü�� ����ϱ� ���� java.sql ��Ű���� import�Ѵ�--%>
<%
  try {
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();

    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection connection = null;
    connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/coint_webtoon","root","dmswnqt1");          //���߿� �����ؾ� �� �κ�
    if(connection != null) {

      String weekday = request.getParameter("weekday");

      String sql = "SELECT  * FROM WEBTOON WHERE ID IN (SELECT ID_W FROM WEEKDAY WHERE Weekday=?)";
      PreparedStatement statement = connection.prepareStatement(sql);

      statement.setString(1,weekday);
      ResultSet resultSet = statement.executeQuery();                         // sql ������ ���� ����� resultSet�� ����

      int i=0;
      while(resultSet.next()){

        JSONObject object = new JSONObject();                                        // JSON������ ���� ��ü

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
