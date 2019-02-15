package com.sj.http_practice.okhttp.header;

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
public class HeaderAct extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_header);

        this.findViewById(R.id.set_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetF = Fragment.instantiate(HeaderAct.this, SetHeader.class.getName());
                addFragment(targetF);
            }
        });

        this.findViewById(R.id.range).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetF = Fragment.instantiate(HeaderAct.this, Range.class.getName());
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
