package com.ty.Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ty.controller.StockInfoController;
import com.ty.entity.AccountRecord;
import com.ty.repository.AccountRecordRepository;
import com.ty.repository.TseDailyAvgInfoRepository;
import com.ty.repository.TseDailyBaseInfoRepository;
import com.ty.repository.StockMainRepository;
import com.ty.service.AccountService;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@Service
public class HttpUtils{

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final int timeout = 30;

    private static final String tse_month_queryUrl = "http://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json";

    private static final String otc_month_queryUrl = "http://www.tpex.org.tw/web/stock/aftertrading/daily_trading_info/st43_result.php?l=zh-tw";

    private static final String finmind_stock_priceInfo_queryUrl = "https://api.finmindtrade.com/api/v4/data?dataset=TaiwanStockPrice&data_id=";

    private static final String finmind_stock_legalInfo_queryUrl = "https://api.finmindtrade.com/api/v4/data?dataset=TaiwanStockInstitutionalInvestorsBuySell&data_id=";

    private static final String finmind_token_google = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkYXRlIjoiMjAyMS0wNy0xNCAxNTowNjo0MSIsInVzZXJfaWQiOiJ0bWFpYXM2NjYiLCJpcCI6IjEwMS45LjczLjE5NiJ9.Xi8EkhINXTIhWDpo1npejM5J4A6US08RXp1aMlGMW4w";

    private static final String finmind_token_google2 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkYXRlIjoiMjAyMS0wNy0xNSAxODowNDozNiIsInVzZXJfaWQiOiJ0b21teWxpbiIsImlwIjoiMTAxLjkuNDUuMTY1In0.v07-Ajm-Wxu3N3eDpCKcHRcPi8DaV5QaUOEbjZFk2ww";

    private static final String finmind_token_yahoo = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkYXRlIjoiMjAyMS0wNy0xNCAxNzoyODoxMiIsInVzZXJfaWQiOiJ0bWFpYXM2NjY2IiwiaXAiOiIxMDEuOS42OC41In0.Wc46Sk-y5yO971ML4qUqEYuZAh3F5ImIXTRsvfp05No";

    public static String getStockLegalInfoByFinmind(String stockNo, String startDate, String endDate) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException{
        String url = finmind_stock_legalInfo_queryUrl + stockNo + "&start_date=" + startDate + "&end_date=" + endDate + "&token=" + finmind_token_yahoo;
        logger.info("finmind stock legal request url: " + url);
        CloseableHttpClient client = generateClient();
        HttpUriRequest httpGet = new HttpGet(url);
        CloseableHttpResponse response = client.execute(httpGet);
        String entityStr = "";
        if (response.getStatusLine().getStatusCode() == 200){
            HttpEntity entity = response.getEntity();
            if (entity != null){
                entityStr = EntityUtils.toString(entity, "UTF-8");
            }
        }
        return entityStr;
    }

    public static String getStockPriceInfoByFinmind(String stockNo, String startDate, String endDate) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException{
        String url = finmind_stock_priceInfo_queryUrl + stockNo + "&start_date=" + startDate + "&end_date=" + endDate + "&token=" + finmind_token_yahoo;
        logger.info("finmind stock price request url: " + url);
        CloseableHttpClient client = generateClient();
        HttpUriRequest httpGet = new HttpGet(url);
        CloseableHttpResponse response = client.execute(httpGet);
        String entityStr = "";
        if (response.getStatusLine().getStatusCode() == 200){
            HttpEntity entity = response.getEntity();
            if (entity != null){
                entityStr = EntityUtils.toString(entity, "UTF-8");
            }
        }
        return entityStr;
    }

    public static String getTSEStockInfoByCodeAndDate(String stockNo, String dateTime) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException{
        String url = tse_month_queryUrl + "&date=" + dateTime + "&stockNo=" + stockNo;
        logger.info("request url: ", url);
        CloseableHttpClient client = generateClient();
        HttpUriRequest httpGet = new HttpGet(url);
        CloseableHttpResponse response = client.execute(httpGet);
        String entityStr = "";
        if (response.getStatusLine().getStatusCode() == 200){
            HttpEntity entity = response.getEntity();
            if (entity != null){
                entityStr = EntityUtils.toString(entity, "UTF-8");
                logger.info(entityStr);
            }
        }
        return entityStr;
    }

    public static String getOTCStockInfoByCodeAndDate(String stockNo, String dateTime) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException{
        String url = otc_month_queryUrl + "&d=" + dateTime + "&stkno=" + stockNo;
        logger.info("request url: ", url);
        CloseableHttpClient client = generateClient();
        HttpUriRequest httpGet = new HttpGet(url);
        CloseableHttpResponse response = client.execute(httpGet);
        String entityStr = "";
        if (response.getStatusLine().getStatusCode() == 200){
            HttpEntity entity = response.getEntity();
            if (entity != null){
                entityStr = EntityUtils.toString(entity, "UTF-8");
                logger.info(entityStr);
            }
        }
        return entityStr;
    }

    private static CloseableHttpClient generateClient() throws NoSuchAlgorithmException, KeyManagementException{
        // 請求重試機制，預設重試3次
        HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler(){

            public boolean retryRequest(IOException exception, int executionCount, HttpContext context){
                if (executionCount >= 3){
                    return false;
                }
                if (exception instanceof InterruptedIOException){
                    return false;
                }
                if (exception instanceof UnknownHostException){
                    return false;
                }
                if (exception instanceof ConnectTimeoutException){
                    return false;
                }
                if (exception instanceof SSLException){
                    // SSL handshake exception
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !( request instanceof HttpEntityEnclosingRequest );
                if (idempotent){
                    // Retry if the request is considered idempotent
                    return true;
                }
                return false;
            }
        };
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[]{
            new X509TrustManager(){

                public X509Certificate[] getAcceptedIssuers(){
                    logger.info("getAcceptedIssuers =============");
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType){
                    logger.info("checkClientTrusted =============");
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType){
                    logger.info("checkServerTrusted =============");
                }
            }
        }, new SecureRandom());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
        // PoolingHttpClientConnectionManager
        PoolingHttpClientConnectionManager pm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        pm.setMaxTotal(2000);
        pm.setDefaultMaxPerRoute(500);
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout * 1000).setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).build();
        return HttpClientBuilder.create().setConnectionManager(pm).setDefaultRequestConfig(defaultRequestConfig).setRetryHandler(myRetryHandler).build();
    }
}
