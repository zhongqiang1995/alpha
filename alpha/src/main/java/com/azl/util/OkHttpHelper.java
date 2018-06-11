package com.azl.util;

import android.os.Environment;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by zhong on 2017/7/11.
 */

public class OkHttpHelper {
    private static OkHttpClient mClient;

    public static OkHttpClient getClient() {
        if (mClient == null) {
            synchronized (OkHttpHelper.class) {
                if (mClient == null) {
                    mClient = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .sslSocketFactory(getSSL(), getTrustManager())
                            .hostnameVerifier(getVerifier())
//                            .cache(new Cache(new File(Environment.getExternalStorageDirectory() + "/obs/cache"), 1024 * 1024 * 20))
                            .retryOnConnectionFailure(true)
                            .followRedirects(false)
                            .build();
                }

            }
        }
        return mClient;
    }

    private static X509TrustManager getTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
    }

    private static SSLSocketFactory getSSL() {

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{getTrustManager()}, new SecureRandom());

            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HostnameVerifier getVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

}
