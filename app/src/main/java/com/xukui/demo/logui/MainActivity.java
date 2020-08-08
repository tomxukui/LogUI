package com.xukui.demo.logui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
                LogUI.json("ddd", "{\"app\":\"demo\"}");
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