import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.WindowsProcessManager;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import io.webfolder.cdp.type.network.Cookie;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static io.webfolder.cdp.session.SessionFactory.DEFAULT_PORT;

/**
 * Created by duanyb on 2017/2/17.
 */
public class GetCookie {
    public static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    public static void main(String[] args) {
        List<String> arr = new ArrayList<>();
//        arr.add("--headless");
//        arr.add("--disable-gpu");
        arr.add("--no-sandbox");
//                arr.add("--single-process");
        arr.add("--in-process-plugins");
        arr.add("--disable-popup-blocking");
        arr.add("--disable-images");
//        arr.add("--proxy-server=" + proxyInfo1.getProxyip() + ":" + proxyInfo1.getProxyport());
        String chars1 = "abcdefghijklmnopqrstuvwxyz";
        char c2 = chars1.charAt((int) (Math.random() * 26));
        String url1 = "http://weixin.sogou.com/weixin?type=1&query=yule&ie=utf8&_sug_=" + c2 + "&_sug_type_=";
        String str1[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        String cc = str1[(int) (Math.random() * 26)];
        cc = cc + (int) (Math.random() * 1000);
        url1 = String.format(url1, cc);
        getCookies(url1, arr);
    }


    private static void getCookies(String url, List<String> arguments) {
        Launcher launcher = new Launcher(getFreePort(DEFAULT_PORT));
        launcher.setProcessManager(new WindowsProcessManager());
        SessionFactory sessionFactory = launcher.launch(arguments);
        try {
            for (; ; ) {
//            SessionFactory sessionFactory = launcher.launch("C:\\Users\\lijie7\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe", arguments);
                sessionFactory.createBrowserContext();
                Session session = sessionFactory.create();
                session.getCommand().getNetwork().clearBrowserCookies();
                session.getCommand().getNetwork().clearBrowserCache();
                session.navigate(url);
                session.waitDocumentReady(60000);
                List<Cookie> cookies = session.getCommand().getNetwork().getAllCookies();
                for (Cookie cookie : cookies) {
                    int a = 0;
                    if (cookie.getName().equals("SNUID") && cookie.getExpires() != null) {
//                    sCookie.setSnuid(cookie.getValue());
                        a++;
                    } else if (cookie.getName().equals("SUID") && cookie.getExpires() != null) {
//                    sCookie.setSuid(cookie.getValue());
                        a++;
                    } else if (cookie.getName().equals("SUV") && cookie.getExpires() != null) {
//                    sCookie.setSuv(cookie.getValue());
                        a++;
                    } else {
                        continue;
                    }
                    if (a >= 3) {
                        System.out.println("OK");
                    }
                }

                session.close();
                continue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sessionFactory.close();
            launcher.getProcessManager().kill();
        }
    }

    protected static int getFreePort(int portNumber) {
        try (ServerSocket socket = new ServerSocket(portNumber)) {
            int freePort = socket.getLocalPort();
            return freePort;
        } catch (IOException e) {
            return getFreePort(portNumber + 1);
        }
    }
}
