package com.example.qrscan;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            final String scanResult = bundle != null ? bundle.getString("result") : null;
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Decode Result")
                    .setMessage(scanResult)
                    .setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Copy to Clipboard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("result", scanResult);
                            clipboard.setPrimaryClip(clipData);
                            Toast.makeText(MainActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
        else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Decode Result")
                    .setMessage("Can't recognize QR Code.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button encoder = findViewById(R.id.btn_encoder);
        Button decoder = findViewById(R.id.btn_decoder);

        View.OnClickListener mainListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (v.getId()) {
                    case R.id.btn_encoder:
                        intent = new Intent(MainActivity.this, QREncoder.class);
                        startActivity(intent);
                        break;
                    case R.id.btn_decoder:
                        intent = new Intent(MainActivity.this, QRDecoder.class);
                        startActivityForResult(intent, FragmentActivity.RESULT_CANCELED);
                        break;
                    default:
                        break;
                }
            }
        };

        encoder.setOnClickListener(mainListener);
        decoder.setOnClickListener(mainListener);
    }
}
