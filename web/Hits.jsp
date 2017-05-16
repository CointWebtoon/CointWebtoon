<%--
        �ȵ���̵忡�� json�Ľ��Ҷ� <html>���� �±׺κе� ���� ������� �ٶ���
         contentType�� application/json���� �ٲ��־� �Ľ� ������ �ذ��߽��ϴ�.

        jsp �ּ� ���� : http://localhost:8080/Hits.jsp?id=679519
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

      //Connection ��ü�� ����, ���ǹ� ����� ���� statement ��ü ����

      String sql = "UPDATE webtoon SET Hits=Hits+1 WHERE Id=?";
      PreparedStatement statement = connection.prepareStatement(sql);

      statement.setString(1,id);            //  where���� ������ ����
      statement.executeUpdate();                      //���� ����
      System.out.println("[Hits Page] ID : " + id  + " Connection successful");
   }
  }catch (SQLException ex){
    System.out.println("SQL Exception "+ex);
  }catch (Exception ex) {
    System.out.println("Exception " + ex);
  }
%>
