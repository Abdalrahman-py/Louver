package com.example.louver;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.louver.data.auth.SessionManager;
import com.example.louver.ui.admin.AdminDashboardFragment;
import com.example.louver.ui.auth.AuthActivity;
import com.example.louver.ui.favorites.FavoritesFragment;
import com.example.louver.ui.home.HomeFragment;
import com.example.louver.ui.mybookings.MyBookingsFragment;
import com.example.louver.ui.profile.ProfileFragment;
import com.example.louver.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private BottomNavigationView bottomNavigation;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private boolean suppressBottomNavCallback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            Intent i = new Intent(this, AuthActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(android.view.View drawerView) {
                // Show admin item only for ADMIN role
                String role = sessionManager.getUserRole();
                boolean isAdmin = "ADMIN".equals(role);
                navigationView.getMenu()
                        .findItem(R.id.drawer_admin_dashboard)
                        .setVisible(isAdmin);
            }
        });
        drawerToggle.syncState();

        // Navigation drawer items
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.drawer_profile) {
                openFragment(new ProfileFragment(), -1);
            } else if (id == R.id.drawer_favorites) {
                openFragment(new FavoritesFragment(), R.id.nav_favorites);
            } else if (id == R.id.drawer_bookings) {
                openFragment(new MyBookingsFragment(), R.id.nav_bookings);
            } else if (id == R.id.drawer_settings) {
                openFragment(new SettingsFragment(), R.id.nav_settings);
            } else if (id == R.id.drawer_admin_dashboard) {
                openFragment(new AdminDashboardFragment(), -1);
            } else if (id == R.id.drawer_logout) {
                sessionManager.clearSession();
                Intent intent = new Intent(this, AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        // Bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Only add fragment the first time (avoid re-adding on rotation)
        if (savedInstanceState == null) {
            openFragment(new HomeFragment(), R.id.nav_home);
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (suppressBottomNavCallback) return true;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                openFragment(new HomeFragment(), R.id.nav_home);
                return true;
            } else if (id == R.id.nav_favorites) {
                openFragment(new FavoritesFragment(), R.id.nav_favorites);
                return true;
            } else if (id == R.id.nav_bookings) {
                openFragment(new MyBookingsFragment(), R.id.nav_bookings);
                return true;
            } else if (id == R.id.nav_settings) {
                openFragment(new SettingsFragment(), R.id.nav_settings);
                return true;
            }
            return false;
        });
    }

    private void openFragment(Fragment fragment, int bottomNavItemId) {
        if (isFinishing()) return;

        // Prevent duplicate replace of same fragment type
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (current != null && current.getClass().equals(fragment.getClass())) {
            if (bottomNavItemId != -1) {
                suppressBottomNavCallback = true;
                bottomNavigation.getMenu().findItem(bottomNavItemId).setChecked(true);
                suppressBottomNavCallback = false;
            }
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        if (bottomNavItemId != -1) {
            suppressBottomNavCallback = true;
            bottomNavigation.getMenu().findItem(bottomNavItemId).setChecked(true);
            suppressBottomNavCallback = false;
        }
    }
}
