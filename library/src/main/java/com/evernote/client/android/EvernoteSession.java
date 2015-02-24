/*
 * Copyright 2012 Evernote Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.evernote.client.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.evernote.client.android.helper.Cat;
import com.evernote.client.android.helper.EvernotePreconditions;
import com.evernote.client.android.helper.EvernoteSdkUtil;
import com.evernote.client.oauth.EvernoteAuthToken;

import java.io.File;
import java.util.Locale;

/**
 * Represents a session with the Evernote web service API. Used to authenticate
 * to the service via OAuth and obtain NoteStore.Client objects, which are used
 * to make authenticated API calls.
 *
 * To use EvernoteSession, first initialize the EvernoteSession singleton and
 * initiate authentication at an appropriate time:
 * <pre>
 *   EvernoteSession session = EvernoteSession.init(...);
 *   if (!session.isLoggedIn()) {
 *     session.authenticate(...);
 *   }
 * </pre>
 *
 * When authentication completes, you will want to trap the result in onActivityResult
 * to see if it was successful:
 * <pre>
 *   public void onActivityResult(int requestCode, int resultCode, Intent data) {
 *     super.onActivityResult(requestCode, resultCode, data);
 *       switch(requestCode) {
 *       case EvernoteSession.REQUEST_CODE_OAUTH:
 *         if (resultCode == Activity.RESULT_OK) {
 *           // OAuth login was successful, do the appropriate thing for your app
 *         }
 *         break;
 *     }
 *   }
 * </pre>
 *
 * Later, you can make any Evernote API calls that you need by obtaining a
 * NoteStore.Client from the session and using the session's auth token:
 * <pre>
 *   NoteStore.client noteStore = session.createNoteStoreClient();
 *   Notebook notebook = noteStore.getDefaultNotebook(session.getAuthToken());
 * </pre>
 *
 * class created by @tylersmithnet
 */
@SuppressWarnings("UnusedDeclaration")
public final class EvernoteSession {

    // Standard hostnames for bootstrap detection
    public static final String HOST_SANDBOX = "https://sandbox.evernote.com";
    public static final String HOST_PRODUCTION = "https://www.evernote.com";
    public static final String HOST_CHINA = "https://app.yinxiang.com";

    public static final int REQUEST_CODE_OAUTH = 14390;

    private static final Cat CAT = new Cat("EvernoteSession");

    private static EvernoteSession sInstance = null;

    public static EvernoteSession getInstance() {
        return sInstance;
    }

    /**
     * Use to acquire a singleton instance of the EvernoteSession for authentication.
     * If the singleton has already been initialized, the existing instance will
     * be returned (and the parameters passed to this method will be ignored).
     *
     * @param ctx                       Application Context or activity
     * @param consumerKey               The consumer key portion of your application's API key.
     * @param consumerSecret            The consumer secret portion of your application's API key.
     * @param evernoteService           The enum of the Evernote service instance that you wish
     *                                  to use. Development and testing is typically performed against {@link EvernoteService#SANDBOX}.
     *                                  The production Evernote service is {@link EvernoteService#HOST_PRODUCTION}
     * @param supportAppLinkedNotebooks true if you want to allow linked notebooks for
     *                                  applications which can only access a single notebook.
     * @return The EvernoteSession singleton instance.
     * @throws IllegalArgumentException
     * @deprecated Use the {@link Builder} instead and call {@link EvernoteSession#asSingleton()}.
     */
    @Deprecated
    public static EvernoteSession getInstance(Context ctx,
                                              String consumerKey,
                                              String consumerSecret,
                                              EvernoteService evernoteService,
                                              boolean supportAppLinkedNotebooks) {

        if (sInstance == null) {
            new Builder(ctx)
                .setEvernoteService(evernoteService)
                .setSupportAppLinkedNotebooks(supportAppLinkedNotebooks)
                .build(consumerKey, consumerSecret)
                .asSingleton();
        }

        return sInstance;
    }

    private String mConsumerKey;
    private String mConsumerSecret;
    private EvernoteService mEvernoteService;
    private BootstrapManager mBootstrapManager;
    private ClientFactory mClientFactory;
    private AuthenticationResult mAuthenticationResult;
    private boolean mSupportAppLinkedNotebooks;

    private EvernoteSession() {
        // do nothing, builder sets up everything
    }

    /**
     * @return the Bootstrap object to check for server host urls
     */
    protected BootstrapManager getBootstrapSession() {
        return mBootstrapManager;
    }

    /**
     * Use this to create {@link AsyncNoteStoreClient} and {@link AsyncUserStoreClient}.
     */
    public ClientFactory getClientFactory() {
        return mClientFactory;
    }


    /**
     * Get the authentication token that is used to make API calls
     * though a NoteStore.Client.
     *
     * @return the authentication token, or null if {@link #isLoggedIn()}
     * is false.
     */
    public String getAuthToken() {
        if (mAuthenticationResult != null) {
            return mAuthenticationResult.getAuthToken();
        } else {
            return null;
        }
    }

    /**
     * Get the authentication information returned by a successful
     * OAuth authentication to the Evernote web service.
     */
    public AuthenticationResult getAuthenticationResult() {
        return mAuthenticationResult;
    }

    /**
     * Start the OAuth authentication process.
     * <p/>
     * TODO do we need to do anything special here if you're already logged in?
     */
    public void authenticate(Context ctx) {
        // Create an activity that will be used for authentication
        Intent intent = new Intent(ctx, EvernoteOAuthActivity.class);
        intent.putExtra(EvernoteOAuthActivity.EXTRA_EVERNOTE_SERVICE, (Parcelable) mEvernoteService);
        intent.putExtra(EvernoteOAuthActivity.EXTRA_CONSUMER_KEY, mConsumerKey);
        intent.putExtra(EvernoteOAuthActivity.EXTRA_CONSUMER_SECRET, mConsumerSecret);
        intent.putExtra(EvernoteOAuthActivity.EXTRA_SUPPORT_APP_LINKED_NOTEBOOKS, mSupportAppLinkedNotebooks);

        if (ctx instanceof Activity) {
            //If this is being called from an activity, an activity can register for the result code
            ((Activity) ctx).startActivityForResult(intent, REQUEST_CODE_OAUTH);
        } else {
            //If this is being called from a service, the refresh will be handled manually
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        }
    }

    /**
     * Called upon completion of the OAuth process to save resulting authentication
     * information into the application's SharedPreferences, allowing it to be reused
     * later.
     *
     * TODO: move this code
     *
     * @param ctx          Application Context or activity
     * @param authToken    The authentication information returned at the end of a
     *                     successful OAuth authentication.
     * @param evernoteHost the URL of the Evernote Web API to connect to, provided by the bootstrap results
     */
    protected boolean persistAuthenticationToken(Context ctx, EvernoteAuthToken authToken, String evernoteHost) {
        if (ctx == null || authToken == null) {
            return false;
        }
        synchronized (this) {
            mAuthenticationResult =
                new AuthenticationResult(
                    authToken.getToken(),
                    authToken.getNoteStoreUrl(),
                    authToken.getWebApiUrlPrefix(),
                    evernoteHost,
                    authToken.getUserId(),
                    authToken.isAppLinkedNotebook());

            mAuthenticationResult.persist(SessionPreferences.getPreferences(ctx));
        }

        return true;
    }

    /**
     * Check whether the session has valid authentication information
     * that will allow successful API calls to be made.
     */
    public boolean isLoggedIn() {
        synchronized (this) {
            return mAuthenticationResult != null;
        }
    }

    public boolean isAppLinkedNotebook() {
        return mAuthenticationResult.isAppLinkedNotebook();
    }

    public EvernoteSession asSingleton() {
        sInstance = this;
        return this;
    }

    /**
     * Clear all stored authentication information.
     */
    public void logOut(Context ctx) throws InvalidAuthenticationException {
        if (!isLoggedIn()) {
            throw new InvalidAuthenticationException("Must not call when already logged out");
        }
        synchronized (this) {
            mAuthenticationResult.clear(SessionPreferences.getPreferences(ctx));
            mAuthenticationResult = null;
        }

        // TODO The cookie jar is application scope, so we should only be removing evernote.com cookies.
        EvernoteSdkUtil.removeAllCookies(ctx);
    }

    /**
     * Construct a user-agent string based on the running application and
     * the device and operating system information. This information is
     * included in HTTP requests made to the Evernote service and assists
     * in measuring traffic and diagnosing problems.
     */
    private static String generateUserAgentString(Context ctx) {
        String packageName = null;
        int packageVersion = 0;
        try {
            packageName = ctx.getPackageName();
            packageVersion = ctx.getPackageManager().getPackageInfo(packageName, 0).versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            CAT.e(e.getMessage());
        }

        String userAgent = packageName + " Android/" + packageVersion;

        Locale locale = java.util.Locale.getDefault();
        if (locale == null) {
            userAgent += " (" + Locale.US + ");";
        } else {
            userAgent += " (" + locale.toString() + "); ";
        }
        userAgent += "Android/" + Build.VERSION.RELEASE + "; ";
        userAgent += Build.MODEL + "/" + Build.VERSION.SDK_INT + ";";
        return userAgent;
    }

    /**
     * Restore an AuthenticationResult from shared preferences.
     * @return The restored AuthenticationResult, or null if the preferences
     * did not contain the required information.
     */
    private static AuthenticationResult getAuthenticationResultFromPref(SharedPreferences prefs) {
        AuthenticationResult authResult = new AuthenticationResult(prefs);

        if (TextUtils.isEmpty(authResult.getEvernoteHost()) || TextUtils.isEmpty(authResult.getAuthToken()) || TextUtils.isEmpty(authResult.getNoteStoreUrl())
            || TextUtils.isEmpty(authResult.getWebApiUrlPrefix()) || TextUtils.isEmpty(authResult.getEvernoteHost())) {
            return null;
        }

        return authResult;
    }

    /**
     * Evernote Service to use with the bootstrap profile detection.
     * Sandbox will return profiles referencing sandbox.evernote.com
     * Production will return evernote.com and app.yinxiang.com
     */
    public static enum EvernoteService implements Parcelable {
        SANDBOX,
        PRODUCTION;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeInt(ordinal());
        }

        public static final Creator<EvernoteService> CREATOR = new Creator<EvernoteService>() {
            @Override
            public EvernoteService createFromParcel(final Parcel source) {
                return EvernoteService.values()[source.readInt()];
            }

            @Override
            public EvernoteService[] newArray(final int size) {
                return new EvernoteService[size];
            }
        };
    }

    public static class Builder {

        private final Context mContext;

        private EvernoteService mEvernoteService;
        private boolean mSupportAppLinkedNotebooks;
        private String mUserAgent;
        private File mMessageCacheDir;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Null not allowed");
            }

            mContext = context;
            mSupportAppLinkedNotebooks = true;
            mEvernoteService = EvernoteService.SANDBOX;
            mUserAgent = generateUserAgentString(context);
            mMessageCacheDir = mContext.getFilesDir();
        }

        public Builder setEvernoteService(EvernoteService evernoteService) {
            if (evernoteService == null) {
                throw new IllegalArgumentException("Null not allowed");
            }
            mEvernoteService = evernoteService;
            return this;
        }

        public Builder setSupportAppLinkedNotebooks(boolean supportAppLinkedNotebooks) {
            mSupportAppLinkedNotebooks = supportAppLinkedNotebooks;
            return this;
        }

        /*package*/ Builder setUserAgent(String userAgent) {
            // maybe set this to public
            mUserAgent = userAgent;
            return this;
        }

        /*package*/ Builder setMessageCacheDir(File messageCacheDir) {
            // maybe set this to public
            mMessageCacheDir = messageCacheDir;
            return this;
        }

        public EvernoteSession build(String consumerKey, String consumerSecret) {
            EvernoteSession evernoteSession = new EvernoteSession();
            evernoteSession.mConsumerKey = EvernotePreconditions.checkNotEmpty(consumerKey);
            evernoteSession.mConsumerSecret = EvernotePreconditions.checkNotEmpty(consumerSecret);
            evernoteSession.mAuthenticationResult = getAuthenticationResultFromPref(SessionPreferences.getPreferences(mContext));

            return build(evernoteSession);
        }

        public EvernoteSession buildForSingleUser(String developerToken, String noteStoreUrl) {
            EvernoteSession evernoteSession = new EvernoteSession();
            evernoteSession.mAuthenticationResult = new AuthenticationResult(EvernotePreconditions.checkNotEmpty(developerToken),
                EvernotePreconditions.checkNotEmpty(noteStoreUrl), mSupportAppLinkedNotebooks);

            return build(evernoteSession);
        }

        private EvernoteSession build(EvernoteSession session) {
            session.mEvernoteService = mEvernoteService;
            session.mSupportAppLinkedNotebooks = mSupportAppLinkedNotebooks;
            session.mClientFactory = new ClientFactory(mUserAgent, mMessageCacheDir);
            session.mBootstrapManager = new BootstrapManager(session.mEvernoteService, session.mClientFactory);
            return session;
        }
    }
}
