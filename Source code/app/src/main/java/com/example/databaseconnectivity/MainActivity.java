package com.example.sqlite_download; // Đổi lại đúng package của bạn

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    EditText edtId, edtSoPhong, edtGia, edtDien, edtTong;
    Button btnTaoMoi, btnLuu, btnXem;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ
        edtId = findViewById(R.id.edtId);
        edtSoPhong = findViewById(R.id.edtSoPhong);
        edtGia = findViewById(R.id.edtGia);
        edtDien = findViewById(R.id.edtDien);
        edtTong = findViewById(R.id.edtTong);
        
        btnTaoMoi = findViewById(R.id.btnTaoMoi);
        btnLuu = findViewById(R.id.btnLuu);
        btnXem = findViewById(R.id.btnXem);

        // Xin quyền lưu trữ
        checkPermissions();

        dbHelper = new DatabaseHelper(this);

        // Nút Tạo mới: Xóa trống các ô để nhập lại
        btnTaoMoi.setOnClickListener(v -> {
            edtId.setText("");
            edtSoPhong.setText("");
            edtGia.setText("");
            edtDien.setText("");
            edtTong.setText("");
            Toast.makeText(MainActivity.this, "Đã xóa trống form", Toast.LENGTH_SHORT).show);
        });

        // Nút Lưu: Tính tổng tiền và lưu vào SQLite ở thư mục Download
        btnLuu.setOnClickListener(v -> {
            String soPhong = edtSoPhong.getText().toString().trim();
            String giaStr = edtGia.getText().toString().trim();
            String dienStr = edtDien.getText().toString().trim();

            if (soPhong.isEmpty() || giaStr.isEmpty() || dienStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            double gia = Double.parseDouble(giaStr);
            double dien = Double.parseDouble(dienStr);
            double tong = gia * dien;

            // Hiển thị tổng tiền lên giao diện luôn
            edtTong.setText(String.valueOf(tong));

            // Lưu vào database
            boolean isInserted = dbHelper.insertData(soPhong, gia, dien, tong);
            if (isInserted) {
                Toast.makeText(MainActivity.this, "Đã lưu vào data.db tại thư mục Download!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Lưu thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Xem: Đọc dữ liệu từ SQLite và hiển thị dạng chuỗi lên ô Tổng tiền (hoặc ô ID) để xem nhanh
        btnXem.setOnClickListener(v -> {
            Cursor cursor = dbHelper.getAllData();
            if (cursor.getCount() == 0) {
                Toast.makeText(MainActivity.this, "Không có dữ liệu!", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();
            while (cursor.moveToNext()) {
                stringBuilder.append("ID: ").append(cursor.getInt(0)).append(" | ")
                        .append("Phòng: ").append(cursor.getString(1)).append(" | ")
                        .append("Giá: ").append(cursor.getDouble(2)).append(" | ")
                        .append("Điện: ").append(cursor.getDouble(3)).append(" | ")
                        .append("Tổng: ").append(cursor.getDouble(4)).append("\n\n");
            }
            
            // Hiển thị tạm toàn bộ dữ liệu vào ô Tổng tiền dưới dạng text để xem
            edtTong.setText(stringBuilder.toString());
            cursor.close();
        });
    }

    // Hàm xin quyền truy cập bộ nhớ cho các đời Android mới và cũ
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }
}
