package com.sj.http_practice.okhttp.header;

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

import java.util.Iterator;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 设置请求报文中的Header
 * -addHeader():这种方式设置的Header是以最后一次设置的为准,新值会覆盖旧值。
 * -header():这种方式会添加多个name相同,value不同的请求头。但是测试发现，并不是这样，效果与header()一样，也是新值会覆盖旧值。
 *
 * Created by SJ on 2019/2/13.
 */
public class SetHeader extends Fragment {

    private static final String TAG = "===" + SetHeader.class.getSimpleName();
    private final String URL = "http://pic29.photophoto.cn/20131021/0005018305864117_b.png";

    private TextView text;
    private ImageView img;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_header, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        text = view.findViewById(R.id.text);
        img = view.findViewById(R.id.img);

        Button btn = view.findViewById(R.id.test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asynGet();
            }
        });
    }

    private void asynGet() {
        Single.create(new SingleOnSubscribe<Result>() {
            @Override
            public void subscribe(SingleEmitter<Result> emitter) throws Exception {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(URL)
                        .header("Range", "bytes=0-30000")
                        .header("Range", "bytes=0-10000")
                        .addHeader("User-Agent", "Custom UserAgent")
                        .addHeader("Accept", "application/json; q=0.5")
                        .addHeader("Accept", "image/webp,image/apng,*/*;q=0.8")
                        .build();
                Response response = client.newCall(request).execute();

                //
                final StringBuilder requestLine = new StringBuilder();
                final StringBuilder statueLine = new StringBuilder();
                final StringBuilder requestStr = new StringBuilder();
                final StringBuilder responseStr = new StringBuilder();

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
                        img.setImageBitmap(BitmapFactory.decodeByteArray(result.body, 0, result.body.length));
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
