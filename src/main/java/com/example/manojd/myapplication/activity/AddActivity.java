package com.example.manojd.myapplication.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.manojd.myapplication.R;
import com.example.manojd.myapplication.common.Utility;
import com.example.manojd.myapplication.db.DbHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//添加联系人页面
public class AddActivity extends AppCompatActivity {
    EditText name,phone,email;
    String Fenzu;

    Spinner spinner;
    String[] relationshipTypes = {"未分组","家庭", "朋友", "工作"};
    ImageView imageView;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);




        name = findViewById(R.id.editName);
        phone = findViewById(R.id.editPhone);
        email = findViewById(R.id.editEmail);
        imageView = findViewById(R.id.imageButtonAdd);

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
                Fenzu=selectedRelationship;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 如果没有选中任何选项时的操作
            }
        });





        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    //获取权限
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1){
            if((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[2] == PackageManager.PERMISSION_GRANTED)){
                imageView.setEnabled(true);
                if(userChoosenTask.equals("Take Photo")){
                    cameraIntent();
                }
                else if(userChoosenTask.equals("Choose from Library")){
                    galleryIntent();
                }
            }
            else{
                imageView.setEnabled(false);
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


    //选择图片
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

        imageView.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imageView.setImageBitmap(bm);
    }

    private void selectImage() {
        final CharSequence[] items = { "照相", "从相册中选择",
                "取消" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle("选择头像!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(AddActivity.this);

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

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    //--------------------------------------------------------------------------------------------------------------------------------


    public long save(View view){
        DbHelper helper = new DbHelper(this,DbHelper.DB_NAME,null,DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getWritableDatabase();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bm=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        bm.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] arr1 = stream.toByteArray();
        String result = Base64.encodeToString(arr1,Base64.DEFAULT);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_2,name.getText().toString());
        values.put(DbHelper.COLUMN_3,email.getText().toString());
        values.put(DbHelper.COLUMN_4,phone.getText().toString());
        values.put(DbHelper.COLUMN_5,Fenzu);
        values.put(DbHelper.COLUMN_6,result);

        long insertId = db.insert(DbHelper.TABLE_NAME,null,values);
        Toast.makeText(this,"Inserted 1 item..",Toast.LENGTH_SHORT).show();
        Log.d("AddActivity","Inserted.."+insertId);
        db.close();
        finish();
        return insertId;
    }

    public void cancel(View view){
        finish();
    }
}
