package com.evernote.android.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.evernote.android.demo.R;
import com.evernote.android.demo.fragment.UserInfoFragment;
import com.evernote.edam.type.User;

/**
 * @author rwondratschek
 */
public class UserInfoActivity extends AppCompatActivity {

    private static final String KEY_USER = "KEY_USER";

    public static Intent createIntent(Context context, User user) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(KEY_USER, user);
        return intent;
    }

    private User mUser;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mUser = (User) getIntent().getSerializableExtra(KEY_USER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tb_text));

        setSupportActionBar(toolbar);

        if (!isTaskRoot()) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, UserInfoFragment.create(mUser))
                    .commit();
        }

        getSupportActionBar().setTitle(mUser.getUsername());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
