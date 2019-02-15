package com.sj.http_practice.okhttp.authentication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sj.http_practice.R;
import com.sj.http_practice.Util.NetUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * 超时
 *
 * Created by SJ on 2019/2/14.
 */
public class AuthenticationAct extends AppCompatActivity{

    private final String TAG = "===" + this.getClass().getSimpleName();

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
        Single.create(new SingleOnSubscribe<AuthenticationAct.Result>() {
            @Override
            public void subscribe(SingleEmitter<AuthenticationAct.Result> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url("http://publicobject.com/secrets/hellosecret.txt")
                        .build();

                OkHttpClient okHttpClient = NetUtil.getAddFiddlerCERHttlClient(AuthenticationAct.this)
                        .callTimeout(20, TimeUnit.SECONDS)
                        .authenticator(new Authenticator() {
                            @Nullable
                            @Override
                            public Request authenticate(@Nullable Route route, Response response) throws IOException {
                                Log.e(TAG, route.toString());

                                if (response.request().header("Authorization") != null) {
                                    return null; // Give up, we've already attempted to authenticate.
                                }

                                String credential = Credentials.basic("jesse", "password1");

                                Log.e(TAG, "Authenticating for response: " + response);
                                Log.e(TAG, "Challenges: " + response.challenges());
                                Log.e(TAG, "credential: " + credential);

                                return response.request().newBuilder()
                                        .header("Authorization", credential)
                                        .build();
                            }
                        })
                        .build();

                Response response = okHttpClient.newCall(request).execute();

                Result result = new Result();
                result.body = "Response response:          " + response.body().string();

                emitter.onSuccess(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AuthenticationAct.Result>() {
                    @Override
                    public void accept(AuthenticationAct.Result result) throws Exception {
                        Log.e(TAG, result.toString());

                        text.setText(result.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());

                        text.setText(throwable.getMessage());
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
