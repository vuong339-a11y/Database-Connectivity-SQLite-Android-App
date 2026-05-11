package com.example.databaseconnectivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import androidx.annotation.Nullable;

public class MyDB extends SQLiteOpenHelper {
    private Context context;

    public MyDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "PhongTro.db", factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table phong(so_phong TEXT primary key, gia INTEGER, dien_moi INTEGER, dien_cu INTEGER, gia_dien INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS phong");
        onCreate(db);
    }

    // Hàm Lưu/Sửa
    public void Save_Data(String so, int gia, int dm, int dc, int gd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("so_phong", so);
        values.put("gia", gia);
        values.put("dien_moi", dm);
        values.put("dien_cu", dc);
        values.put("gia_dien", gd);
        db.replace("phong", null, values);
        db.close();
    }

    // Hàm Xóa (Giải quyết lỗi cannot find symbol Delete_Data)
    public int Delete_Data(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("phong", "so_phong=?", new String[]{id});
        db.close();
        return result;
    }

    // Hàm Hiển thị (Giải quyết lỗi cannot find symbol Display_Data)
    public String Display_Data() {
        String result = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from phong", null);
        while (cursor.moveToNext()) {
            String so = cursor.getString(0);
            int gia = cursor.getInt(1);
            int dm = cursor.getInt(2);
            int dc = cursor.getInt(3);
            int gd = cursor.getInt(4);
            int tongDien = (dm - dc) * gd;
            result += "P." + so + ": " + (gia + tongDien) + "k (Điện: " + (dm-dc) + ")\n";
        }
        cursor.close();
        db.close();
        return result;
    }

    // Hàm Xuất file .db ra thư mục Download
    public String exportDatabase() {
        try {
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File data = Environment.getDataDirectory();
            String currentDBPath = "//data//" + context.getPackageName() + "//databases//PhongTro.db";
            String backupDBName = "bak_PhongTro_" + System.currentTimeMillis() + ".db";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBName);
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close(); dst.close();
            return "Đã lưu: " + backupDBName;
        } catch (Exception e) { return "Lỗi: " + e.getMessage(); }
    }
}
