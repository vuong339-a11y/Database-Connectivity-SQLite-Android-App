package com.example.databaseconnectivity; // Bạn nhớ đổi lại đúng package name của bạn

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import android.os.Environment;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    private EditText etPassword, etContent;
    private Button btnUnlock, btnSave;
    private TextView tvDiaryLabel;

    private final String FILE_NAME = "data.txt";
    private final String CORRECT_PASSWORD = "1234"; // Thay đổi mật khẩu của bạn tại đây


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        // Cách làm Fullscreen cho Android 11 (API 30) trở lên
        android.view.WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            // Ẩn cả thanh trạng thái (Status bar) và thanh điều hướng (Navigation bar)
            controller.hide(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
            // Thiết lập chế độ vuốt nhẹ để hiển thị lại tạm thời (Behavior)
            controller.setSystemBarsBehavior(android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    } else {
        // Cách làm Fullscreen cũ cho các máy chạy Android 10 trở xuống
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
        // Ánh xạ các thuộc tính giao diện
        etPassword = findViewById(R.id.etPassword);
        etContent = findViewById(R.id.etContent);
        btnUnlock = findViewById(R.id.btnUnlock);
        btnSave = findViewById(R.id.btnSave);
        tvDiaryLabel = findViewById(R.id.tvDiaryLabel);

        // Sự kiện khi nhấn nút Mở Nhật Ký
        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputPass = etPassword.getText().toString();
                if (inputPass.equals(CORRECT_PASSWORD)) {
                    // Nếu đúng mật khẩu, hiện các trường chỉnh sửa nhật ký lên
                    //tvDiaryLabel.setVisibility(View.VISIBLE);
                    etContent.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                    etPassword.setVisibility(View.GONE);
                    btnUnlock.setVisibility(View.GONE);
                    // Tiến hành đọc file data.txt và giải mã đưa vào textbox
                    String decodedData = readAndDecodeFile();
                    etContent.setText(decodedData);
                    Toast.makeText(MainActivity.this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Sai mật khẩu! Không thể mở.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sự kiện khi nhấn nút Lưu Nhật Ký
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rawText = etContent.getText().toString();
                boolean success = encodeAndWriteFile(rawText);
                if (success) {
                    Toast.makeText(MainActivity.this, "Đã mã hóa và lưu thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi khi lưu file!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Hàm mã hóa dịch ASCII lên 2 đơn vị và ghi vào file data.txt
     */
    private boolean encodeAndWriteFile(String text) {
    StringBuilder encodedText = new StringBuilder();
    
    // Vòng lặp dịch mã ASCII + 2
    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        char encodedChar = (char) (c + 2);
        encodedText.append(encodedChar);
    }

    try {
        // Lấy đường dẫn đến thư mục Download công khai của máy
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        
        // Tạo file data.txt nằm trong thư mục Download
        File myFile = new File(downloadDir, FILE_NAME);
        
        // Tiến hành ghi file
        FileWriter writer = new FileWriter(myFile);
        writer.append(encodedText.toString());
        writer.flush();
        writer.close();
        
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    /**
     * Hàm đọc file data.txt và giải mã dịch ASCII lùi 2 đơn vị
     */
private String readAndDecodeFile() {
    StringBuilder rawContent = new StringBuilder();
    
    try {
        // Tìm đến file data.txt trong thư mục Download
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File myFile = new File(downloadDir, FILE_NAME);
        
        // Nếu file chưa tồn tại (lần đầu dùng app), trả về chuỗi rỗng luôn
        if (!myFile.exists()) {
            return "";
        }

        // Tiến hành đọc file
        FileReader reader = new FileReader(myFile);
        BufferedReader br = new BufferedReader(reader);
        String line;
        
        while ((line = br.readLine()) != null) {
            rawContent.append(line).append("\n");
        }
        br.close();
        reader.close();
        
        if (rawContent.length() > 0) {
            rawContent.setLength(rawContent.length() - 1);
        }

    } catch (Exception e) {
        e.printStackTrace();
        return "";
    }

    // Tiến hành giải mã chuỗi: dịch ngược mã ASCII - 2
    String encryptedData = rawContent.toString();
    StringBuilder decodedText = new StringBuilder();
    
    for (int i = 0; i < encryptedData.length(); i++) {
        char c = encryptedData.charAt(i);
        char decodedChar = (char) (c - 2);
        decodedText.append(decodedChar);
    }

    return decodedText.toString();
    }
 @Override
protected void onStop() {
    super.onStop();
    
    // 1. Xóa nội dung nhật ký đang hiển thị trong textbox để bảo mật
    etContent.setText("");
    
    // 2. ẨN ô viết nhật ký và nút Lưu đi
    tvDiaryLabel.setVisibility(View.GONE);
    etContent.setVisibility(View.GONE);
    btnSave.setVisibility(View.GONE);
    
    // 3. HIỆN lại ô nhập mật khẩu và nút Mở khóa
    etPassword.setVisibility(View.VISIBLE);
    btnUnlock.setVisibility(View.VISIBLE);
    
    // 4. Xóa chữ trong ô mật khẩu cũ để người dùng phải nhập lại từ đầu
    etPassword.setText("");
}

}
