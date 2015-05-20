package com.evernote.android.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.evernote.android.demo.R;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;

/**
 * @author rwondratschek
 */
public class LoginActivity extends AppCompatActivity implements EvernoteLoginFragment.ResultCallback {

    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
    }

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tb_text));

        setSupportActionBar(toolbar);

        mButton = (Button) findViewById(R.id.button_login);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EvernoteSession.getInstance().authenticate(LoginActivity.this);
                mButton.setEnabled(false);
            }
        });

    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            finish();
        } else {
            mButton.setEnabled(true);
        }
    }
}
