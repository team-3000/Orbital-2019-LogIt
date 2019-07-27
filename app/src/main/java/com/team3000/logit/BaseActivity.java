package com.team3000.logit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Stack;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
     protected static Stack<Activity> activityStack = new Stack<>();
    protected static boolean cameFromNavMenu = false;
    protected int currPosition; // indicates which activity of the navigation drawer that the user is currently at
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
        // this.currPosition = R.id.base_activity; // overriden in some children classes

        // Set the text of the welcome message in the navigation drawer
        View header = navigationView.getHeaderView(0);
        TextView message = header.findViewById(R.id.welcome_textView);
        if (user.getDisplayName() != null) {
            message.setText(String.format("Welcome %s!", user.getDisplayName()));
        } else {
            message.setText(String.format("Welcome %s!", user.getEmail()));
        }

        // Set clickListeners
        setOnClickListeners();

        // Handle backstack
        if (cameFromNavMenu) {
            manageBackStack();
            cameFromNavMenu = false;
        }
        activityStack.add(this);
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
        
        // refactor this such that instead of passing the boolean to intent, create a superclass for
        // all the activities in the option menu to extend from
        // then put the boolean in that superclass
        boolean isPartOfBaseActivities = getIntent().getBooleanExtra("BaseActivities", false);
        boolean isTodayDailyLog = getIntent().getBooleanExtra("isTodayDailyLog", false);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        // Always redirect the user to today's daily log if the current activity is part of the activities
        // in the navigation menu and not currently at today's daily log
        if (isPartOfBaseActivities && !isTodayDailyLog) {
            Intent intent = new Intent(BaseActivity.this, DailyLogActivity.class)
                    .putExtra("year", 0)
                    .putExtra("isTodayDailyLog", true)
                    .putExtra("BaseActivities", true);

            startActivity(intent); // don't need to call finish cuz this will be handled by the manageBackStack method
        } else {
            super.onBackPressed();
        }
        this.finish();
        Log.i("BaseActivity", "After calling finish");
    }

    // Show dropdown menu on the top right corner of app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            onSearchRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // Prevent app from sending intent to the same current activity when user click on the option
        // which matches current activity
        if (id == currPosition) {
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }

        if (id == R.id.new_collection) {
            showNewCollectionDialog();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        Intent intent = null;
        if (id == R.id.nav_today) {
            intent = new Intent(BaseActivity.this, DailyLogActivity.class)
                    .putExtra("isTodayDailyLog", true);
        } else if (id == R.id.nav_this_month) {
            intent = new Intent(BaseActivity.this, MonthlyLogActivity.class);
        } else if (id == R.id.nav_calendar) {
            intent = new Intent(BaseActivity.this, CalendarActivity.class);
        } else if (id == R.id.nav_collections) {
            intent = new Intent(BaseActivity.this, CollectionListActivity.class);
        } else if (id == R.id.nav_eisen) {
            intent = new Intent(BaseActivity.this, EisenhowerActivity.class);
        } else if (id == R.id.nav_signOut) {
            mAuth.signOut();
            intent = new Intent(this, LoginActivity.class);
        }

        // new stuff (remember to reflect any newly added extra in the intent in onBackPressed method
        // need to refactor the code later to prevent these hastle (can create a method call which
        // takes in the intent and put necessary extras that are out of those if statements
        intent.putExtra("BaseActivities", true);

        // for managing backstack
        if (id != R.id.nav_signOut) {
            cameFromNavMenu = true;
        }

        if (this instanceof DailyLogActivity && id != currPosition) {
            this.finish();
        }
        startActivity(intent);

        if (id == R.id.nav_signOut) {
            manageBackStack();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Set the necessary onclicklisteners
    private void setOnClickListeners() {
        noteButton = findViewById(R.id.note);
        taskButton = findViewById(R.id.task);
        eventButton = findViewById(R.id.event);

        noteButton.setOnClickListener(v -> {
            Intent intentNew = new Intent(BaseActivity.this, EntryListActivity.class);
            intentNew.putExtra("trackType", "noteStore");
            startActivity(intentNew);
        });

        taskButton.setOnClickListener(v -> {
            Intent intentNew = new Intent(BaseActivity.this, EntryListActivity.class);
            intentNew.putExtra("trackType", "taskStore");
            startActivity(intentNew);
        });

        eventButton.setOnClickListener(v -> {
            Intent intentNew = new Intent(BaseActivity.this, EntryListActivity.class);
            intentNew.putExtra("trackType", "eventStore");
            startActivity(intentNew);
        });
    }

    // Clear the activity backstack when user clicks on another option in the navigation menu
    private void manageBackStack() {
        int size = activityStack.size();

        for (int i = 0; i < size; i++) {
            Activity activity = activityStack.pop();
            if (!activity.isDestroyed()) {
                activity.finish();
            }
        }
    }

    private void showNewCollectionDialog() {
        // Handle Fragment transaction & backstack stuff
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog fragment
        NewCollectionFragment fragment = new NewCollectionFragment();
        fragment.show(getSupportFragmentManager(), "dialog");
    }
}
