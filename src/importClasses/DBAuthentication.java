package importClasses;

public class DBAuthentication {
    public static String driverName = "com.mysql.jdbc.Driver";
    private static String form = "jdbc:mysql://";
    private static String ip = "localhost";
    private static String DBName = "COINT_WEBTOON";
    private static String port = "3306";
    public static String id = "TEAM_COINT";
    public static String password = "131123";
    public static String url = form + ip + ":" + port + "/" + DBName + "?autoReconnect=true&useSSL=false";
}
