package edu.duke.ece651.riskclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import edu.duke.ece651.riskclient.Player;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.ui.AboutFragment;
import edu.duke.ece651.riskclient.ui.HomeFragment;
import edu.duke.ece651.riskclient.ui.PasswordFragment;

public class MainActivity extends AppCompatActivity {

    /**
     *  constant value
     */
    public final static String LOGIN_ID = "edu.duke.ece651.risk.LOGIN_ID";
    public final static String LOGIN_NAME = "edu.duke.ece651.risk.LOGIN_NAME";

    /**
     * UI variable
     */
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    /**
     * variable
     */
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();
        int userID = intent.getIntExtra(LOGIN_ID, 1);
        String userName = intent.getStringExtra(LOGIN_NAME);
        player = new Player(userID, userName);

        // setup toolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Fancy Risk");

        // load default fragment
        if (savedInstanceState == null){
            replaceFragment(HomeFragment.newInstance(player));
        }

        setUpDrawer(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setUpDrawer(Toolbar toolbar){
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.post(() -> toggle.syncState());
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nv_main);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            Fragment fragment;
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    fragment = HomeFragment.newInstance(player);
                    break;
                case R.id.nav_update:
                    fragment = PasswordFragment.newInstance(player);
                    break;
                case R.id.nav_about:
                    fragment = AboutFragment.newInstance();
                    break;
                default:
                    fragment = HomeFragment.newInstance(player);
            }
            replaceFragment(fragment);
            menuItem.setChecked(true);
            // set the ToolBar title
            setTitle(menuItem.getTitle());

            drawer.closeDrawers();
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_main, fragment)
                .commit();
    }

}
