<%@ page contentType="text/html;charset=EUC-KR" language="java" %>
<%@ page import="java.sql.*"%>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="importClasses.DBAuthentication" %>

<%
    /* Variables */
    /* For getting data from database */
    Connection conn = null;
    PreparedStatement  pstmt = null;
    ResultSet rs = null;

    /* For JSON generation */
    int resultCount = 0;
    JSONObject jsonResult = new JSONObject();
    JSONArray jsonArray = new JSONArray();

    try {
        /* Corresponding development environment is required for DB access */
        Class.forName(DBAuthentication.driverName);
        conn = DriverManager.getConnection(DBAuthentication.url, DBAuthentication.id, DBAuthentication.password);

        if(conn != null){
            /* Query statement (Need to modify as needed) */
            String id = request.getParameter("id");
            String sql = "SELECT * " +
                         "FROM WEBTOON WHERE Mobile_unsupported=0;";

            /* Query request */
            pstmt= conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            /* Create JSON */
            while(rs.next()){
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("Id", rs.getString("Id"));
                jsonObject.put("Title", rs.getString("Title"));
                jsonObject.put("Artist", rs.getString("Artist"));
                jsonObject.put("Starscore", rs.getString("Starscore"));
                jsonObject.put("Hits", rs.getString("Hits"));
                jsonObject.put("Thumburl", rs.getString("Thumburl"));
                jsonObject.put("Likes", rs.getString("Likes"));
                jsonObject.put("Toontype", rs.getString("Toontype"));
                jsonObject.put("Is_adult", rs.getString("Is_adult"));
                jsonObject.put("Is_charged", rs.getString("Is_charged"));
                jsonObject.put("Is_updated", rs.getString("Is_updated"));

                jsonArray.add(resultCount, jsonObject); resultCount++;
            }
            jsonResult.put("list_total_count", resultCount);
            jsonResult.put("result", jsonArray);

            out.println(jsonResult);
            out.flush();
            System.out.println("[Weboon List Page] Connection successful");
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Connection failure");
    } finally {
        if (rs != null) try {rs.close();} catch (SQLException e) {}
        if (pstmt != null) try {pstmt.close();} catch (SQLException e) {}
        if (conn != null) try {conn.close();} catch (SQLException e) {}
    }
%>