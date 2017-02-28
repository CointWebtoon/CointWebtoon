<%@ page import="importClasses.Webtoon" %>
<%@ page import="importClasses.WebtoonParse" %>
<%@ page contentType="text/html;charset=EUC-KR" language="java" %>
<%@ page import="java.util.ArrayList" %>
<html>
<head>
    <title>Weekly Webtoon Parsing</title>
</head>
<body>
<%!
private static final int
        finished = 0,
        sun = 7;
    private String certification = "TEAM_COINT";
%>

<%
    try {
        if (!request.getHeader("certification").equals(certification)) {
            out.println("권한이 없습니다.");
        } else {
            final WebtoonParse parsingInstance = new WebtoonParse();
            ArrayList<Webtoon> webtoons = parsingInstance.getWebtoons();

            for (int i = finished; i <= sun; i++) {    //월요일부터 각 요일의 웹툰을 받아 ArrayList에 추가
                if (parsingInstance.getWebtoonArrayList(i) == false) {
                    out.print("가져오기 에러\r\n");
                    return;
                }
            }
            int genreTableInsertNum = 0;
            for (Webtoon webtoon : webtoons) {
                genreTableInsertNum += webtoon.getGenre().size();
                webtoon.printToon();
            }
            System.out.println("총 " + webtoons.size() + "개의 웹툰 로드 완료");

            out.print("총 " + webtoons.size() + "개 웹툰 로드 완료\r\n총 " + genreTableInsertNum + "개의 장르 tuple 생성 완료\r\n");
            out.print("에러 검사..............");
            if (parsingInstance.IsErrorOccurred())
                out.print("완료.\r\n에러 발생 콘솔 확인\r\n");
            else {
                out.print("완료.\r\n정상적으로 로드되었습니다.\r\n");
                if (parsingInstance.insertToDB()) {   //완성된 ArrayList를 DB에 넣고 결과에 따라 결과 출력
                    out.print("DB 업데이트 완료");
                } else {
                    out.print("DB 업데이트 실패");
                }
            }
        }
    } catch (Exception e) {
        out.println("권한이 없습니다.");
    }
%>

</body>
</html>
