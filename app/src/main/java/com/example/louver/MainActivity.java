package com.example.louver;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.louver.data.auth.LocaleHelper;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.ui.admin.AdminAccessGuard;
import com.example.louver.ui.admin.AdminDashboardFragment;
import com.example.louver.ui.auth.AuthActivity;
import com.example.louver.ui.categories.CategoriesFragment;
import com.example.louver.ui.favorites.FavoritesFragment;
import com.example.louver.ui.home.HomeFragment;
import com.example.louver.ui.mybookings.MyBookingsFragment;
import com.example.louver.ui.onboarding.OnboardingActivity;
import com.example.louver.ui.profile.ProfileViewFragment;
import com.example.louver.ui.search.SearchFilterFragment;
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

    // Cached fragment instances — created once, reused on every tab switch
    private HomeFragment        homeFragment;
    private CategoriesFragment  categoriesFragment;
    private SearchFilterFragment searchFragment;
    private MyBookingsFragment  bookingsFragment;
    private ProfileViewFragment profileFragment;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyPersistedLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        // First launch → show onboarding
        if (!sessionManager.isOnboardingShown()) {
            Intent i = new Intent(this, OnboardingActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

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
        drawerToggle.syncState();

        // Navigation drawer items
        navigationView = findViewById(R.id.navigationView);

        // Set admin item visibility ONCE here — never re-checked on drawer open
        boolean isAdmin = AdminAccessGuard.isAdmin(sessionManager);
        navigationView.getMenu()
                .findItem(R.id.drawer_admin_dashboard)
                .setVisible(isAdmin);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.drawer_profile) {
                openFragment(new ProfileViewFragment(), R.id.nav_profile);
            } else if (id == R.id.drawer_favorites) {
                openFragment(new FavoritesFragment(), -1);
            } else if (id == R.id.drawer_bookings) {
                openFragment(new MyBookingsFragment(), R.id.nav_bookings);
            } else if (id == R.id.drawer_settings) {
                openFragment(new SettingsFragment(), -1);
            } else if (id == R.id.drawer_admin_dashboard) {
                if (AdminAccessGuard.isAdmin(sessionManager)) {
                    openFragment(new AdminDashboardFragment(), -1);
                }
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
            homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, homeFragment, "home")
                    .commit();
            bottomNavigation.getMenu().findItem(R.id.nav_home).setChecked(true);
        } else {
            // Restore cached references after rotation
            homeFragment        = (HomeFragment)        getSupportFragmentManager().findFragmentByTag("home");
            categoriesFragment  = (CategoriesFragment)  getSupportFragmentManager().findFragmentByTag("categories");
            searchFragment      = (SearchFilterFragment) getSupportFragmentManager().findFragmentByTag("search");
            bookingsFragment    = (MyBookingsFragment)   getSupportFragmentManager().findFragmentByTag("bookings");
            profileFragment     = (ProfileViewFragment)  getSupportFragmentManager().findFragmentByTag("profile");
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (suppressBottomNavCallback) return true;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showTab(getOrCreateHome(), "home"); return true;
            } else if (id == R.id.nav_categories) {
                showTab(getOrCreateCategories(), "categories"); return true;
            } else if (id == R.id.nav_search) {
                showTab(getOrCreateSearch(), "search"); return true;
            } else if (id == R.id.nav_bookings) {
                showTab(getOrCreateBookings(), "bookings"); return true;
            } else if (id == R.id.nav_profile) {
                showTab(getOrCreateProfile(), "profile"); return true;
            }
            return false;
        });
    }

    // ── Tab helpers ───────────────────────────────────────────────────────────

    private HomeFragment getOrCreateHome() {
        if (homeFragment == null) homeFragment = new HomeFragment();
        return homeFragment;
    }
    private CategoriesFragment getOrCreateCategories() {
        if (categoriesFragment == null) categoriesFragment = new CategoriesFragment();
        return categoriesFragment;
    }
    private SearchFilterFragment getOrCreateSearch() {
        if (searchFragment == null) searchFragment = new SearchFilterFragment();
        return searchFragment;
    }
    private MyBookingsFragment getOrCreateBookings() {
        if (bookingsFragment == null) bookingsFragment = new MyBookingsFragment();
        return bookingsFragment;
    }
    private ProfileViewFragment getOrCreateProfile() {
        if (profileFragment == null) profileFragment = new ProfileViewFragment();
        return profileFragment;
    }

    /**
     * Show the given fragment tab, adding it if not yet added, hiding all others.
     * Uses add+show/hide instead of replace so fragment views are preserved.
     */
    private void showTab(Fragment target, String tag) {
        if (isFinishing()) return;
        androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction tx = fm.beginTransaction();

        // Add if not already in the back stack / manager
        if (!target.isAdded()) {
            tx.add(R.id.fragmentContainer, target, tag);
        }

        // Hide every other added fragment
        for (Fragment f : fm.getFragments()) {
            if (f != target && f.isAdded() && !(f instanceof androidx.fragment.app.DialogFragment)) {
                tx.hide(f);
            }
        }
        tx.show(target);
        tx.commitNowAllowingStateLoss();
    }

    /**
     * Push a detail/sub-screen on top of the current tab.
     * The current tab is hidden. Pressing back will pop the detail and restore the tab.
     */
    public void navigateTo(Fragment destination) {
        if (isFinishing()) return;
        androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();

        // Hide every currently visible tab so the detail renders alone
        androidx.fragment.app.FragmentTransaction tx = fm.beginTransaction();
        for (Fragment f : fm.getFragments()) {
            if (f != null && f.isAdded() && f.isVisible()
                    && !(f instanceof androidx.fragment.app.DialogFragment)) {
                tx.hide(f);
            }
        }
        tx.add(R.id.fragmentContainer, destination)
          .addToBackStack(null)
          .commit();
    }

    private void openFragment(Fragment fragment, int bottomNavItemId) {
        if (isFinishing()) return;

        if (bottomNavItemId == R.id.nav_home) {
            showTab(getOrCreateHome(), "home");
        } else if (bottomNavItemId == R.id.nav_categories) {
            showTab(getOrCreateCategories(), "categories");
        } else if (bottomNavItemId == R.id.nav_search) {
            showTab(getOrCreateSearch(), "search");
        } else if (bottomNavItemId == R.id.nav_bookings) {
            showTab(getOrCreateBookings(), "bookings");
        } else if (bottomNavItemId == R.id.nav_profile) {
            showTab(getOrCreateProfile(), "profile");
        } else {
            // Overlay screens (admin, settings, favorites) — push over current tab
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        if (bottomNavItemId != -1) {
            suppressBottomNavCallback = true;
            bottomNavigation.getMenu().findItem(bottomNavItemId).setChecked(true);
            suppressBottomNavCallback = false;
        }
    }
}
