package com.sj.http_practice.okhttp.get;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.sj.http_practice.R;

/**
 * Created by SJ on 2019/2/12.
 */
public class GetAct extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_get);

        this.findViewById(R.id.syn_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetF = Fragment.instantiate(GetAct.this, Synchronous_Get.class.getName());
                addFragment(targetF);
            }
        });

        this.findViewById(R.id.asyn_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetF = Fragment.instantiate(GetAct.this, Asynchronous_Get.class.getName());
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
