package com.example.manojd.myapplication.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.manojd.myapplication.R;
import com.example.manojd.myapplication.common.Utility;
import com.example.manojd.myapplication.db.DbHelper;
import com.example.manojd.myapplication.model.Contact;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//更新联系人
public class UpdateActivity extends AppCompatActivity {
    EditText name,email,phone;

    String fenzu;
    Spinner spinner;
    String[] relationshipTypes = {"未分组","家庭", "朋友", "工作"};
    ImageView imageButton;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    // 初始化修改页面
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        name = findViewById(R.id.editNameUpdate);
        email = findViewById(R.id.editEmailUpdate);
        phone = findViewById(R.id.editPhoneUpdate);
        imageButton =(ImageView) findViewById(R.id.imageButtonUpdate);

        spinner = findViewById(R.id.spinner_relationship);


        // 创建一个ArrayAdapter并设置样式
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, relationshipTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 将适配器设置给Spinner
        spinner.setAdapter(adapter);

        // 如果需要监听Spinner的选择事件，可以在这里添加监听器
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRelationship = relationshipTypes[position];
                // 在这里处理选中选项的操作
                // 将选中的值设置给EditText
                fenzu=selectedRelationship;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 如果没有选中任何选项时的操作
            }
        });

        getData();

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.saveFlaotingButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                Toast.makeText(UpdateActivity.this,"Updated successfully...!!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // 处理图片
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1){
            if((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[2] == PackageManager.PERMISSION_GRANTED)){
                imageButton.setEnabled(true);
                if(userChoosenTask.equals("Take Photo")){
                    cameraIntent();
                }
                else if(userChoosenTask.equals("Choose from Library")){
                    galleryIntent();
                }
            }
            else{
                imageButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            destination.getAbsolutePath();
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageButton.setImageBitmap(thumbnail);
    }


    // 图片按钮
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imageButton.setImageBitmap(bm);
    }

    private void selectImage() {
        final CharSequence[] items = { "照相", "从相册中选择",
                "取消" };

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        builder.setTitle("添加头像!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(UpdateActivity.this);

                if (items[item].equals("照相")) {
                    userChoosenTask ="照相";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("从相册中选择")) {
                    userChoosenTask ="从相册中选择";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("取消")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //跳转到系统自带的相册选择
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    //跳转到系统照相机
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void getData(){
        Intent intent = getIntent();
        int id = intent.getIntExtra("updateContact",0);
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

        name.setText(contact.getName());
        email.setText(contact.getEmail());
        phone.setText(contact.getMobile());
        fenzu=contact.getFenzu();

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(fenzu)) {
                spinner.setSelection(i);
                break;
            }
        }

        if (contact.getImage() == null) {
            imageButton.setImageResource(R.drawable.contacts_icon);
        } else {
            byte[] arr = Base64.decode(contact.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            imageButton.setImageBitmap(bitmap);
        }
    }

    // 向数据库中添加
    public void addData(){
        Intent intent = getIntent();
        int id = intent.getIntExtra("updateContact",0);
        DbHelper helper = new DbHelper(this,DbHelper.DB_NAME,null,DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getWritableDatabase();
        Log.v("TAG","Updated row no "+id);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bm=((BitmapDrawable)imageButton.getDrawable()).getBitmap();
        bm.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] arr1 = stream.toByteArray();
        String result = Base64.encodeToString(arr1,Base64.DEFAULT);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_2,name.getText().toString());
        values.put(DbHelper.COLUMN_3,email.getText().toString());
        values.put(DbHelper.COLUMN_4,phone.getText().toString());
        values.put(DbHelper.COLUMN_5,fenzu);
        values.put(DbHelper.COLUMN_6,result);

        db.update(DbHelper.TABLE_NAME,values,"ID="+id,null);
        db.close();
    }
}
