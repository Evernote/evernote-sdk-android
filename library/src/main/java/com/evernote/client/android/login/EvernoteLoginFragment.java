package com.evernote.client.android.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.evernote.androidsdk.R;
import com.evernote.client.android.EvernoteOAuthHelper;
import com.evernote.client.android.EvernoteSession;

import net.vrallev.android.task.TaskExecutor;
import net.vrallev.android.task.TaskResult;

/**
 * Used if you call {@link EvernoteSession#authenticate(FragmentActivity)}. This class is the
 * recommended authentication process.
 *
 * <p/>
 *
 * You can either extend this class and override {@link EvernoteLoginFragment#onLoginFinished(boolean)} method
 * to get notified about the authentication result or the parent {@link FragmentActivity} can implement the
 * {@link ResultCallback} interface to receive the result.
 *
 * @author rwondratschek
 */
@SuppressWarnings("UnusedDeclaration")
public class EvernoteLoginFragment extends DialogFragment {

    public static final String TAG = "EvernoteDialogFragment";

    private static final String ARG_CONSUMER_KEY = "consumerKey";
    private static final String ARG_CONSUMER_SECRET = "consumerSecret";
    private static final String ARG_SUPPORT_APP_LINKED_NOTEBOOKS = "supportAppLinkedNotebooks";

    private static final String TASK_KEY = "taskKey";

    public static EvernoteLoginFragment create(String consumerKey, String consumerSecret, boolean supportAppLinkedNotebooks) {
        return create(EvernoteLoginFragment.class, consumerKey, consumerSecret, supportAppLinkedNotebooks);
    }

    public static <T extends EvernoteLoginFragment> T create(Class<T> subClass, String consumerKey, String consumerSecret, boolean supportAppLinkedNotebooks) {
        T fragment;
        try {
            fragment = subClass.newInstance();
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }

        Bundle args = new Bundle();
        args.putString(ARG_CONSUMER_KEY, consumerKey);
        args.putString(ARG_CONSUMER_SECRET, consumerSecret);
        args.putBoolean(ARG_SUPPORT_APP_LINKED_NOTEBOOKS, supportAppLinkedNotebooks);
        fragment.setArguments(args);

        return fragment;
    }

    private int mTaskKey = -1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            EvernoteOAuthHelper helper = new EvernoteOAuthHelper(EvernoteSession.getInstance(), args.getString(ARG_CONSUMER_KEY),
                args.getString(ARG_CONSUMER_SECRET), args.getBoolean(ARG_SUPPORT_APP_LINKED_NOTEBOOKS, true));

            mTaskKey = TaskExecutor.getInstance().execute(new EvernoteLoginTask(helper, true), this);

        } else {
            mTaskKey = savedInstanceState.getInt(TASK_KEY, -1);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EvernoteLoginTask task = TaskExecutor.getInstance().getTask(mTaskKey);
                if (task != null) {
                    task.cancel();
                }
            }
        };

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.esdk__loading));
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), onClickListener);
        progressDialog.setCancelable(isCancelable());

        return progressDialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TASK_KEY, mTaskKey);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        dismiss();

        FragmentActivity activity = getActivity();
        if (activity instanceof ResultCallback) {
            ((ResultCallback) activity).onLoginFinished(result);
        } else {
            onLoginFinished(result);
        }
    }

    protected void onLoginFinished(boolean success) {
        // override me
    }

    public interface ResultCallback {
        public void onLoginFinished(boolean successful);
    }
}
