package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    protected FirebaseUser user;
    protected Button noteButton;
    protected Button taskButton;
    protected Button eventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // FirebaseAuth part
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // App bar part
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation drawer part
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Set the text of the welcome message in the navigation drawer
        View header = navigationView.getHeaderView(0);
        TextView message = header.findViewById(R.id.welcome_textView);
        if (user.getDisplayName() != null) {
            message.setText(String.format("Welcome %s!", user.getDisplayName()));
        } else {
            message.setText(String.format("Welcome %s!", user.getEmail()));
        }

        // Bottom bar part
        setOnClickListeners();
    }

    // Check if user is logged in.
    // Redirect the user to the login page if they have not logged in.
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.daily_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_today) {
            Intent intentToday = new Intent(BaseActivity.this, DailyLogActivity.class);
            intentToday.putExtra("year", 0);
            finish();
            startActivity(intentToday);
        } else if (id == R.id.nav_this_month) {
            Intent intentThisMonth = new Intent(BaseActivity.this, MonthlyLogActivity.class);
            intentThisMonth.putExtra("year", 0);
            finish();
            startActivity(intentThisMonth);
        } else if (id == R.id.nav_calendar) {
            startActivity(new Intent(BaseActivity.this, CalendarActivity.class));
            finish(); // destroy the current activity after finish
        } else if (id == R.id.nav_collections) {

        } else if (id == R.id.nav_eisen) {

        } else if (id == R.id.nav_new) {

        } else if (id == R.id.nav_signOut) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // destroy the current activity after finish
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Set the necessary onclicklisteners
    private void setOnClickListeners() {
        noteButton = findViewById(R.id.note);
        taskButton = findViewById(R.id.task);
        eventButton = findViewById(R.id.event);

        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNew = new Intent(BaseActivity.this, EntryFormActivity.class);
                intentNew.putExtra("type", "note");
                startActivity(intentNew);
            }
        });

        taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNew = new Intent(BaseActivity.this, EntryFormActivity.class);
                intentNew.putExtra("type", "task");
                startActivity(intentNew);
            }
        });

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNew = new Intent(BaseActivity.this, EntryFormActivity.class);
                intentNew.putExtra("type", "event");
                startActivity(intentNew);
            }
        });
    }
}
