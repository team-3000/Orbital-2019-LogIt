package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class DailyLogActivity extends BaseActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the content of the frame layout accordingly.
        // The frame layout serves as a container for the content you want to put.
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_daily_log, contentFrameLayout);

        /* ----------------Test, feel free to comment out -------------------------------------- */
        TextView tvTest = findViewById(R.id.tvTest);
        tvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyLogActivity.this, EntryActivity.class);
                intent.putExtra("type", "task");
                String directory = String.format(Locale.US, "users/%s/task/2019/Jun/rMpaHrQOZkQUkGlbm2BK",
                        user.getUid());
                intent.putExtra("directory", directory);
                intent.putExtra("entryId", "rMpaHrQOZkQUkGlbm2BK");
                startActivity(intent);
            }
        });
        /* --------------------------------------------------------------------------------------*/
    }
}
