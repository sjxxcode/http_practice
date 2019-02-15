package com.sj.http_practice.okhttp.post;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sj.http_practice.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
 * Post-发送文件
 *
 * 关于https访问自签名网站,请参考
 * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0831/3393.html
 *
 * Created by SJ on 2019/2/13.
 */
public class PostFile extends Fragment {

    private static final String TAG = "===" + PostFile.class.getSimpleName();
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
                //FIXME
                //为了解决采用Https访问"自签名"网站
                //因为本地用Fiddler抓包,所以也得这样做
                //手机是经过Fiddler代理把网络请求向外分发的,所以当前请求是先要经过Fillder,如果当前访问的url是https的话,不把Fillder的证书添加上的话就会出错
                //InputStream caIn = getActivity().getAssets().open("LocalFiddler.cer");
                //
                //OkHttpClient client = new OkHttpClient.Builder()
                //        .sslSocketFactory(GetCA.getCertificates(caIn)).build();

                //访问的是经过CA认证的网站的话,则不需要把该网站的证书加上,否则通过sslSocketFactory()把要访问的网站的CA证书设置进去
                OkHttpClient client = new OkHttpClient.Builder().build();

                final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/plain;");

                Request request = new Request.Builder()
                        .url("https://api.github.com/markdown/raw")
                        .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, getBytes()))
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
                        Log.e(TAG, throwable.getMessage());
                        text.setText(throwable.getMessage());
                    }
                });
    }

    private byte[] getBytes(){
        byte[] content = null;

        try {
            InputStream is = this.getActivity().getAssets().open("test_normal_text");
            BufferedInputStream reader = new BufferedInputStream(is);

            content = new byte[128];
            reader.read(content);

            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    private static class Result{
        String str = "";
        byte[] body = {'0'};
    }
}
