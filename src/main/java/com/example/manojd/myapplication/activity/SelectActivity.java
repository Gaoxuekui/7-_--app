package com.example.manojd.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.manojd.myapplication.R;
import com.example.manojd.myapplication.common.ContactClickListner;
import com.example.manojd.myapplication.model.Contact;

public class SelectActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        // 获取TextView的引用
        TextView nothing = findViewById(R.id.Nothing);
        TextView family = findViewById(R.id.Family);
        TextView friends = findViewById(R.id.Friends);
        TextView work = findViewById(R.id.Work);

        // 设置点击事件
        nothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动未分组活动
                Intent intent = new Intent(SelectActivity.this, SelectDetailsActivity.class);
                intent.putExtra("category", "未分组");
                startActivity(intent);
            }
        });
        family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动家庭活动
                Intent intent = new Intent(SelectActivity.this, SelectDetailsActivity.class);
                intent.putExtra("category", "家庭");
                startActivity(intent);
            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动朋友活动
                Intent intent = new Intent(SelectActivity.this, SelectDetailsActivity.class);
                intent.putExtra("category", "朋友");
                startActivity(intent);
            }
        });

        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动工作活动
                Intent intent = new Intent(SelectActivity.this, SelectDetailsActivity.class);
                intent.putExtra("category", "工作");
                startActivity(intent);
            }
        });
    }
}
