package com.pc.wx.http;

import com.pc.wx.po.WxProtocol;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 16:24)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class HttpUtil {
    private static HttpClientBuilder builder;
    private static final Pattern regexp = Pattern.compile("([a-z_]+)=(\\w+)");
    public static String serverAddr;
//    public static int serverPort;
    static {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        initHttpClient();
    }

    static void initHttpClient() {
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                if (i > 3)                 //请求失败重试次数
                    return false;
                if (e instanceof NoHttpResponseException)
                    return true;
                if (e instanceof UnknownHostException)
                    return false;
                if (e instanceof InterruptedIOException)
                    return false;
                if (e instanceof ConnectException)
                    return false;
                if (e instanceof SSLException)
                    return false;
                if (!new Boolean(String.valueOf(httpContext.getAttribute(HttpCoreContext.HTTP_REQ_SENT))))
                    return true;
                return false;

            }
        };
        SSLConnectionSocketFactory sssf = null;
        try {
            sssf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SSL Connection could not be Initialized,Default SSLContext");
            e.printStackTrace();
        }
        ;
        PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager(3000L, TimeUnit.MILLISECONDS);
        pcm.setMaxTotal(20);
        pcm.setDefaultMaxPerRoute(128);
        builder = HttpClients.custom();
        builder.setRetryHandler(retryHandler);
        builder.setConnectionManager(pcm);
        builder.setDefaultHeaders(Arrays.asList(new BasicHeader("Content-Type", "application/json; charset=utf-8"), new BasicHeader("Connection", "keep-alive")));
    }

    public static HttpClient getHttpClient(boolean ssl) {
        SSLContext ctx = null;
        SSLSocketFactory ssf = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] arg0,
                                               String arg1) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] arg0,
                                               String arg1) throws CertificateException {
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            ssf = new SSLSocketFactory(ctx,
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return ssl ? builder.setSSLSocketFactory(ssf).build() : builder.build();
    }

    public static String execute(WxProtocol wxProtocol, final Map<String, Object> params) {
        String resultData = "";
        try {
            String url = genGetURL(wxProtocol.getUrl(), params);
            HttpUriRequest request = new HttpGet(url);
            if (wxProtocol.getPostParams() != null) {
                request = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                params.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v.toString())));
                ((HttpPost) request).setEntity(EntityBuilder.create().setParameters(nameValuePairs).setContentEncoding("UTF-8").build());
            }
            HttpEntity entity = getHttpClient(url.startsWith("https")).execute(request).getEntity();
//            if (convertType.equalsIgnoreCase("string"))//传输类型
            resultData = EntityUtils.toString(entity, "UTF-8");
//            else resultData = new String(EntityUtils.toByteArray(entity), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    /*    public static String execute(WxProtocol wxProtocol, String convertType) {
            String resultData = "";
            try {
                request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + charset);
                HttpEntity entity = getHttpClient(request.getURI().getScheme().startsWith("https")).execute(request).getEntity();
                if (convertType.equalsIgnoreCase("string"))//传输类型
                    resultData = EntityUtils.toString(entity, charset);
                else resultData = new String(EntityUtils.toByteArray(entity), charset);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultData;
        }*/
    public static String genGetURL(String sUrl, final Map<String, Object> params) {
        Matcher matcher = regexp.matcher(sUrl);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String k = matcher.group(1);
//            String v = matcher.group(2);
            String n = params.get(k).toString();
            if (StringUtils.isNotEmpty(n))
                matcher.appendReplacement(sb, "$1=" + n);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}