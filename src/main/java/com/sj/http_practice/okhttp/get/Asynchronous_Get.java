package com.sj.http_practice.okhttp.get;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * okhttp-异步get
 *
 * Created by SJ on 2019/2/13.
 */
public class Asynchronous_Get extends Fragment {

    private static final String TAG = "===" + Asynchronous_Get.class.getSimpleName();
    private final String URL = "http://pic29.photophoto.cn/20131021/0005018305864117_b.png";

    private TextView text;
    private ImageView img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asynchronous_get, container, false);
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
        final StringBuilder requestLine = new StringBuilder();
        final StringBuilder statueLine = new StringBuilder();
        final StringBuilder requestStr = new StringBuilder();
        final StringBuilder responseStr = new StringBuilder();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                //.addHeader("Range", "bytes=0-30000")
                .build();

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

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                statueLine.append(response.protocol() + " " + response.code() + " " + response.message());

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    responseStr.append(responseHeaders.name(i) + ": " + responseHeaders.value(i) + "\n");
                }

                responseStr.append("\r\n");

                final byte[] imgBytes = response.body().bytes();

                Asynchronous_Get.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(requestLine.toString() + "\n"
                                + requestStr.toString() + "\n-----------------------------\n"
                                + statueLine.toString() + "\n"
                                + responseStr.toString());

                        img.setImageBitmap(BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length));
                    }
                });
            }
        });
    }
}
