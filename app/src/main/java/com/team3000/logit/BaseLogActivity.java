package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class BaseLogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the content of the frame layout accordingly.
        // The frame layout serves as a container for the content you want to put.
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_base_log, contentFrameLayout);


//        TextView tvTest = findViewById(R.id.tvLogType);
//        int today = getIntent().getIntExtra("day", 0);
//        String monthNow = getIntent().getStringExtra("month");
//        int yearNow = getIntent().getIntExtra("year", 0);
//        tvTest.setText(String.format(Locale.US, "%d/%s/%d", today, monthNow, yearNow));
//        tvTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Placeholder entryId, change here after all test Edit/Delete ops
//                String entryId = "CCVvhaumJvI00SAFMEud";
//                Intent intent = new Intent(BaseLogActivity.this, EntryActivity.class);
//                intent.putExtra("type", "task");
//                String directory = String.format(Locale.US, "users/%s/task/2019/Jun/%s",
//                        user.getUid(), entryId);
//                intent.putExtra("directory", directory);
//                intent.putExtra("entryId", entryId);
//                startActivity(intent);
//            }
//        });
    }
}
