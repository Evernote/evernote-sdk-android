package com.evernote.android.demo.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.evernote.android.demo.R;
import com.evernote.android.demo.fragment.note.CreateNoteDialogFragment;
import com.evernote.android.demo.fragment.note.NoteContainerFragment;
import com.evernote.android.demo.fragment.notebook.NotebookTabsFragment;
import com.evernote.android.demo.task.GetUserTask;
import com.evernote.android.demo.util.Util;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.User;

import net.vrallev.android.task.TaskResult;

/**
 * @author rwondratschek
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_SELECTED_NAV_ITEM = "KEY_SELECTED_NAV_ITEM";
    private static final String KEY_USER = "KEY_USER";

    private DrawerLayout mDrawerLayout;
    private TextView mTextViewUserName;

    private int mSelectedNavItem;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            // LoginChecker will call finish
            return;
        }

        setContentView(R.layout.activity_main);

        Resources resources = getResources();

        mSelectedNavItem = R.id.nav_item_notes;
        if (savedInstanceState != null) {
            mSelectedNavItem = savedInstanceState.getInt(KEY_SELECTED_NAV_ITEM, mSelectedNavItem);
            mUser = (User) savedInstanceState.getSerializable(KEY_USER);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(resources.getColor(R.color.tb_text));

        setSupportActionBar(toolbar);

        if (!isTaskRoot()) {
            //noinspection ConstantConditions
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);

        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                onNavDrawerItemClick(menuItem.getItemId());
                return true;
            }
        });
        navigationView.getMenu().findItem(mSelectedNavItem).setChecked(true);

        mTextViewUserName = (TextView) findViewById(R.id.textView_user_name);
        findViewById(R.id.nav_drawer_header_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser != null) {
                    startActivity(UserInfoActivity.createIntent(MainActivity.this, mUser));
                }
            }
        });

        if (savedInstanceState == null) {
            showItem(mSelectedNavItem);
            new GetUserTask().start(this);

        } else if (mUser != null) {
            onGetUser(mUser);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_NAV_ITEM, mSelectedNavItem);
        outState.putSerializable(KEY_USER, mUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Util.logout(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CreateNoteDialogFragment.REQ_SELECT_IMAGE:
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (fragment != null) {
                    // somehow the event doesn't get dispatched correctly
                    fragment.onActivityResult(requestCode, resultCode, data);
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @TaskResult
    public void onGetUser(User user) {
        mUser = user;
        if (user != null) {
            mTextViewUserName.setText(user.getUsername());
        }
    }

    private void onNavDrawerItemClick(int navItemId) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mSelectedNavItem = navItemId;

        showItem(navItemId);
    }

    private void showItem(int navItemId) {
        switch (navItemId) {
            case R.id.nav_item_notes:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, NoteContainerFragment.create())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;

            case R.id.nav_item_notebooks:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new NotebookTabsFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;

            default:
                throw new IllegalStateException("not implemented");
        }
    }
}
