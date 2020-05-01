package com.example.qrscan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QREncoder extends AppCompatActivity {
    EditText textContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encoder);

        textContent = findViewById(R.id.gen_content);
        Button btn = findViewById(R.id.btn_generate);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String contentStr = textContent.getText().toString();
                    if (!contentStr.equals("")) {
                        BitMatrix matrix = new MultiFormatWriter().encode(contentStr, BarcodeFormat.QR_CODE, 300, 300);
                        int width = matrix.getWidth();
                        int height = matrix.getHeight();

                        int[] pixels = new int[height * width];

                        for (int x = 0; x < height; x++)
                            for (int y = 0; y < width; y++)
                                if (matrix.get(x, y))
                                    pixels[x*height+y] = Color.BLACK;

                        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

                        ImageView image1 = new ImageView(QREncoder.this);
                        image1.setImageBitmap(bitmap);

                        new AlertDialog.Builder(QREncoder.this)
                                .setTitle(R.string.app_name)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setView(image1)
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                    else {
                        Toast.makeText(QREncoder.this, "Text cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
