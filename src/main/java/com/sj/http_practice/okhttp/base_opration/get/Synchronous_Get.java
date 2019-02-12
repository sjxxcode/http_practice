package com.sj.http_practice.okhttp.base_opration.get;

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

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SJ on 2019/2/12.
 */
public class Synchronous_Get extends Fragment {

    private static final String TAG = "===" + Synchronous_Get.class.getSimpleName();
    private final String URL = "https://publicobject.com/helloworld.txt";
    private final String URL2 = "https://direct.wap.zol.com.cn/ipj/baoban/index.php?v=7.0&action=downloadZip&deviceType=2&vs=and704";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_synchronous_get, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView text = view.findViewById(R.id.text);

        Button btn = view.findViewById(R.id.test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synGet(new ICall() {
                    @Override
                    public void end(final String str) {
                        Synchronous_Get.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text.setText(str);
                            }
                        });
                    }
                });
            }
        });
    }

    private void synGet(final ICall call){
        new Thread(){
            @Override
            public void run() {
                StringBuilder requestLine = new StringBuilder();
                StringBuilder statueLine = new StringBuilder();
                StringBuilder requestStr = new StringBuilder();
                StringBuilder responseStr = new StringBuilder();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(URL)
                        //.addHeader("", "application/json")
                        .build();

                Log.e(TAG, request.toString());

                //-------------------------//
                requestLine.append(request.method() + " " + request.url() + " ");

                Headers requestHeader = request.headers();
                Iterator<String> names = requestHeader.names().iterator();
                while(names.hasNext()){
                    String headerName = names.next();
                    String value = requestHeader.get(headerName);

                    requestStr.append(headerName + ": " + value + "\n");
                }

                requestStr.append("\r\n");
                //-------------------------//

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    statueLine.append(response.protocol() + " " + response.code() + " " + response.message());

                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        responseStr.append(responseHeaders.name(i) + ": " + responseHeaders.value(i) + "\n");
                    }

                    responseStr.append("\r\n");
                    responseStr.append(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                call.end(requestLine.toString() + "\n"
                        + requestHeader.toString() + "\n-----------------------------\n"
                        + statueLine.toString() + "\n"
                        + responseStr.toString());
            }
        }.start();
    }

    interface ICall{
        void end(String str);
    }
}
