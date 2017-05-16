<%--
        �ȵ���̵忡�� json�Ľ��Ҷ� <html>���� �±׺κе� ���� ������� �ٶ���
         contentType�� application/json���� �ٲ��־� �Ľ� ������ �ذ��߽��ϴ�.

        jsp �ּ� ���� : http://localhost:8080/Likes.jsp?type=episode&id=679519&ep_id=71
                �Ǵ�         http://localhost:8080/Likes.jsp?type=webton&id=679519
--%>


<%@ page contentType="application/json;charset=EUC-KR" language="java" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="java.sql.*" %><%@ page import="importClasses.DBAuthentication"%>

<%
  try {
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    Class.forName(DBAuthentication.driverName);
    Connection connection = DriverManager.getConnection(DBAuthentication.url, DBAuthentication.id, DBAuthentication.password);
    System.out.println("Connection successful");
    if(connection != null) {

      String type = request.getParameter("type");
      String id = request.getParameter("id");
      String value = request.getParameter("value");
      String ep_id;

      PreparedStatement statement;
      ResultSet resultSet = null;
      int plusValue = Integer.parseInt(value);

      if(type.equals("webtoon")){                               //��û ������ webtoon�� ��� Likes�� 1 �ø��� ������� resultSet�� �����Ѵ�

          String sql = "UPDATE webtoon SET Likes=Likes+" + plusValue + " WHERE Id=?";
          statement = connection.prepareStatement(sql);

          statement.setString(1,id);
          statement.executeUpdate();
          System.out.println("[Like Page] ID : " + id + " + " + String.valueOf(plusValue) + " Successful");
      }
      else if(type.equals("episode")){                           //��û ������ episode�� ��� Likes_E�� 1 �ø��� ������� resultSet�� �����Ѵ�
          ep_id = request.getParameter("ep_id");
          String sql = "UPDATE episode SET Likes_E=Likes_E"+ plusValue + " WHERE Id_E=? AND Episode_id=?";
          statement = connection.prepareStatement(sql);

          statement.setString(1,id);
          statement.setString(2,ep_id);
          statement.executeUpdate();
          System.out.println("[Like Page] ID : " + id + " + Episode ID : " + ep_id + " + " + String.valueOf(plusValue) + " Successful");
      }
   }
  }catch (SQLException ex){
    System.out.println("SQL Exception "+ex);
  }catch (Exception ex) {
    System.out.println("Exception " + ex);
  }
%>
