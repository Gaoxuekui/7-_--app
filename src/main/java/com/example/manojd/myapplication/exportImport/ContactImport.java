package com.example.manojd.myapplication.exportImport;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.manojd.myapplication.db.DbHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ContactImport {

    public static final String TAG = "ContactImporter";

    public static void importContacts(Context context, File importFile) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(importFile));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String name = parts[0];
                    String phone = parts[1];
                    String email = parts[2];
                    String fenzu = parts[3];

                    // 插入数据到数据库
                    db.execSQL("INSERT INTO " + DbHelper.TABLE_NAME +
                            "(" + DbHelper.COLUMN_2 + ", " + DbHelper.COLUMN_4 +", " + DbHelper.COLUMN_3 +", " + DbHelper.COLUMN_5 + ")" +
                            " VALUES ('" + name + "', '" + phone +"', '" + email+"', '" + fenzu + "')");
                }
            }

            reader.close();

            Toast.makeText(context, "Contacts imported successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error importing contacts: " + e.getMessage());
            Toast.makeText(context, "Import failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }
}
