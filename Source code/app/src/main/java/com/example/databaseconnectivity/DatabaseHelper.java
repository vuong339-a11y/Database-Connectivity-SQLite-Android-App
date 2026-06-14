package com.example.databaseconnectivity;// Đổi lại đúng package của bạn

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import java.io.File;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;
    
    // Tên bảng và các cột
    public static final String TABLE_NAME = "PhongTro";
    public static final String COL_ID = "id";
    public static final String COL_SOPHONG = "sophong";
    public static final String COL_GIA = "gia";
    public static final String COL_DIEN = "dien";
    public static final String COL_TONG = "tong";

    // Đường dẫn lưu file data.db ở thư mục Download
    private static String getDatabasePath() {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return new File(downloadDir, DATABASE_NAME).getAbsolutePath();
    }

    public DatabaseHelper(Context context) {
        // Truyền đường dẫn đầy đủ ở Download vào thay vì chỉ truyền tên file
        super(context, getDatabasePath(), null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SOPHONG + " TEXT, " +
                COL_GIA + " REAL, " +
                COL_DIEN + " REAL, " +
                COL_TONG + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Hàm thêm dữ liệu
    public boolean insertData(String soPhong, double gia, double dien, double tong) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.setData(COL_SOPHONG, soPhong);
        contentValues.put(COL_GIA, gia);
        contentValues.put(COL_DIEN, dien);
        contentValues.put(COL_TONG, tong);
        
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // Trả về true nếu lưu thành công
    }

    // Hàm đọc dữ liệu
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
