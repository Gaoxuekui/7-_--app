package com.example.manojd.myapplication.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.manojd.myapplication.R;
import com.example.manojd.myapplication.adapter.ContactAdapterRecyclerView;
import com.example.manojd.myapplication.common.ContactClickListner;
import com.example.manojd.myapplication.db.DbHelper;
import com.example.manojd.myapplication.exportImport.ContactExport;
import com.example.manojd.myapplication.model.Contact;

import java.util.ArrayList;

public class SelectDetailsActivity extends AppCompatActivity  implements ContactClickListner {

    RecyclerView recyclerView;
    ArrayList<Contact> contacts = new ArrayList<>();

    ContactAdapterRecyclerView adapter;
    String category;


    private EditText search=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdetails);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            category = intent.getStringExtra("category");
            // 确保 category 不为 null，然后可以在这里使用它
            if (category != null) {
                actionBar.setTitle(category);;

            }
        }

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
        getData(category);

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
            getData(category);
        }else if(id == R.id.action_eixt){
            finish();
        }
        else if(id == R.id.action_export){
            ContactExport.exportContacts(this);

        }


        return super.onOptionsItemSelected(item);
    }

    // 从数据库获取联系人信息
    public void getData(String queryName){
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
        adapter.notifyDataSetChanged();
    }

    // 跳转详情页面
    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("contact",contact.getId());
        startActivity(intent);
        Toast.makeText(this,"item clicked",Toast.LENGTH_SHORT).show();
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
