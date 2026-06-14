package com.example.mynotepad; // Bạn nhớ đổi lại đúng package name của bạn

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

public class MainActivity extends AppCompatActivity {

    private EditText etPassword, etContent;
    private Button btnUnlock, btnSave;
    private TextView tvDiaryLabel;

    private final String FILE_NAME = "data.txt";
    private final String CORRECT_PASSWORD = "1234"; // Thay đổi mật khẩu của bạn tại đây

    @Override
    protected void Bundle savedInstanceState) {
        super.Bundle(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    tvDiaryLabel.setVisibility(View.VISIBLE);
                    etContent.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                    
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
        
        // Vòng lặp duyệt từng ký tự để dịch mã ASCII + 2
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Dịch chuyển mã ASCII lên 2 đơn vị
            char encodedChar = (char) (c + 2);
            encodedText.append(encodedChar);
        }

        // Tiến hành ghi chuỗi đã mã hóa vào bộ nhớ trong của App (Internal Storage)
        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
            fos.write(encodedText.toString().getBytes());
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
        
        // Đọc nội dung file thô từ bộ nhớ
        try (FileInputStream fis = openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            
            String line;
            while ((line = br.readLine()) != null) {
                rawContent.append(line).append("\n");
            }
            
            // Xóa bớt ký tự xuống dòng thừa ở cuối file do vòng lặp
            if (rawContent.length() > 0) {
                rawContent.setLength(rawContent.length() - 1);
            }

        } catch (Exception e) {
            // Nếu file chưa tồn tại (lần đầu mở app), trả về chuỗi rỗng
            return "";
        }

        // Tiến hành giải mã chuỗi thô: dịch ngược mã ASCII - 2
        String encryptedData = rawContent.toString();
        StringBuilder decodedText = new StringBuilder();
        
        for (int i = 0; i < encryptedData.length(); i++) {
            char c = encryptedData.charAt(i);
            char decodedChar = (char) (c - 2);
            decodedText.append(decodedChar);
        }

        return decodedText.toString();
    }
}
