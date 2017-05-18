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
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Date"%>
<%
	PreparedStatement statement = null;
	Connection connection = null;
  try {
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    Class.forName(DBAuthentication.driverName);
    connection = DriverManager.getConnection(DBAuthentication.url, DBAuthentication.id, DBAuthentication.password);
    System.out.println("Connection successful");
    if(connection != null) {

      String type = request.getParameter("type");
      String id = request.getParameter("id");
      String value = request.getParameter("value");
      String ep_id;
	System.out.println(value);
      if(type.equals("webtoon")){                               //��û ������ webtoon�� ��� Likes�� 1 �ø��� ������� resultSet�� �����Ѵ�
          String sql = "UPDATE webtoon SET Likes=Likes+1 WHERE Id=?";
	  String sqlMinus = "UPDATE webtoon SET Likes=Likes-1 WHERE Id=?";
	if(value.equals("plus")){
          statement = connection.prepareStatement(sql);
	}else if(value.equals("minus")){
          statement = connection.prepareStatement(sqlMinus);
	}else{
	System.out.println("[Like Page]Failed");
          statement = null;
	return;
	}
          statement.setString(1,id);
          statement.executeUpdate();
          System.out.println("[Like Page] ID : " + id + value + " Successful");
      }
      else if(type.equals("episode")){                           //��û ������ episode�� ��� Likes_E�� 1 �ø��� ������� resultSet�� �����Ѵ�
          ep_id = request.getParameter("ep_id");
          String sql = "UPDATE webtoon SET Likes=Likes+1 WHERE Id_E=? Episode_id=?";
	  String sqlMinus = "UPDATE webtoon SET Likes=Likes-1 WHERE Id_E=? Episode_id=?";
	if(value.equals("plus")){
          statement = connection.prepareStatement(sql);
	}else if(value.equals("minus")){
          statement = connection.prepareStatement(sqlMinus);
	}else{
	System.out.println("[Like Page]Failed");
          statement = null;
	return;
}
          statement.setString(1,id);
          statement.setString(2,ep_id);
          statement.executeUpdate();
          System.out.println("[Like Page] ID : " + id + " + Episode ID : " + ep_id + value + " Successful"  + new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a").format(new Date()).toString());
      }
   }
  }catch (SQLException ex){
    System.out.println("SQL Exception "+ex);
ex.printStackTrace();
  }catch (Exception ex) {
    System.out.println("Exception " + ex);
	ex.printStackTrace();
  }finally{
try{	connection.close();}catch(Exception e){}
try{statement.close();}catch(Exception e){}

}
%>
