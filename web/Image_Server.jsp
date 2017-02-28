<%@ page import="importClasses.ImageURLParse" %>
<%@ page contentType="text/html;charset=EUC-KR" language="java" %>
<html>
<head>
    <title>Parse and save ImageURL data</title>
</head>
<body>
<%!
    private String certification = "TEAM_COINT";
%>
<%
    try{
        if(!request.getHeader("certification").equals(certification)){
            out.println("권한이 없습니다.");
            return;
        } else {
            ImageURLParse imageURLParse = new ImageURLParse(out);
            imageURLParse.updateImages(request.getParameter("update"));
        }
    } catch (Exception e){
        out.println("권한이 없습니다.");
    }
%>
</body>
</html>
