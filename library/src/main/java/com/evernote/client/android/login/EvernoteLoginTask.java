package com.evernote.client.android.login;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.evernote.client.android.EvernoteOAuthHelper;

import net.vrallev.android.task.Task;

import java.util.concurrent.CountDownLatch;

/**
 * @author rwondratschek
 */
/*package*/ class EvernoteLoginTask extends Task<Boolean> {

    public static final int REQUEST_AUTH = 858;

    private final CountDownLatch mCountDownLatch;
    private final EvernoteOAuthHelper mOAuthHelper;

    private int mResultCode;
    private Intent mData;

    private final boolean mIsFragment;

    public EvernoteLoginTask(EvernoteOAuthHelper helper, boolean isFragment) {
        mOAuthHelper = helper;
        mCountDownLatch = new CountDownLatch(1);
        mIsFragment = isFragment;
    }

    private EvernoteLoginFragment getFragment() {
        if (!mIsFragment) {
            return null;
        }

        Fragment fragment = findFragmentSupport(EvernoteLoginFragment.TAG);
        if (fragment instanceof EvernoteLoginFragment) {
            return (EvernoteLoginFragment) fragment;
        } else {
            return null;
        }
    }

    @Override
    public Boolean execute() {
        boolean intentFired = startAuthorization();
        if (!intentFired) {
            return false;
        }

        if (!canContinue()) {
            return false;
        }

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            return false;
        }

        return finishAuthorization();
    }

    public void onActivityResult(int resultCode, Intent data) {
        mResultCode = resultCode;
        mData = data;
        mCountDownLatch.countDown();
    }

    private boolean startAuthorization() {
        if (!canContinue()) {
            return false;
        }

        Intent intent = mOAuthHelper.startAuthorization(getActivity());

        if (!canContinue() || intent == null) {
            return false;
        }


        if (mIsFragment) {
            EvernoteLoginFragment fragment = getFragment();
            if (fragment != null) {
                fragment.startActivityForResult(intent, REQUEST_AUTH);
                return true;
            } else {
                return false;
            }

        } else {
            Activity activity = getActivity();
            if (activity != null) {
                activity.startActivityForResult(intent, REQUEST_AUTH);
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean finishAuthorization() {
        return canContinue() && mOAuthHelper.finishAuthorization(getActivity(), mResultCode, mData);
    }

    private boolean canContinue() {
        return !isCancelled() && getActivity() != null;
    }
}
