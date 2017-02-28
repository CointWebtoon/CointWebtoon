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
            out.println("������ �����ϴ�.");
        } else {
            final WebtoonParse parsingInstance = new WebtoonParse();
            ArrayList<Webtoon> webtoons = parsingInstance.getWebtoons();

            for (int i = finished; i <= sun; i++) {    //�����Ϻ��� �� ������ ������ �޾� ArrayList�� �߰�
                if (parsingInstance.getWebtoonArrayList(i) == false) {
                    out.print("�������� ����\r\n");
                    return;
                }
            }
            int genreTableInsertNum = 0;
            for (Webtoon webtoon : webtoons) {
                genreTableInsertNum += webtoon.getGenre().size();
                webtoon.printToon();
            }
            System.out.println("�� " + webtoons.size() + "���� ���� �ε� �Ϸ�");

            out.print("�� " + webtoons.size() + "�� ���� �ε� �Ϸ�\r\n�� " + genreTableInsertNum + "���� �帣 tuple ���� �Ϸ�\r\n");
            out.print("���� �˻�..............");
            if (parsingInstance.IsErrorOccurred())
                out.print("�Ϸ�.\r\n���� �߻� �ܼ� Ȯ��\r\n");
            else {
                out.print("�Ϸ�.\r\n���������� �ε�Ǿ����ϴ�.\r\n");
                if (parsingInstance.insertToDB()) {   //�ϼ��� ArrayList�� DB�� �ְ� ����� ���� ��� ���
                    out.print("DB ������Ʈ �Ϸ�");
                } else {
                    out.print("DB ������Ʈ ����");
                }
            }
        }
    } catch (Exception e) {
        out.println("������ �����ϴ�.");
    }
%>

</body>
</html>
