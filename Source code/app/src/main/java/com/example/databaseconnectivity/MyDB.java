package com.example.databaseconnectivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class MyDB extends SQLiteOpenHelper {
    public MyDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "PhongTro.db", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng với các cột bạn cần
        db.execSQL("create table phong(so_phong TEXT primary key, gia INTEGER, dien_moi INTEGER, dien_cu INTEGER, gia_dien INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS phong");
        onCreate(db);
    }

    // Thêm hoặc Sửa phòng (Dùng replace để nếu trùng số phòng nó sẽ tự cập nhật mới)
    void Save_Data(String so, int gia, int dm, int dc, int gd) {
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

    // Xóa phòng theo số phòng
    int Delete_Data(String so) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("phong", "so_phong=?", new String[]{so});
    }

    // Lấy dữ liệu hiển thị (kèm tính toán)
    String Display_Data() {
        String result = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from phong", null);
        while (cursor.moveToNext()) {
            String so = cursor.getString(0);
            int gia = cursor.getInt(1);
            int dm = cursor.getInt(2);
            int dc = cursor.getInt(3);
            int gd = cursor.getInt(4);
            
            // Công thức tính toán của bạn
            int tongDien = (dm - dc) * gd;
            int tongCong = gia + tongDien;

            result += "Phòng: " + so + " | Tổng: " + tongCong + "k\n";
            result += "(Điện: " + (dm-dc) + " số * " + gd + "đ = " + tongDien + ")\n----------\n";
        }
        cursor.close();
        db.close();
        return result;
    }
}
