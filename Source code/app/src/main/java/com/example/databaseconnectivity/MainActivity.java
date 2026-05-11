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
        MyDBHandler = new MyDB(this, null, null, 1);
        edSo = findViewById(R.id.editSoPhong);
        edGia = findViewById(R.id.editGiaPhong);
        edMoi = findViewById(R.id.editDienMoi);
        edCu = findViewById(R.id.editDienCu);
        edGiaDien = findViewById(R.id.editGiaDien);
    }

    // Hàm hỗ trợ đổi số phòng
    private void changeRoom(int delta) {
        try {
            int currentRoom = Integer.parseInt(edSo.getText().toString());
            edSo.setText(String.valueOf(currentRoom + delta));
        } catch (Exception e) {
            edSo.setText("101"); // Nếu chưa có số thì mặc định bắt đầu từ 101
        }
    }

    public void next(View v) { changeRoom(1); }
    public void pre(View v) { changeRoom(-1); }
    public void next10(View v) { changeRoom(10); }
    public void pre10(View v) { changeRoom(-10); }

    public void Save(View view) {
        try {
            MyDBHandler.Save_Data(edSo.getText().toString(), 
                Integer.parseInt(edGia.getText().toString()),
                Integer.parseInt(edMoi.getText().toString()),
                Integer.parseInt(edCu.getText().toString()),
                Integer.parseInt(edGiaDien.getText().toString()));
            Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) { Toast.makeText(this, "Lỗi nhập liệu!", Toast.LENGTH_SHORT).show(); }
    }

    public void Backup(View v) {
        String msg = MyDBHandler.exportDatabase();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void Delete(View v) {
        if (MyDBHandler.Delete_Data(edSo.getText().toString()) > 0) 
            Toast.makeText(this, "Đã xóa!", Toast.LENGTH_SHORT).show();
    }

    public void Display(View v) {
        String data = MyDBHandler.Display_Data();
        new android.app.AlertDialog.Builder(this).setMessage(data).setTitle("Danh sách").show();
    }
}
