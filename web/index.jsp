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
    <h3 style="color : skyblue;">서버 구동 중</h3>
</div>
<nav id="menubar">
    <ul>
        <li id="developer">개발자 소개</li>
        <li id="project">프로젝트 소개</li>
        <li id="downloadapk" style="border-right:2px solid whitesmoke;">APK 다운로드</li>
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
                광운대학교 컴퓨터 소프트웨어 학과 재학<br><br>
                2013726011 최은주(24)<br>
                2012726069 박종원(26)<br>
                2012726072 손석희(25)<br>
                2012726006 신정민(25)<br><br>
                <strong>광운대학교 with Naver</strong><br>
                &lt;2016/10/01 ~ 2017/05/31&gt;<br>
                산학 연계 프로젝트 진행<br>
            </p>
        </div>
    </div>

    <div class="project hidden">
        <div class="inline">
            <img class="apppic" src="resources/application.jpg" alt="Naver Webtoon Aplication">
        </div>
        <div class="paragraph_container inline" style="text-align: center">
            <h2><span>새로운</span> 네이버 웹툰 앱을 제작하라!</h2>
            <p>
                기본의 모바일 환경에서 서비스되고 있는 Naver WebToon 앱과 같은 웹툰 앱을 개발하는 과제로서,
                웹 서버를 통해 웹툰 content를 모바일 앱(Android, iOS)에서 볼 수 있는 애플리케이션을 개발한다.<br><br>
                <strong>개발 환경</strong><br>
                HTML5/CSS3/JavaScript/Android 또는 iOS 개발<br>Java/JSP/Tomcat/Apache 개발<br>
                <strong>개발 조건</strong><br>
                기존의 웹툰 앱보다 더 좋은 <span>사용자 경험</span>이나 기능을 제공하면 가산점을 부여하고 직접 설계/개발한 내용을 중심으로 심사를 진행함<br>
                <strong>제약 사항</strong><br>기존 코드를 그대로 사용하는 것은 인정하지 않으며 <span>독창성</span>이 있어야 함. 애플리케이션의 기획이나 디자인, content는 기존 웹툰의 내용을 내부 데모 목적으로 활용할 수는 있으나 외부 재배포나 공개는 불가함."
            </p>
        </div>
    </div>
    <div class="downloadapk hidden">
        <img src="resources/android.png">
        <p class="inline">
            <strong>지원 버전</strong> : Lollipop ~ Nougat<br>
            <span>cointwebtoon.apk</span>
        </p>
        <a href="resources/cointwebtoon.apk" download>
            <input id="download" class="inline" type="image" src="resources/downloadbtn.png" width="400" height="120">
        </a>
    </div>
</div>
<p id="footer" style="text-align: center">KwanWoon Univ. Computer Software Team ⓒ<strong>Coint</strong> Server</p>
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
            alert("개발 중입니다. 개발이 완료 시점에 업로드 예정");
        });
    });
</script>
</body>
</html>
