package com.example.manojd.myapplication.exportImport;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.example.manojd.myapplication.db.DbHelper;
import com.example.manojd.myapplication.model.Contact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ContactExport {

    public static final String TAG = "ContactExport";

    public static void exportContacts(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper .getReadableDatabase();

        String exportFileName = "contacts_export.txt";
        File exportFile = new File(context.getExternalFilesDir(null), exportFileName);

        try {
            FileOutputStream fos = new FileOutputStream(exportFile);
            Cursor cursor = db.rawQuery("select * from contacts", null);

            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String phone = cursor.getString(3);
                String email = cursor.getString(2);
                String fenzu = cursor.getString(4);
                String line = name + "," + phone +","+email+","+fenzu+ "\n";
                fos.write(line.getBytes());
            }

            fos.close();
            cursor.close();

            Toast.makeText(context, "Contacts exported to " + exportFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e(TAG, "Error exporting contacts: " + e.getMessage());
            Toast.makeText(context, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }
}