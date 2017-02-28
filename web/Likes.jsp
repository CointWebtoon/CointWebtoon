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
      String ep_id;

      PreparedStatement statement;
      ResultSet resultSet = null;

      if(type.equals("webtoon")){                               //��û ������ webtoon�� ��� Likes�� 1 �ø��� ������� resultSet�� �����Ѵ�

          String sql = "UPDATE webtoon SET Likes=Likes+1 WHERE Id=?";
          statement = connection.prepareStatement(sql);

          statement.setString(1,id);
          statement.executeUpdate();

          String result = "SELECT Likes FROM webtoon WHERE Id=?";
          statement = connection.prepareStatement(result);

          statement.setString(1,id);
          resultSet = statement.executeQuery();
      }
      else if(type.equals("episode")){                           //��û ������ episode�� ��� Likes_E�� 1 �ø��� ������� resultSet�� �����Ѵ�
          ep_id = request.getParameter("ep_id");
          String sql = "UPDATE episode SET Likes_E=Likes_E+1 WHERE Id_E=? AND Episode_id=?";
          statement = connection.prepareStatement(sql);

          statement.setString(1,id);
          statement.setString(2,ep_id);
          statement.executeUpdate();

          String result = "SELECT Likes_E FROM episode WHERE Id_E=? AND Episode_id=?";
          statement = connection.prepareStatement(result);

          statement.setString(1,id);
          statement.setString(2,ep_id);

          resultSet = statement.executeQuery();
      }

      while(resultSet.next()){                                                                  //������ ����� JSON���� �����.

          JSONObject object = new JSONObject();

          if(type.equals("webtoon")){
              object.put("likes", resultSet.getString("Likes"));
              out.print(resultSet.getString("Likes"));
          }else if(type.equals("episode")){
              object.put("likes", resultSet.getString("Likes_E"));
              out.print(resultSet.getString("Likes_E"));
          }

          jsonArray.add(0,object);
          jsonObject.put("result",jsonArray);
      }
   }
  }catch (SQLException ex){
    System.out.println("SQL Exception "+ex);
  }catch (Exception ex) {
    System.out.println("Exception " + ex);
  }
%>
