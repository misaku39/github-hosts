package com.misaku.util;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author misaku
 * @since 2021/2/22 14:32
 */
public class HttpClientUtil {

    public  static HttpClientContext context = null;

    static {
        System.out.println("====================begin");
        /*为了保证发送多个请求是同一个浏览器*/
        context = HttpClientContext.create();
    }
    /*url*/
    private String url;
    /*post参数*/
    private Map<String, String> param;
    /*响应的状态码*/
    private int statusCode;
    /*请求的内容*/
    private String content;
    private String xmlParam;
    private boolean isHttps;

    public void reset() {
        this.url = null;
        this.statusCode = 0;
        this.content = null;
        this.param = null;
    }



    public boolean isHttps() {
        return isHttps;
    }

    public void setHttps(boolean isHttps) {
        this.isHttps = isHttps;
    }

    public String getXmlParam() {
        return xmlParam;
    }

    public void setXmlParam(String xmlParam) {
        this.xmlParam = xmlParam;
    }

    /*post的构造方法*/
    public HttpClientUtil(String url, Map<String, String> param) {
        this.url = url;
        this.param = param;
    }

    /*get的构造方法 url get的参数就在url中*/
    public HttpClientUtil(String url) {
        this.url = url;
    }

    /*post添加参数*/
    public void setParameter(Map<String, String> map) {
        param = map;
    }

    /*添加一个参数*/
    public void addParameter(String key, String value) {
        if (param == null)
            param = new HashMap<String, String>();
        param.put(key, value);
    }

    /*post执行*/
    public void post() throws ClientProtocolException, IOException {
        HttpPost http = new HttpPost(url);
        setReqHeaders(http);
        setEntity(http);
        execute(http);
    }

    /*put执行*/
    public void put() throws ClientProtocolException, IOException {
        HttpPut http = new HttpPut(url);
        setReqHeaders(http);
        setEntity(http);
        execute(http);
    }

    /*get 执行 */
    public void get() throws ClientProtocolException, IOException {
        if (param != null) {
            StringBuilder url = new StringBuilder(this.url);
            boolean isFirst = true;
            for (String key : param.keySet()) {
                if (isFirst)
                    url.append("?");
                else
                    url.append("&");
                url.append(key).append("=").append(param.get(key));
            }
            this.url = url.toString();
        }
        HttpGet http = new HttpGet(url);
        setReqHeaders(http);
        execute(http);
    }

    /**
     * set http post,put param
     */
    private void setEntity(HttpEntityEnclosingRequestBase http) {
        if (param != null) {
            List<NameValuePair> nvps = new LinkedList<NameValuePair>();
            for (String key : param.keySet())
                nvps.add(new BasicNameValuePair(key, param.get(key))); // 参数
            http.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8)); // 设置参数
        }
        if (xmlParam != null) {
            http.setEntity(new StringEntity(xmlParam, Consts.UTF_8));
        }
    }

    /*https*/
    private void execute(HttpUriRequest http) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = null;
        try {
            if (isHttps) {
                SSLContext sslContext = new SSLContextBuilder()
                        .loadTrustMaterial(null, new TrustStrategy() {
                            // 信任所有
                            public boolean isTrusted(X509Certificate[] chain,
                                                     String authType)
                                    throws CertificateException {
                                return true;
                            }
                        }).build();
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            } else {
                httpClient = HttpClients.createDefault();
            }
            CloseableHttpResponse response = httpClient.execute(http,context);
            try {
                if (response != null) {
                    if (response.getStatusLine() != null)
                        statusCode = response.getStatusLine().getStatusCode();
                    HttpEntity entity = response.getEntity();
                    // 响应内容
                    content = EntityUtils.toString(entity, Consts.UTF_8);
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
        }
    }

    /*得到状态码*/
    public int getStatusCode() {
        return statusCode;
    }

    /*得到内容*/
    public String getContent() throws ParseException, IOException {
        return content;
    }

    /**
     * 设置请求头
     */
    private void setReqHeaders(HttpRequestBase http) {
        //        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";
        http.addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
        //http.addHeader("User-Agent","        \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36\";\n");
    }





}
