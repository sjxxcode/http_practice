package com.sj.http_practice.okhttp.base_opration.head;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sj.http_practice.R;

import java.io.IOException;
import java.util.Iterator;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SJ on 2019/2/13.
 */
public class Head extends Activity {

    private static final String TAG = "===" + Head.class.getSimpleName();
    private final String URL = "http://pic29.photophoto.cn/20131021/0005018305864117_b.png";

    private TextView text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.fragment_head);

        text = this.findViewById(R.id.text);
        Button btn = this.findViewById(R.id.test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asynGet();
            }
        });
    }

    private void asynGet() {
        Single.create(new SingleOnSubscribe<Response>() {
            @Override
            public void subscribe(SingleEmitter<Response> emitter) throws Exception {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(URL)
                        .method("HEAD", null)
                        .build();

                emitter.onSuccess(client.newCall(request).execute());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response response) throws Exception {
                        final StringBuilder requestLine = new StringBuilder();
                        final StringBuilder statueLine = new StringBuilder();
                        final StringBuilder requestStr = new StringBuilder();
                        final StringBuilder responseStr = new StringBuilder();

                        //
                        Request request = response.request();

                        //-------------------------//
                        requestLine.append(request.method() + " " + request.url() + " ");

                        Headers requestHeader = request.headers();
                        Iterator<String> names = requestHeader.names().iterator();
                        while (names.hasNext()) {
                            String headerName = names.next();
                            String value = requestHeader.get(headerName);

                            requestStr.append(headerName + ": " + value + "\n");
                        }

                        requestStr.append("\r\n");
                        //-------------------------//

                        statueLine.append(response.protocol() + " " + response.code() + " " + response.message());

                        Headers responseHeaders = response.headers();
                        for (int i = 0; i < responseHeaders.size(); i++) {
                            responseStr.append(responseHeaders.name(i) + ": " + responseHeaders.value(i) + "\n");
                        }

                        responseStr.append("\r\n");
                        responseStr.append(response.body().string());

                        text.setText(requestLine.toString() + "\n"
                                + requestStr.toString() + "\n-----------------------------\n"
                                + statueLine.toString() + "\n"
                                + responseStr.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        text.setText(throwable.getMessage());
                    }
                });
    }
}
