package importClasses;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * 성인 웹툰 이미지를 가져오기 위해 네이버에 로그인 하기 위해 만든 클래스
 */
public class NaverLogin{
    public static Map<String,String> cookies;   //로그인 성공 시 쿠키가 담길 Map
    private String URL_LOGIN ;    //로그인 하는 url. 네이버 메인 페이지의 로그인 부분의 url이다
    private boolean isMobile;
    private boolean isLogin;
    private WebClient webClient;
    private HtmlPage currPage;

    public Map<String,String> getCookies() {
        return makeLoginCookie();
    }

    public boolean isLogin() {
        return isLogin;
    }

    public NaverLogin(String id, String pw, boolean isMobile) throws Exception {
        this.isMobile = isMobile;
        URL_LOGIN = isMobile? "https://nid.naver.com/nidlogin.login" :  "http://static.nid.naver.com/login.nhn";
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.waitForBackgroundJavaScript(5000);
        if(isMobile){
            if(!mobileLongin(id, pw)){
                isLogin = false;
                throw new Exception("cannot login with the id and pw");
            }else{
                isLogin = true;
            }
        }else {
            if(!login(id, pw)) {
                isLogin = false;
                throw new Exception("cannot login with the id and pw");
            } else {
                isLogin = true;
            }
        }
    }

    private Map<String,String> makeLoginCookie() {
        cookies = new HashMap<>();
        CookieManager cookieManager = webClient.getCookieManager();
        Set<Cookie> cookieSet = cookieManager.getCookies();
        for(Cookie c : cookieSet) {
            cookies.put(c.getName(), c.getValue());
        }
        return cookies;
    }

    private boolean login(String naverId, String naverPw) throws Exception {
        currPage = webClient.getPage(URL_LOGIN);
        HtmlForm form = currPage.getFormByName("frmNIDLogin");
        HtmlTextInput inputId = form.getInputByName("id");
        HtmlPasswordInput inputPw = (HtmlPasswordInput)form.getInputByName("pw");
        HtmlImageInput button = (HtmlImageInput)form.getFirstByXPath("//input[@alt='로그인']");
        inputId.setValueAttribute(naverId);
        inputPw.setValueAttribute(naverPw);
        currPage = (HtmlPage)button.click();
        return !currPage.asText().contains("Naver Sign in");
    }

    private boolean mobileLongin(String naverId, String naverPw) throws Exception{
        currPage = webClient.getPage(URL_LOGIN);
        HtmlForm form = currPage.getFormByName("frmNIDLogin");
        HtmlTextInput inputId = form.getInputByName("id");
        HtmlPasswordInput inputPw = (HtmlPasswordInput)form.getInputByName("pw");
        HtmlSubmitInput button = (HtmlSubmitInput)form.getFirstByXPath("//input[@type='submit']");
        inputId.setValueAttribute(naverId);
        inputPw.setValueAttribute(naverPw);
        currPage = (HtmlPage)button.click();
        return !currPage.asText().contains("Naver Sign in");
    }
}