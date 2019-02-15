package com.sj.http_practice.okhttp.timeout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sj.http_practice.R;
import com.sj.http_practice.Util.NetUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 超时
 * -----------------------------------------------------
 * 经过测试发现一个有趣的小问题
 * okhttp给的官方测试api"http://httpbin.org/delay/10",原来是设置的"read timeout"
 * 通过callTimeout()与connectTimeout()、writeTimeout()、readTimeout()综合测试得出的这个结论
 *
 * ------------------------------------------------------
 * 知识点
 *
 * http请求过程包括哪些超时
 *--TCP连接超时
 *--client向server发送请求报文超时(写超时)
 *--server向client发送响应报文超时(读超时)
 *
 * okhttp如何设置这些超时
 *--callTimeout():在一次http通信过程中从请求发出到得到server发回给client响应报文的最大限制时间
 *---这个最大时间包括:
 *----DNS ip地址解析
 *----Tcp连接
 *----发送请求报文
 *----服务端处理报文数据
 *----客户端读取响应报文数据
 *----重试或者重定向时间。
 *--connectTimeout():设置TCP连接超时时间。
 *--writeTimeout():写超时。
 *--readTimeout():读超时。
 *
 * Created by SJ on 2019/2/14.
 */
public class TimeOutAct extends AppCompatActivity{

    private final String TAG = "===" + this.getClass().getSimpleName();

    private long netStartTime;

    private TextView text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_timeout);

        this.text = this.findViewById(R.id.text);

        this.findViewById(R.id.timeout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestInfo();
            }
        });
    }

    private void requestInfo() {
        Single.create(new SingleOnSubscribe<TimeOutAct.Result>() {
            @Override
            public void subscribe(SingleEmitter<TimeOutAct.Result> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url("http://httpbin.org/delay/15")
                        .build();

                OkHttpClient okHttpClient = NetUtil.getGlobalHttpClient().newBuilder()
                        .callTimeout(5, TimeUnit.SECONDS)
                        //.connectTimeout(7, TimeUnit.SECONDS)
                        //.writeTimeout(6, TimeUnit.SECONDS)
                        .readTimeout(2, TimeUnit.SECONDS)
                        .build();

                netStartTime = System.currentTimeMillis();
                Log.e(TAG, netStartTime + "");

                Response response = okHttpClient.newCall(request).execute();

                String callTime = String.valueOf(System.nanoTime() - netStartTime);

                Result result = new Result();
                result.body = callTime + "\r\nResponse response:          " + response.body().string();
                result.body += "\r\nResponse cache response:    " + response.cacheResponse();
                result.body += "\r\nResponse network response:  " + response.networkResponse();

                emitter.onSuccess(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TimeOutAct.Result>() {
                    @Override
                    public void accept(TimeOutAct.Result result) throws Exception {
                        Log.e(TAG, result.toString());

                        text.setText(result.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        long times = System.currentTimeMillis() - netStartTime;

                        Log.e(TAG, throwable.getMessage() + "--Times:" + times);

                        text.setText(times / 1000F + "--" + throwable.getMessage());
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
