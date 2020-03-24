package com.xukui.demo.logui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xukui.library.logui.LogUI;
import com.xukui.library.logui.ui.LogsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_print).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUI.d("ddd", System.currentTimeMillis() + "");
            }

        });

        findViewById(R.id.btn_logs).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = LogsActivity.buildIntent(MainActivity.this);
                startActivity(intent);
            }

        });
    }

}