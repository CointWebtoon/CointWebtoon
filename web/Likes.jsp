<%--
        안드로이드에서 json파싱할때 <html>같은 태그부분도 같이 따라오는 바람에
         contentType을 application/json으로 바꿔주어 파싱 문제를 해결했습니다.

        jsp 주소 형식 : http://localhost:8080/Likes.jsp?type=episode&id=679519&ep_id=71
                또는         http://localhost:8080/Likes.jsp?type=webton&id=679519
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

      if(type.equals("webtoon")){                               //요청 형식이 webtoon인 경우 Likes를 1 올리고 결과물을 resultSet에 저장한다

          String sql = "UPDATE webtoon SET Likes=Likes+1 WHERE Id=?";
          statement = connection.prepareStatement(sql);

          statement.setString(1,id);
          statement.executeUpdate();

          String result = "SELECT Likes FROM webtoon WHERE Id=?";
          statement = connection.prepareStatement(result);

          statement.setString(1,id);
          resultSet = statement.executeQuery();
      }
      else if(type.equals("episode")){                           //요청 형식이 episode인 경우 Likes_E를 1 올리고 결과물을 resultSet에 저장한다
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

      while(resultSet.next()){                                                                  //저장한 결과를 JSON으로 만든다.

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
