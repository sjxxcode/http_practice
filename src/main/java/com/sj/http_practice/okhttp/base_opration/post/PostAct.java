package com.sj.http_practice.okhttp.base_opration.post;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.sj.http_practice.R;
import com.sj.http_practice.okhttp.base_opration.get.Asynchronous_Get;
import com.sj.http_practice.okhttp.base_opration.get.Synchronous_Get;

/**
 * Created by SJ on 2019/2/12.
 */
public class PostAct extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_post);

        this.findViewById(R.id.post_str).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetF = Fragment.instantiate(PostAct.this, PostStr.class.getName());
                addFragment(targetF);
            }
        });

        this.findViewById(R.id.post_json).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetF = Fragment.instantiate(PostAct.this, PostJson.class.getName());
                addFragment(targetF);
            }
        });

        this.findViewById(R.id.post_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetF = Fragment.instantiate(PostAct.this, PostFile.class.getName());
                addFragment(targetF);
            }
        });

        this.findViewById(R.id.post_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetF = Fragment.instantiate(PostAct.this, PostFormData.class.getName());
                addFragment(targetF);
            }
        });

        this.findViewById(R.id.post_mutipart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetF = Fragment.instantiate(PostAct.this, PostMutipartData.class.getName());
                addFragment(targetF);
            }
        });

    }

    private void addFragment(Fragment f) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_1, f);
        ft.commit();
    }
}
