package com.example.manojd.myapplication.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manojd.myapplication.R;
import com.example.manojd.myapplication.db.DbHelper;
import com.example.manojd.myapplication.model.Contact;

import java.util.ArrayList;
//联系人详情
public class DetailsActivity extends AppCompatActivity {
    TextView fName,email,mobile,fenzu;
    ImageView imageView;
    ImageButton call,message;

    // 初始化，从数据库中读取联系人
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        fName = findViewById(R.id.detailsFirstName);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.detailsMobileNumber);
        fenzu = findViewById(R.id.fenzu);
        imageView = findViewById(R.id.imageViewDetails);
        call =(ImageButton) findViewById(R.id.buttonCall);
        message =(ImageButton) findViewById(R.id.buttonMessage);

        Contact contact = getData();
        fName.setText(contact.getName());
        email.setText(contact.getEmail());
        mobile.setText(contact.getMobile());
        fenzu.setText(contact.getFenzu());
        if(contact.getImage() == null){
            imageView.setImageResource(R.drawable.contacts_icon);
        }
        else {
            byte [] arr = Base64.decode(contact.getImage(),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr,0,arr.length);
            imageView.setImageBitmap(bitmap);
        }

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE },
                    1);
        }


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(contact.getName());

        // 编辑按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.editFlaotingButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
                finish();
            }
        });


    }


    // 从数据库获取数据，显示出来
    public Contact getData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("contact", 0);
        DbHelper helper = new DbHelper(this, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Contact> contacts = new ArrayList<>();

        String query = "SELECT * FROM " + DbHelper.TABLE_NAME + " WHERE ID=" + id;
        Cursor cursor = db.rawQuery(query, null);
        Contact contact = null;
        if (cursor.moveToFirst()) {
            do {
                contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setEmail(cursor.getString(2));
                contact.setMobile(cursor.getString(3));
                contact.setFenzu(cursor.getString(4));
                contact.setImage(cursor.getString(5));

                contacts.add(contact);

            } while (cursor.moveToNext());
        }

        return contact;
    }


    // 更新数据
    public void updateData(){
        Intent intent = getIntent();
        int id = intent.getIntExtra("contact",0);
        Intent intent1 = new Intent(this, UpdateActivity.class);
        intent1.putExtra("updateContact",id);
        startActivity(intent1);
    }

    // menu中删除的按钮跳出弹框
    public void deleteData(){
        dialog();
    }

    // 删除功能
    private void dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        builder.setTitle("确定删除吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = getIntent();
                int id = intent.getIntExtra("contact",0);
                DbHelper helper = new DbHelper(DetailsActivity.this,DbHelper.DB_NAME,null,DbHelper.DB_VERSION);
                SQLiteDatabase db = helper.getWritableDatabase();
                int result = db.delete(DbHelper.TABLE_NAME,"ID="+id,null);
                Log.v("TAG","Item deleted "+result+" "+id);
                Toast.makeText(DetailsActivity.this,"Item deleted "+id,Toast.LENGTH_SHORT).show();
                db.close();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    // 详情页的menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            deleteData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // call请求权限处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                call.setEnabled(true);
                Log.d("DetailsActivity", "Permission granted");
            } else {
                Log.d("DetailsActivity", "Permission denied");
            }
        }
    }

    // call跳转
    public void call(View view){
        Contact contact = getData();
        Intent intent1 = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:+86 "+contact.getMobile()));
        startActivity(intent1);
        Log.v("Number",contact.getMobile());
    }

    // message跳转
    public void message(View view){
        Contact contact = getData();
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("sms:"+contact.getMobile()));
        startActivity(intent);
    }

}
