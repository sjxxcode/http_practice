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
import com.sj.http_practice.Util.GetCA;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Post-发送"多部分形式的数据-(单文件/文本/文本+文件)"
 *
 * Created by SJ on 2019/2/13.
 */
public class PostMutipartData extends Fragment {

    private static final String TAG = "===" + PostMutipartData.class.getSimpleName();

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
                //OkHttpClient client = new OkHttpClient();
                InputStream caIn = getActivity().getAssets().open("LocalFiddler.cer");
                //
                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(GetCA.getCertificates(caIn)).build();

                RequestBody formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("who_are_you", "zhangsan")
                        .addFormDataPart("tou_xiang", "avtart.jpeg",
                                RequestBody.create(MediaType.parse("image/jpeg"), getBytes()))
                        .build();

                Request request = new Request.Builder()
                        .url("https://en.wikipedia.org/w/index.php")
                        .post(formBody)
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

    private byte[] getBytes(){
        byte[] content = null;

        try {
            InputStream is = this.getActivity().getAssets().open("test_mutipart_img.jpeg");
            BufferedInputStream reader = new BufferedInputStream(is);

            int size = 512;
            int length = 0;
            content = new byte[size];
            while((size = reader.read(content)) != -1){
                length += size;
                content = Arrays.copyOf(content, length);
            }

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
