package com.yeyolotto.www.yeyo;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;

    final Fragment homeFragment = new HomeFragment();
    final Fragment toolsFragment = new ToolsFragment();
    final Fragment tirosFragment = new TirosFragment();
    final Fragment playsFragment = new PlaysFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNav = findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

        fragmentManager.beginTransaction().add(R.id.container, playsFragment,"4")
                .hide(playsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, tirosFragment,"3")
                .hide(tirosFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, toolsFragment,"2")
                .hide(toolsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, homeFragment,"1")
                .commit();
    }

    private void selectFragment(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                break;
            case R.id.navigation_tools:
                fragmentManager.beginTransaction().hide(activeFragment).show(toolsFragment).commit();
                activeFragment = toolsFragment;
                break;
            case R.id.navigation_tiros:
                fragmentManager.beginTransaction().hide(activeFragment).show(tirosFragment).commit();
                activeFragment = tirosFragment;
                break;
            case R.id.navigation_plays:
                fragmentManager.beginTransaction().hide(activeFragment).show(playsFragment).commit();
                activeFragment = playsFragment;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(fragmentManager != null && activeFragment != null
                && homeFragment != null && activeFragment != homeFragment) {
            fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
            activeFragment = homeFragment;
        }else
            super.onBackPressed();

    }
}
