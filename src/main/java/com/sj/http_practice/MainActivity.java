package com.sj.http_practice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sj.http_practice.okhttp.authentication.AuthenticationAct;
import com.sj.http_practice.okhttp.cache.CacheAct;
import com.sj.http_practice.okhttp.cancel.CancelAct;
import com.sj.http_practice.okhttp.get.GetAct;
import com.sj.http_practice.okhttp.head.Head;
import com.sj.http_practice.okhttp.header.HeaderAct;
import com.sj.http_practice.okhttp.post.PostAct;
import com.sj.http_practice.okhttp.timeout.TimeOutAct;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViewById(R.id.get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GetAct.class));
            }
        });

        this.findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostAct.class));
            }
        });

        this.findViewById(R.id.head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Head.class));
            }
        });

        this.findViewById(R.id.header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HeaderAct.class));
            }
        });

        this.findViewById(R.id.cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CacheAct.class));
            }
        });

        this.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CancelAct.class));
            }
        });

        this.findViewById(R.id.timeout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TimeOutAct.class));
            }
        });

        this.findViewById(R.id.authentication).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AuthenticationAct.class));
            }
        });
    }
}
