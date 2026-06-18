package com.example.pthue; // Bạn nhớ đổi lại đúng package name của bạn

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText etTenKhoanChi, etSoTien, etSoLuong;
    private Button btnThem, btnSua, btnXem;
    private ListView lvChiTieu;

    private SQLiteDatabase database;
    private String dbPath;
    private ArrayList<String> dsChiTieuString = new ArrayList<>();
    private ArrayList<Integer> dsId = new ArrayList<>(); // Lưu ID tương ứng để sửa
    private ArrayAdapter<String> adapter;
    private int selectedId = -1; // Lưu ID của dòng đang chọn để sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Cấu hình Fullscreen tràn viền 100% & Ẩn thanh tên app
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }

        // 2. Ánh xạ các thành phần giao diện
        etTenKhoanChi = findViewById(R.id.etTenKhoanChi);
        etSoTien = findViewById(R.id.etSoTien);
        etSoLuong = findViewById(R.id.etSoLuong);
        btnThem = findViewById(R.id.btnThem);
        btnSua = findViewById(R.id.btnSua);
        btnXem = findViewById(R.id.btnXem);
        lvChiTieu = findViewById(R.id.lvChiTieu);

        // Cấu hình ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dsChiTieuString);
        lvChiTieu.setAdapter(adapter);

        // 3. Khởi tạo Database SQLite trong thư mục Download
        initDatabase();

        // 4. Các sự kiện nút bấm
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themKhoanChi();
            }
        });

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suaKhoanChi();
            }
        });

        btnXem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hienThiDanhSach();
                Toast.makeText(MainActivity.this, "Đã cập nhật danh sách!", Toast.LENGTH_SHORT).show();
            }
        });

        // Click vào 1 dòng trong danh sách để chuẩn bị sửa
        lvChiTieu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedId = dsId.get(position);
                layThongTinChiTiet(selectedId);
            }
        });

        // Mặc định mở app lên là load danh sách luôn
        hienThiDanhSach();
    }

    private void initDatabase() {
        try {
            // Lấy đường dẫn đến thư mục Download của Samsung A25
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File dbFile = new File(downloadDir, "ChiTieu.db");
            dbPath = dbFile.getAbsolutePath();

            // Mở hoặc tạo mới database
            database = SQLiteDatabase.openOrCreateDatabase(dbPath, null);

            // Tạo bảng nếu chưa tồn tại
            String sqlCreate = "CREATE TABLE IF NOT EXISTS tblChiTieu (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ten TEXT, " +
                    "sotien INTEGER, " +
                    "soluong INTEGER, " +
                    "tong INTEGER)";
            database.execSQL(sqlCreate);

            // Kiểm tra nếu bảng trống thì tự động thêm 3 record mẫu sẵn
            Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM tblChiTieu", null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();

            if (count == 0) {
                database.execSQL("INSERT INTO tblChiTieu (ten, sotien, soluong, tong) VALUES ('Ăn sáng', 35000, 1, 35000)");
                database.execSQL("INSERT INTO tblChiTieu (ten, sotien, soluong, tong) VALUES ('Xăng xe', 50000, 2, 100000)");
                database.execSQL("INSERT INTO tblChiTieu (ten, sotien, soluong, tong) VALUES ('Cà phê họp mặt', 45000, 3, 135000)");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khởi tạo DB trong thư mục Download!", Toast.LENGTH_LONG).show();
        }
    }

    private void hienThiDanhSach() {
        dsChiTieuString.clear();
        dsId.clear();
        
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM tblChiTieu ORDER BY id DESC", null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String ten = cursor.getString(1);
                int sotien = cursor.getInt(2);
                int soluong = cursor.getInt(3);
                int tong = cursor.getInt(4);

                dsId.add(id);
                // Định dạng dòng hiển thị trong ListView
                dsChiTieuString.add(id + ". " + ten + "\nGiá: " + sotien + "đ x " + soluong + " = " + tong + "đ");
            }
            cursor.close();
            adapter.notifyDataSetChanged(); // Cập nhật lại giao diện ListView
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void themKhoanChi() {
        String ten = etTenKhoanChi.getText().toString().trim();
        String txtSotien = etSoTien.getText().toString().trim();
        String txtSoluong = etSoLuong.getText().toString().trim();

        if (ten.isEmpty() || txtSotien.isEmpty() || txtSoluong.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int sotien = Integer.parseInt(txtSotien);
        int soluong = Integer.parseInt(txtSoluong);
        int tong = sotien * soluong; // Tự động tính tổng

        try {
            String sql = "INSERT INTO tblChiTieu (ten, sotien, soluong, tong) VALUES (?, ?, ?, ?)";
            database.execSQL(sql, new Object[]{ten, sotien, soluong, tong});
            
            xoaTrangInput();
            hienThiDanhSach();
            Toast.makeText(this, "Thêm khoản chi thành công!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi thêm dữ liệu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void suaKhoanChi() {
        if (selectedId == -1) {
            Toast.makeText(this, "Vui lòng chọn 1 khoản chi từ danh sách bên dưới để sửa!", Toast.LENGTH_SHORT).show();
            return;
        }

        String ten = etTenKhoanChi.getText().toString().trim();
        String txtSotien = etSoTien.getText().toString().trim();
        String txtSoluong = etSoLuong.getText().toString().trim();

        if (ten.isEmpty() || txtSotien.isEmpty() || txtSoluong.isEmpty()) {
            Toast.makeText(this, "Thông tin sửa không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        int sotien = Integer.parseInt(txtSotien);
        int soluong = Integer.parseInt(txtSoluong);
        int tong = sotien * soluong;

        try {
            String sql = "UPDATE tblChiTieu SET ten=?, sotien=?, soluong=?, tong=? WHERE id=?";
            database.execSQL(sql, new Object[]{ten, sotien, soluong, tong, selectedId});
            
            xoaTrangInput();
            selectedId = -1; // Reset lại id chọn
            hienThiDanhSach();
            Toast.makeText(this, "Đã sửa thông tin thành công!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
        }
    }

    private void layThongTinChiTiet(int id) {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM tblChiTieu WHERE id = ?", new String[]{String.valueOf(id)});
            if (cursor.moveToFirst()) {
                etTenKhoanChi.setText(cursor.getString(1));
                etSoTien.setText(String.valueOf(cursor.getInt(2)));
                etSoLuong.setText(String.valueOf(cursor.getInt(3)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xoaTrangInput() {
        etTenKhoanChi.setText("");
        etSoTien.setText("");
        etSoLuong.setText("");
    }
}
