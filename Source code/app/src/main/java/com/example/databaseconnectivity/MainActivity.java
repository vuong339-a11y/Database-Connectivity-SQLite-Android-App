package com.example.databaseconnectivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText edSo, edGia, edMoi, edCu, edGiaDien;
    MyDB MyDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        MyDBHandler = new MyDB(this, "phongtro", null, 1);
        
        edSo = findViewById(R.id.editSoPhong);
        edGia = findViewById(R.id.editGiaPhong);
        edMoi = findViewById(R.id.editDienMoi);
        edCu = findViewById(R.id.editDienCu);
        edGiaDien = findViewById(R.id.editGiaDien);
    }

    public void Save(View view) {
        try {
            String so = edSo.getText().toString();
            int gia = Integer.parseInt(edGia.getText().toString());
            int dm = Integer.parseInt(edMoi.getText().toString());
            int dc = Integer.parseInt(edCu.getText().toString());
            int gd = Integer.parseInt(edGiaDien.getText().toString());

            MyDBHandler.Save_Data(so, gia, dm, dc, gd);
            Toast.makeText(this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Vui lòng nhập đủ số liệu!", Toast.LENGTH_SHORT).show();
        }
    }

    public void Display(View v) {
        String data = MyDBHandler.Display_Data();
        if(data.equals("")) Toast.makeText(this, "Trống!", Toast.LENGTH_SHORT).show();
        else {
            // Hiển thị tạm qua Toast, bạn có thể thay bằng TextView nếu muốn
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(data).setTitle("Hóa Đơn Phòng Trọ").show();
        }
    }

    public void Delete(View v) {
        String so = edSo.getText().toString();
        if (MyDBHandler.Delete_Data(so) > 0) {
            Toast.makeText(this, "Đã xóa phòng " + so, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không tìm thấy phòng!", Toast.LENGTH_SHORT).show();
        }
    }
}
