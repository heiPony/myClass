package com.pony.test.wx;



import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class WxHttp {

    private static Logger logger = LoggerFactory.getLogger(WxHttp.class);

    public static JSONObject href(String url, String method, String... params) {
        StringBuffer bufferRes = new StringBuffer();
        try {
            URL realUrl = new URL(replaceURL(url, params));
            trustAllHttpsCertificates();
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 连接超时
            conn.setConnectTimeout(25000);
            // 读取超时 --服务器响应比较慢,增大时间
            conn.setReadTimeout(25000);
            HttpURLConnection.setFollowRedirects(true);
            // 请求方式
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            // out.write(URLEncoder.encode(params,"UTF-8"));
            out.write("");
            out.flush();
            out.close();
            InputStream in = conn.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String valueString = null;
            while ((valueString = read.readLine()) != null) {
                bufferRes.append(valueString);
            }
            in.close();
            if (conn != null) {
                conn.disconnect();
            }
            return JSONObject.fromObject(bufferRes.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 更换url
     * @Author huangzhanping
     * @param [url, params]
     * @return 2020/6/11 15:39
     */
    public static String replaceURL(String url, String... params) {
        for (String param : params) {
            url = url.replaceFirst("\\{.*?\\}", param);
        }
        logger.info("replace url:{}", url);
        return url;
    }

    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
}
