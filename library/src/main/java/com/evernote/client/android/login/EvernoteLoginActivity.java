package com.evernote.client.android.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.evernote.androidsdk.R;
import com.evernote.client.android.EvernoteOAuthHelper;
import com.evernote.client.android.EvernoteSession;

import net.vrallev.android.task.TaskExecutor;
import net.vrallev.android.task.TaskResult;

/**
 * Used if you call {@link EvernoteSession#authenticateWithApp(Activity)}. You shouldn't need interact
 * with this class directly.
 *
 * @author rwondratschek
 */
@SuppressWarnings("UnusedDeclaration")
public class EvernoteLoginActivity extends Activity {

    private static final String EXTRA_CONSUMER_KEY = "EXTRA_CONSUMER_KEY";
    private static final String EXTRA_CONSUMER_SECRET = "EXTRA_CONSUMER_SECRET";
    private static final String EXTRA_SUPPORT_APP_LINKED_NOTEBOOKS = "EXTRA_SUPPORT_APP_LINKED_NOTEBOOKS";

    private static final String TASK_KEY = "TASK_KEY";

    public static Intent createIntent(Context context, String consumerKey, String consumerSecret, boolean supportAppLinkedNotebooks) {
        Intent intent = new Intent(context, EvernoteLoginActivity.class);
        intent.putExtra(EXTRA_CONSUMER_KEY, consumerKey);
        intent.putExtra(EXTRA_CONSUMER_SECRET, consumerSecret);
        intent.putExtra(EXTRA_SUPPORT_APP_LINKED_NOTEBOOKS, supportAppLinkedNotebooks);
        return intent;
    }

    private int mTaskKey;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle args = getIntent().getExtras();
            EvernoteOAuthHelper helper = new EvernoteOAuthHelper(EvernoteSession.getInstance(), args.getString(EXTRA_CONSUMER_KEY),
                args.getString(EXTRA_CONSUMER_SECRET), args.getBoolean(EXTRA_SUPPORT_APP_LINKED_NOTEBOOKS, true));

            mTaskKey = TaskExecutor.getInstance().execute(new EvernoteLoginTask(helper, false), this);

        } else {
            mTaskKey = savedInstanceState.getInt(TASK_KEY, -1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            showDialog();
        }
    }

    @Override
    protected void onStop() {
        mProgressDialog.dismiss();
        mProgressDialog = null;
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TASK_KEY, mTaskKey);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EvernoteLoginTask.REQUEST_AUTH) {
            EvernoteLoginTask task = TaskExecutor.getInstance().getTask(mTaskKey);
            if (task != null) {
                task.onActivityResult(resultCode, data);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @TaskResult
    public final void onResult(Boolean result) {
        setResult(result ? RESULT_OK : RESULT_CANCELED);
        finish();
    }

    protected void showDialog() {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EvernoteLoginTask task = TaskExecutor.getInstance().getTask(mTaskKey);
                if (task != null) {
                    task.cancel();
                }
            }
        };

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.esdk__loading));
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), onClickListener);
        mProgressDialog.setCancelable(false);

        mProgressDialog.show();
    }
}
