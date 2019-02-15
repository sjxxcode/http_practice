package com.sj.http_practice.Util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by SJ on 2019/2/15.
 */
public class NetUtil {

    private static final OkHttpClient httlClient;

    static {
        httlClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)

                .cache(createCacheFiles())

                .build();
    }

    private static Cache createCacheFiles(){
        final int cacheSize = 10 * 1024 * 1024; // 10 MiB
        final String cachePatch = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "2222222";

        File okhttpCache = new File(cachePatch);
        if(!okhttpCache.exists()){
            okhttpCache.mkdirs();
        }

        return new Cache(okhttpCache, cacheSize);
    }

    public static OkHttpClient getGlobalHttpClient(){
        return httlClient;
    }

    public static OkHttpClient.Builder getAddFiddlerCERHttlClient(Context context){
        try {
            InputStream caIn = context.getAssets().open("LocalFiddler.cer");

            return httlClient.newBuilder()
                    .sslSocketFactory(GetCA.getCertificates(caIn));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
