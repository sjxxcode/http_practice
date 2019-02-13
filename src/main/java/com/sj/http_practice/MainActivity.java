package com.sj.http_practice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sj.http_practice.okhttp.base_opration.get.GetAct;
import com.sj.http_practice.okhttp.base_opration.head.Head;
import com.sj.http_practice.okhttp.base_opration.header.HeaderAct;
import com.sj.http_practice.okhttp.base_opration.post.PostAct;

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
    }
}
