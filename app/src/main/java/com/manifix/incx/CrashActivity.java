package com.manifix.incx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        TextView crashTitle = findViewById(R.id.crash_title);
        TextView crashMessage = findViewById(R.id.crash_message);
        Button restartButton = findViewById(R.id.restart_button);
        Button exitButton = findViewById(R.id.exit_button);

        String crashInfo = getIntent().getStringExtra("crash_info");
        if (crashInfo != null) {
            crashMessage.setText(crashInfo);
        }

        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(CrashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        exitButton.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent back button from closing the crash screen
    }
}
