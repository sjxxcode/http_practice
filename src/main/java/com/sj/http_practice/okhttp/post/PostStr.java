package com.sj.http_practice.okhttp.post;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sj.http_practice.R;

import java.util.Iterator;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Post-发送简单字符串(markdown格式)
 *
 * Created by SJ on 2019/2/13.
 */
public class PostStr extends Fragment {

    private static final String TAG = "===" + PostStr.class.getSimpleName();
    private final String URL = "http://pic29.photophoto.cn/20131021/0005018305864117_b.png";

    private TextView text;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_str, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        text = view.findViewById(R.id.text);

        Button btn = view.findViewById(R.id.test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStr();
            }
        });
    }

    private void postStr() {
        Single.create(new SingleOnSubscribe<Result>() {
            @Override
            public void subscribe(SingleEmitter<Result> emitter) throws Exception {
                OkHttpClient client = new OkHttpClient();

                final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

                String postBody = ""
                        + "Releases\n"
                        + "--------\n"
                        + "\n"
                        + " * _1.0_ May 6, 2013\n"
                        + " * _1.1_ June 15, 2013\n"
                        + " * _1.2_ August 11, 2013\n";

                Request request = new Request.Builder()
                        .url("https://api.github.com/markdown/raw")
                        .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
                        .build();

                Response response = client.newCall(request).execute();

                //
                final StringBuilder requestLine = new StringBuilder();
                final StringBuilder statueLine = new StringBuilder();
                final StringBuilder requestStr = new StringBuilder();
                final StringBuilder responseStr = new StringBuilder();

                //-------------------------//
                request = response.request();
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
                responseStr.append("\n");
                responseStr.append(response.body().string());

                Result result = new Result();
                result.str = requestLine.toString() + "\n"
                        + requestStr.toString() + "\n-----------------------------\n"
                        + statueLine.toString() + "\n"
                        + responseStr.toString();
                result.body = response.body().bytes();


                emitter.onSuccess(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result>() {
                    @Override
                    public void accept(Result result) throws Exception {
                        text.setText(result.str);
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
        byte[] body = {'0'};
    }
}
