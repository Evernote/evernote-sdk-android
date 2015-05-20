package com.evernote.android.demo.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evernote.android.demo.R;
import com.evernote.android.demo.fragment.note.CreateNoteDialogFragment;
import com.evernote.android.demo.fragment.note.NoteContainerFragment;
import com.evernote.android.demo.fragment.notebook.NotebookTabsFragment;
import com.evernote.android.demo.task.GetUserTask;
import com.evernote.android.demo.util.Util;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.helper.Cat;
import com.evernote.edam.type.User;

import net.vrallev.android.task.TaskResult;

/**
 * @author rwondratschek
 */
public class MainActivity extends AppCompatActivity {

    private static final Cat CAT = new Cat("MainActivity");

    private static final String KEY_CURRENT_POSITION = "KEY_CURRENT_POSITION";
    private static final String KEY_USER = "KEY_USER";

    private DrawerLayout mDrawerLayout;
    private MyAdapter mAdapter;

    private int mCurrentPosition;

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

        mCurrentPosition = 1;
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, mCurrentPosition);
            mUser = (User) savedInstanceState.getSerializable(KEY_USER);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(resources.getColor(R.color.tb_text));

        setSupportActionBar(toolbar);

        if (!isTaskRoot()) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        mAdapter = new MyAdapter(resources.getStringArray(R.array.nav_items), mUser != null ? mUser.getUsername() : null, mCurrentPosition);
        recyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(new MyOnItemTouchListener());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);

        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (savedInstanceState == null) {
            showItem(mCurrentPosition);
            new GetUserTask().start(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, mCurrentPosition);
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
            mAdapter.setUsername(user.getUsername());
        }
    }

    private void onNavDrawerItemClick(int position) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mCurrentPosition = position;

        showItem(position);
    }

    private void showItem(int position) {
        switch (position) {
            case 0:
                if (mUser != null) {
                    startActivity(UserInfoActivity.createIntent(this, mUser));
                }
                break;

            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, NoteContainerFragment.create())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;

            case 2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new NotebookTabsFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;

            default:
                throw new IllegalStateException("not implemented");
        }
    }

    private class MyOnItemTouchListener implements RecyclerView.OnItemTouchListener {

        private final GestureDetector mGestureDetector;

        public MyOnItemTouchListener() {
            mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && mGestureDetector.onTouchEvent(e)) {
                int position = rv.getChildAdapterPosition(child);
                if (!mAdapter.isPositionHeader(position)) {
                    rv.getAdapter().notifyItemChanged(mAdapter.mSelection);
                    mAdapter.mSelection = position;
                    rv.getAdapter().notifyItemChanged(position);
                }

                onNavDrawerItemClick(position);
                return true;
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            // no op
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        private final String mNavTitles[];
        private String mUsername;

        private int mSelection;

        public MyAdapter(String titles[], String username, int initialPosition) {
            mNavTitles = titles;
            mUsername = username;
            mSelection = initialPosition;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_HEADER:
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_header, parent, false);
                    return new ViewHolderHeader(view);

                case TYPE_ITEM:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_item, parent, false);
                    return new ViewHolderItem(view);

                default:
                    throw new IllegalStateException("not implemented");
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean selected = position == mSelection;
            holder.itemView.setSelected(selected);

            if (isPositionHeader(position)) {
                ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
                viewHolderHeader.mTextViewUserName.setText(mUsername);

            } else {
                ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
                viewHolderItem.mTextViewTitle.setText(mNavTitles[position - 1]);
            }
        }

        @Override
        public int getItemCount() {
            return mNavTitles.length + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return isPositionHeader(position) ? TYPE_HEADER : TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        public void setUsername(String username) {
            mUsername = username;
            notifyItemChanged(0);
        }

        private static class ViewHolderHeader extends RecyclerView.ViewHolder {

            private final TextView mTextViewUserName;

            public ViewHolderHeader(View itemView) {
                super(itemView);
                mTextViewUserName = (TextView) itemView.findViewById(R.id.textView_user_name);
            }
        }

        private static class ViewHolderItem extends RecyclerView.ViewHolder {
            private final TextView mTextViewTitle;

            public ViewHolderItem(View itemView) {
                super(itemView);
                mTextViewTitle = (TextView) itemView.findViewById(R.id.textView_title);
            }
        }
    }
}
