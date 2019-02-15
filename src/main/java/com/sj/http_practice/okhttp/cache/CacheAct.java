package com.sj.http_practice.okhttp.cache;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.sj.http_practice.R;
import com.sj.http_practice.Util.GetCA;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SJ on 2019/2/14.
 */
public class CacheAct extends AppCompatActivity{

    private String okhttpCachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "2222222";
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_cache);

        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(createCacheFiles(), cacheSize);

        InputStream caIn = null;
        try {
            caIn = getAssets().open("LocalFiddler.cer");

            //
            this.okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .sslSocketFactory(GetCA.getCertificates(caIn)).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        this.okHttpClient = new OkHttpClient.Builder()
//                .cache(cache)
//                .build();




        this.findViewById(R.id.clear_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCacheFiles();
            }
        });

        this.findViewById(R.id.request_net).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStr(CacheControl.FORCE_NETWORK);
            }
        });

        this.findViewById(R.id.request_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStr(CacheControl.FORCE_CACHE);
            }
        });
    }

    private File createCacheFiles(){
        File okhttpCache = new File(okhttpCachePath);
        if(!okhttpCache.exists()){
            okhttpCache.mkdirs();
        }

        return okhttpCache;
    }

    private void deleteCacheFiles(){
        File okhttpCache = new File(okhttpCachePath);
        Cache cache = new Cache(okhttpCache, 10);

        try {
            cache.delete();

            okhttpCache = new File(okhttpCachePath);

            Log.e("===Cache", "delet cache files success:" + okhttpCache.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postStr(final CacheControl cacheControl) {
        Single.create(new SingleOnSubscribe<CacheAct.Result>() {
            @Override
            public void subscribe(SingleEmitter<CacheAct.Result> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url("https://publicobject.com/helloworld.txt")
                        .cacheControl(cacheControl)
                        //.header("Cache-Control","max-age=10")
                        //.header("Cache-Control","no-store")
                        //.header("Cache-Control","no-cache")
                        .build();

                Response response = okHttpClient.newCall(request).execute();

                Result result = new Result();
                result.body = "Response response:          " + response.body().string();
                result.body += "\nResponse cache response:    " + response.cacheResponse();
                result.body += "\nResponse network response:  " + response.networkResponse();

                emitter.onSuccess(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CacheAct.Result>() {
                    @Override
                    public void accept(CacheAct.Result result) throws Exception {
                        Log.e("===Cache", result.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("===Cache", throwable.getMessage());
                    }
                });
    }

    private static class Result{
        String str = "";
        String body = "";

        @Override
        public String toString() {
            return str + "\n" + body;
        }
    }
}
