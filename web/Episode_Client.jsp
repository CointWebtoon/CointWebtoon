<%@ page contentType="application/json;charset=EUC-KR" language="java" %>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="java.sql.*" %>
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
        conn = DriverManager.getConnection(DBAuthentication.url , DBAuthentication.id, DBAuthentication.password);

        if(conn != null){
            String id = request.getParameter("id");
            /* Query statement (Need to modify as needed) */
            String sql = "SELECT * " +
                    "FROM EPISODE " +
                    "WHERE id_E = " + id + ";";

            /* Query request */
            pstmt= conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            /* Create JSON */
            while(rs.next()){
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("Id_E", rs.getString("Id_E"));
                jsonObject.put("Episode_id", rs.getString("Episode_id"));
                jsonObject.put("Episode_title", rs.getString("Episode_title"));
                jsonObject.put("Ep_starscore", rs.getString("Ep_starscore"));
                jsonObject.put("Ep_thumburl", rs.getString("Ep_thumburl"));
                jsonObject.put("Reg_date", rs.getString("Reg_date"));
                jsonObject.put("Mention", rs.getString("Mention"));
                jsonObject.put("Likes_E", rs.getString("Likes_E"));

                jsonArray.add(resultCount, jsonObject); resultCount++;
            }
            jsonResult.put("list_total_count", resultCount);
            jsonResult.put("result", jsonArray);

            out.println(jsonResult);
            out.flush();
            System.out.println("[Episode Page] ID : " + id  + " Connection successful");
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