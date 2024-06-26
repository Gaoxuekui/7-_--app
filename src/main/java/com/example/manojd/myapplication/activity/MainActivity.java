package com.example.manojd.myapplication.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manojd.myapplication.R;
import com.example.manojd.myapplication.adapter.ContactAdapterRecyclerView;
import com.example.manojd.myapplication.common.ContactClickListner;
import com.example.manojd.myapplication.db.DbHelper;
import com.example.manojd.myapplication.exportImport.ContactExport;
import com.example.manojd.myapplication.exportImport.ContactImport;
import com.example.manojd.myapplication.model.Contact;

import java.io.File;
import java.util.ArrayList;

//系统主页面
public class MainActivity extends AppCompatActivity implements ContactClickListner {

    RecyclerView recyclerView;
    ArrayList<Contact> contacts = new ArrayList<>();

    ContactAdapterRecyclerView adapter;

    private EditText search=null;

    // 开始
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recyclerview);


        // 添加按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.saveFlaotingButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });



        // 获取TextView的引用
        TextView fenzu = findViewById(R.id.fenzu);

        // 设置点击事件
        fenzu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动家庭活动
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                intent.putExtra("category", "分组");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    // Android Activity 生命周期
    @Override
    protected void onResume() {
        super.onResume();

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new ContactAdapterRecyclerView(this,contacts,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        search = (EditText)findViewById(R.id.search);
        search.addTextChangedListener(new SearchTextChangedListener());
        getData();

    }

    // 菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // 菜单栏的添加和清空联系人列表功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this,AddActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_delete_all){
            DbHelper helper = new DbHelper(this,DbHelper.DB_NAME,null,DbHelper.DB_VERSION);
            SQLiteDatabase db = helper.getReadableDatabase();
            //删除全部，然后重新查询
            String sql = "delete from contacts";
            db.execSQL(sql);
            getData();
        }else if(id == R.id.action_eixt){
            finish();
        }else if(id == R.id.action_export){
            ContactExport.exportContacts(MainActivity.this);

        }else if(id == R.id.action_import){
            File importFile = new File(getExternalFilesDir(null), "contacts_export.txt");
            ContactImport.importContacts(MainActivity.this, importFile);
            getData();
        }



        return super.onOptionsItemSelected(item);
    }

    // 从数据库获取联系人信息
    public void getData(){
        contacts.clear();
        DbHelper helper = new DbHelper(this,DbHelper.DB_NAME,null,DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();

        String query = "SELECT * FROM "+DbHelper.TABLE_NAME+" ORDER BY " + DbHelper.COLUMN_2+ " COLLATE NOCASE";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                Contact contact = new Contact();
                contact.setId( Integer.parseInt(cursor.getString(0)) );
                contact.setName( cursor.getString(1) );
                contact.setEmail( cursor.getString(2) );
                contact.setMobile( cursor.getString(3) );
                contact.setFenzu( cursor.getString(4) );
                contact.setImage( cursor.getString(5) );

                contacts.add(contact);

            }while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
    }

    // 跳转详情页面
    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("contact",contact.getId());
        startActivity(intent);
        Toast.makeText(MainActivity.this,"item clicked",Toast.LENGTH_SHORT).show();
    }

    // EditText监听变化
    // 搜索栏
    class SearchTextChangedListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getContent(search.getText().toString());
        }

    }

    // 搜索逻辑，从数据库中获取
    private void getContent(String queryName){
        contacts.clear();
        DbHelper helper = new DbHelper(this,DbHelper.DB_NAME,null,DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from contacts where name like ? or email like ? or phone like ? or fenzu like ?";
        String[] params = new String[]{"%"+queryName+"%", "%"+queryName+"%","%"+queryName+"%","%"+queryName+"%"};
        Cursor cursor = db.rawQuery(sql, params);
        if(cursor.moveToFirst()){
            do{
                Contact contact = new Contact();
                contact.setId( Integer.parseInt(cursor.getString(0)) );
                contact.setName( cursor.getString(1) );
                contact.setEmail( cursor.getString(2) );
                contact.setMobile( cursor.getString(3) );
                contact.setFenzu( cursor.getString(4) );
                contact.setImage( cursor.getString(5) );

                contacts.add(contact);

            }while (cursor.moveToNext());
        }
        Log.e("Contacts","size:"+contacts.size());
        adapter.notifyDataSetChanged();
    }


}


