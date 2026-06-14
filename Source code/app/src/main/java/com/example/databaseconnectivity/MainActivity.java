package com.example.databaseconnectivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText edSo, edGia, edMoi, edCu, edGiaDien, edKetQua;
    MyDB MyDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        MyDBHandler = new MyDB(this, null, null, 1);
        
        edSo = findViewById(R.id.editSoPhong);
        edGia = findViewById(R.id.editGiaPhong);
        edMoi = findViewById(R.id.editDienMoi);
        edCu = findViewById(R.id.editDienCu);
        edGiaDien = findViewById(R.id.editGiaDien);
        edKetQua = findViewById(R.id.editKetQua); // Ánh xạ ô kết quả để copy
    }

    private void adjustRoom(int val) {
        try {
            int num = Integer.parseInt(edSo.getText().toString());
            edSo.setText(String.valueOf(num + val));
        } catch (Exception e) { edSo.setText("101"); }
    }
    public void next(View v) { adjustRoom(1); }
    public void pre(View v) { adjustRoom(-1); }
    public void next10(View v) { adjustRoom(10); }
    public void pre10(View v) { adjustRoom(-10); }

    public void Save(View view) {
        try {
            MyDBHandler.Save_Data(edSo.getText().toString(),
                    Integer.parseInt(edGia.getText().toString()),
                    Integer.parseInt(edMoi.getText().toString()),
                    Integer.parseInt(edCu.getText().toString()),
                    Integer.parseInt(edGiaDien.getText().toString()));
            Toast.makeText(this, "Đã Lưu dữ liệu!", Toast.LENGTH_SHORT).show();
            Display(null); // Tự động làm mới danh sách sau khi lưu
        } catch (Exception e) { Toast.makeText(this, "Nhập thiếu số liệu!", Toast.LENGTH_SHORT).show(); }
    }

    // Hàm hiển thị đổ dữ liệu trực tiếp vào ô Textbox cho phép COPY đi nơi khác
    public void Display(View v) {
        String data = MyDBHandler.Display_Data();
        if (data.equals("")) {
            edKetQua.setText("Chưa có dữ liệu phòng nào!");
        } else {
            edKetQua.setText(data);
        }
    }

    public void Delete(View v) {
        if (MyDBHandler.Delete_Data(edSo.getText().toString()) > 0) {
            Toast.makeText(this, "Đã Xóa phòng!", Toast.LENGTH_SHORT).show();
            Display(null); // Làm mới danh sách sau khi xóa
        }
    }

    public void Backup(View v) {
        Toast.makeText(this, MyDBHandler.exportDatabase(), Toast.LENGTH_LONG).show();
    }
}
