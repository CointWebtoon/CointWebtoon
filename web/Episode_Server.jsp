<%@ page import="importClasses.EpisodeParse" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.time.DayOfWeek" %>
<%@ page contentType="text/html;charset=EUC-KR" language="java" %>
<html>
<head>
    <title>$Episode Webtoon Parsing$</title>
</head>
<body>
<%!
    private String certification = "TEAM_COINT";
%>
<%
    try{
        if(!request.getHeader("certification").equals(certification)){
            out.println("권한이 없습니다.");
        }else {
            EpisodeParse parseInstance = new EpisodeParse();
            Calendar now = Calendar.getInstance();

            if(parseInstance.updateEpisodes(request.getParameter("update")))
                out.print("총 " + parseInstance.updateCount() + "개의 에피소드 업데이트 완료");
            else
                out.print("ERR OCCURRED");
        }
    }catch (Exception e){
        out.print("ERR OCCURRED, 권한이 없거나 동작 수행 중 오류가 발생했습니다.");
    }
%>
</body>
</html>
