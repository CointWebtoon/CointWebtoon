<%@ page contentType="text/html;charset=EUC-KR" language="java" %>
<html>
<head>
    <title>Coint(KW Univ.)</title>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
    <style>
        @import url(http://fonts.googleapis.com/earlyaccess/jejugothic.css);
        body{
            background-color : whitesmoke;
            font-family: 'Jeju Gothic', serif;
        }
        h1{
            margin: 0;
            padding: 0;
            font-size: 70px;
            text-align: center;
        }
        #menubar{
            margin : 0 auto 0 auto;
            width : 1000px;
            height : 50px;
        }
        #menubar ul{
            padding-top : 10px;
            padding-bottom : 10px;
            list-style : none;
            margin: 0;
            text-align: center;
            border-radius: 10px;
            background-color : #32CDA0;
        }
        #menubar ul li{
            display: inline;
            border-left: 2px solid whitesmoke;
            color : white;
            font-size : 20px;
            text-decoration: none;
            padding-left: 10px;
            padding-right: 10px;
        }
        #menubar ul li:hover{
            color : #5f5f5f;
            cursor:pointer;
        }
        div.banner h3{
            margin : 0 auto 0 auto;
            text-align: center;
        }
        div.banner{
            padding-top : 30px;
            padding-bottom : 30px;
            border-radius: 10px;
            margin: 0 auto 0 auto;
            color : whitesmoke;
            background-color:#5f5f5f;
            width : 1000px;
        }
        div.container{
            border-radius: 10px;
            margin : 0 auto 0 auto;
            width : 1000px;
            height: 520px;
            background-color : blanchedalmond;
        }
        div.container div{
            margin : 0;
            padding: 10px;
        }
        .hidden{
            display: none;
        }
        .inline{
            float : left;
            display: inline;
        }

        .paragraph_container{
            width : 550px;
            height : 480px;
        }
        .teampic{
            width: 360px;
            height: 480px;
        }
        div span{
            font-size : 30px;
            color : #32CDA0;
        }
        div.developer p{
            line-height : 40px;
            font-size : 20px;
            text-align: center;
        }

        .apppic{
            width:288px;
            height: 450px;
        }
        div.project p{
            width : 650px;
            line-height : 25px;
            font-size : 20px;
            text-align: left;
        }

        .downloadapk{
            width: 1000px;
        }
        .downloadapk img{
            width: 950px;
            height: 340px;
            margin-left: 15px;
            margin-right: 20px;
            margin-top : 10px;
        }
        .downloadapk p{
            margin-top : 30px;
            line-height : 40px;
            font-size : 20px;
            text-align: left;
            margin-right: 30px;
            margin-left : 150px;
        }
        .downloadapk input{
            margin-top : 10px;
        }
    </style>
</head>
<body>
<div class="banner">
    <h1>COINT WEBTOON</h1>
    <h3 style="color : skyblue;">���� ���� ��</h3>
</div>
<nav id="menubar">
    <ul>
        <li id="developer">������ �Ұ�</li>
        <li id="project">������Ʈ �Ұ�</li>
        <li id="downloadapk" style="border-right:2px solid whitesmoke;">APK �ٿ�ε�</li>
    </ul>
</nav>
<div class="container">
    <div class="developer hidden">
        <div class="teampic inline">
            <img class="teampic" src="resources/team_picture.JPG" alt="TEAM COINT">
        </div>
        <div class="paragraph_container inline" style="text-align: center">
            <h2>COINT(<span>CO</span>ding <span>IN</span> the <span>T</span>rap)</h2>
            <p>
                ������б� ��ǻ�� ����Ʈ���� �а� ����<br><br>
                2013726011 ������(24)<br>
                2012726069 ������(26)<br>
                2012726072 �ռ���(25)<br>
                2012726006 ������(25)<br><br>
                <strong>������б� with Naver</strong><br>
                &lt;2016/10/01 ~ 2017/05/31&gt;<br>
                ���� ���� ������Ʈ ����<br>
            </p>
        </div>
    </div>

    <div class="project hidden">
        <div class="inline">
            <img class="apppic" src="resources/application.jpg" alt="Naver Webtoon Aplication">
        </div>
        <div class="paragraph_container inline" style="text-align: center">
            <h2><span>���ο�</span> ���̹� ���� ���� �����϶�!</h2>
            <p>
                �⺻�� ����� ȯ�濡�� ���񽺵ǰ� �ִ� Naver WebToon �۰� ���� ���� ���� �����ϴ� �����μ�,
                �� ������ ���� ���� content�� ����� ��(Android, iOS)���� �� �� �ִ� ���ø����̼��� �����Ѵ�.<br><br>
                <strong>���� ȯ��</strong><br>
                HTML5/CSS3/JavaScript/Android �Ǵ� iOS ����<br>Java/JSP/Tomcat/Apache ����<br>
                <strong>���� ����</strong><br>
                ������ ���� �ۺ��� �� ���� <span>����� ����</span>�̳� ����� �����ϸ� �������� �ο��ϰ� ���� ����/������ ������ �߽����� �ɻ縦 ������<br>
                <strong>���� ����</strong><br>���� �ڵ带 �״�� ����ϴ� ���� �������� ������ <span>��â��</span>�� �־�� ��. ���ø����̼��� ��ȹ�̳� ������, content�� ���� ������ ������ ���� ���� �������� Ȱ���� ���� ������ �ܺ� ������� ������ �Ұ���."
            </p>
        </div>
    </div>
    <div class="downloadapk hidden">
        <img src="resources/android.png">
        <p class="inline">
            <strong>���� ����</strong> : Lollipop ~ Nougat<br>
            <span>cointwebtoon.apk</span>
        </p>
        <a href="resources/cointwebtoon.apk" download>
            <input id="download" class="inline" type="image" src="resources/downloadbtn.png" width="400" height="120">
        </a>
    </div>
</div>
<p id="footer" style="text-align: center">KwanWoon Univ. Computer Software Team ��<strong>Coint</strong> Server</p>
<script>
    $(document).ready(function(){
        var $divs = $('div.container div');
        $('.project').removeClass('hidden');
        $('#project').css({color : "#5f5f5f"});
        $('#menubar ul li').on('click',function(){
            $('#menubar ul li').css({color : "whitesmoke"});
            $divs.addClass('hidden');
            $('.' + this.id).removeClass('hidden');
            $(this).css({color : "#5f5f5f"});
        });
        $('#download').on('click', function (e) {
            e.preventDefault();
            alert("���� ���Դϴ�. ������ �Ϸ� ������ ���ε� ����");
        });
    });
</script>
</body>
</html>
