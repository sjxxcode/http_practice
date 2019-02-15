package com.sj.http_practice.okhttp.cancel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sj.http_practice.R;
import com.sj.http_practice.Util.GetCA;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 取消请求
 * Call.cancel()
 *
 * Created by SJ on 2019/2/14.
 */
public class CancelAct extends AppCompatActivity{

    private OkHttpClient okHttpClient;
    private Call call;

    private EditText delay;
    private TextView text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_cancel);

        InputStream caIn = null;
        try {
            caIn = getAssets().open("LocalFiddler.cer");

            //
            okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(GetCA.getCertificates(caIn)).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.delay = this.findViewById(R.id.delay);
        this.text = this.findViewById(R.id.text);

        this.findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence cs = delay.getText();
                if(!TextUtils.isEmpty(cs)){
                    int delayTime = 1;
                    try {
                        delayTime = Integer.parseInt(cs.toString());
                    } catch (Exception e){
                        text.setText("设置正确延迟时间");
                        return;
                    }

                    requestBefor(delayTime);
                    return;
                }

                text.setText("设置正确延迟时间");
            }
        });

        this.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void cancel(){
        if(this.call != null && !this.call.isCanceled() && this.call.isExecuted()){
            this.call.cancel();
        }
    }

    /**
     * 在知情request过程中，cancle
     */
    private void requestBefor(final int delay) {
        Single.create(new SingleOnSubscribe<CancelAct.Result>() {
            @Override
            public void subscribe(SingleEmitter<CancelAct.Result> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url("http://httpbin.org/delay/" + delay) //该接口会根据设置的整数事件来做延迟
                        .build();

                call = okHttpClient.newCall(request);
                Response response = call.execute();

                Result result = new Result();
                result.body = "Response response:          " + response.body().string();

                emitter.onSuccess(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CancelAct.Result>() {
                    @Override
                    public void accept(CancelAct.Result result) throws Exception {
                        text.setText(result.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
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
