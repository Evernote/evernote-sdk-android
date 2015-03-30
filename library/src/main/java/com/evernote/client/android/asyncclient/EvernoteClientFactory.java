package com.evernote.client.android.asyncclient;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.helper.EvernotePreconditions;
import com.evernote.client.conn.mobile.ByteStore;
import com.evernote.client.conn.mobile.DiskBackedByteStore;
import com.evernote.client.conn.mobile.TAndroidTransport;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.User;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A factory to create async wrappers around a {@link NoteStore.Client}. Use the corresponding
 * {@link EvernoteClientFactory.Builder} to create an instance.
 *
 * <br>
 * <br>
 *
 * Try to reuse a created instances. A factory caches created {@link NoteStore.Client}s, their wrappers
 * and internal helper objects like the http client. The easiest way to get access to a factory is to
 * call {@link EvernoteSession#getEvernoteClientFactory()}.
 *
 * @author rwondratschek
 */
@SuppressWarnings("unused")
public class EvernoteClientFactory {

    protected final EvernoteSession mEvernoteSession;
    protected final OkHttpClient mHttpClient;
    protected final ByteStore mByteStore;
    protected final Map<String, String> mHeaders;
    protected final ExecutorService mExecutorService;

    private EvernoteUserStoreClient mUserStoreClient;
    private final Map<String, EvernoteNoteStoreClient> mNoteStoreClients;
    private final Map<String, EvernoteLinkedNotebookHelper> mLinkedNotebookHelpers;
    private EvernoteBusinessNotebookHelper mBusinessNotebookHelper;

    private EvernoteHtmlHelper mHtmlHelperDefault;
    private EvernoteHtmlHelper mHtmlHelperBusiness;

    private final EvernoteAsyncClient mCreateHelperClient;

    protected EvernoteClientFactory(EvernoteSession session, OkHttpClient httpClient, ByteStore byteStore, Map<String, String> headers, ExecutorService executorService) {
        mEvernoteSession = EvernotePreconditions.checkNotNull(session);
        mHttpClient = EvernotePreconditions.checkNotNull(httpClient);
        mByteStore = EvernotePreconditions.checkNotNull(byteStore);
        mHeaders = headers;
        mExecutorService = EvernotePreconditions.checkNotNull(executorService);

        mNoteStoreClients = new HashMap<>();
        mLinkedNotebookHelpers = new HashMap<>();

        mCreateHelperClient = new EvernoteAsyncClient(mExecutorService) {};
    }

    /**
     * @return An async wrapper for {@link UserStore.Client}.
     * @see UserStore
     * @see UserStore.Client
     */
    public synchronized EvernoteUserStoreClient getUserStoreClient() {
        if (mUserStoreClient == null) {
            mUserStoreClient = createUserStoreClient();
        }
        return mUserStoreClient;
    }

    protected EvernoteUserStoreClient createUserStoreClient() {
        String url = new Uri.Builder()
                .scheme("https")
                .authority(mEvernoteSession.getAuthenticationResult().getEvernoteHost())
                .path("/edam/user")
                .build()
                .toString();

        UserStore.Client client = new UserStore.Client(createBinaryProtocol(url));
        //noinspection ConstantConditions
        return new EvernoteUserStoreClient(client, mEvernoteSession.getAuthToken(), mExecutorService);
    }

    /**
     * @return The default client for this session. It references the user's private note store.
     * @see EvernoteClientFactory#getNoteStoreClient(String, String)
     * @see com.evernote.client.android.AuthenticationResult#getNoteStoreUrl()
     */
    public synchronized EvernoteNoteStoreClient getNoteStoreClient() {
        return getNoteStoreClient(mEvernoteSession.getAuthenticationResult().getNoteStoreUrl(), EvernotePreconditions.checkNotEmpty(mEvernoteSession.getAuthToken()));
    }

    /**
     *
     *
     * @param url The note store URL.
     * @param authToken The authentication token to get access to this note store.
     * @return An async wrapper for {@link NoteStore.Client} with this specific url and authentication
     * token combination.
     *
     * @see NoteStore
     * @see NoteStore.Client
     */
    public synchronized EvernoteNoteStoreClient getNoteStoreClient(@NonNull String url, @NonNull String authToken) {
        String key = createKey(url, authToken);
        EvernoteNoteStoreClient client = mNoteStoreClients.get(key);
        if (client == null) {
            client = createEvernoteNoteStoreClient(url, authToken);
            mNoteStoreClients.put(key, client);
        }

        return client;
    }

    /**
     * Returns an async wrapper providing several helper methods for this {@link LinkedNotebook}. With
     * {@link EvernoteLinkedNotebookHelper#getClient()} you can get access to the underlying {@link EvernoteNoteStoreClient},
     * which references the {@link LinkedNotebook}'s note store URL.
     *
     * @param linkedNotebook The referenced {@link LinkedNotebook}. Its GUID and share key must not be
     *                       {@code null}.
     * @return An async wrapper providing several helper methods.
     * @throws EDAMUserException
     * @throws EDAMSystemException
     * @throws EDAMNotFoundException
     * @throws TException
     */
    public synchronized EvernoteLinkedNotebookHelper getLinkedNotebookHelper(@NonNull LinkedNotebook linkedNotebook) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        String key = linkedNotebook.getGuid();
        EvernoteLinkedNotebookHelper notebookHelper = mLinkedNotebookHelpers.get(key);
        if (notebookHelper == null) {
            notebookHelper = createLinkedNotebookHelper(linkedNotebook);
            mLinkedNotebookHelpers.put(key, notebookHelper);
        }

        return notebookHelper;
    }

    /**
     * @see #getLinkedNotebookHelper(LinkedNotebook)
     */
    public Future<EvernoteLinkedNotebookHelper> getLinkedNotebookHelperAsync(@NonNull final LinkedNotebook linkedNotebook, @Nullable EvernoteCallback<EvernoteLinkedNotebookHelper> callback) {
        return mCreateHelperClient.submitTask(new Callable<EvernoteLinkedNotebookHelper>() {
            @Override
            public EvernoteLinkedNotebookHelper call() throws Exception {
                return getLinkedNotebookHelper(linkedNotebook);
            }
        }, callback);
    }

    protected EvernoteLinkedNotebookHelper createLinkedNotebookHelper(@NonNull LinkedNotebook linkedNotebook) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        String url = linkedNotebook.getNoteStoreUrl();

        EvernoteNoteStoreClient client = getNoteStoreClient(url, EvernotePreconditions.checkNotEmpty(mEvernoteSession.getAuthToken()));
        AuthenticationResult authenticationResult = client.authenticateToSharedNotebook(linkedNotebook.getShareKey());

        client = getNoteStoreClient(url, authenticationResult.getAuthenticationToken());

        return new EvernoteLinkedNotebookHelper(client, linkedNotebook, mExecutorService);
    }

    /**
     * Returns an async wrapper providing several helper methods for business notebooks. With
     * {@link EvernoteBusinessNotebookHelper#getClient()} you can get access to the underlying {@link EvernoteNoteStoreClient},
     * which references the business note store URL.
     *
     * @return An async wrapper providing several helper methods.
     * @throws EDAMUserException
     * @throws EDAMSystemException
     * @throws TException
     */
    public synchronized EvernoteBusinessNotebookHelper getBusinessNotebookHelper() throws TException, EDAMUserException, EDAMSystemException {
        if (mBusinessNotebookHelper == null) {
            mBusinessNotebookHelper = createBusinessNotebookHelper();
        }
        return mBusinessNotebookHelper;
    }

    /**
     * @see #getBusinessNotebookHelper()
     */
    public Future<EvernoteBusinessNotebookHelper> getBusinessNotebookHelperAsync(@Nullable EvernoteCallback<EvernoteBusinessNotebookHelper> callback) {
        return mCreateHelperClient.submitTask(new Callable<EvernoteBusinessNotebookHelper>() {
            @Override
            public EvernoteBusinessNotebookHelper call() throws Exception {
                return getBusinessNotebookHelper();
            }
        }, callback);
    }

    protected EvernoteBusinessNotebookHelper createBusinessNotebookHelper() throws TException, EDAMUserException, EDAMSystemException {
        com.evernote.client.android.AuthenticationResult authResult = authenticateToBusiness();

        EvernoteNoteStoreClient client = getNoteStoreClient(authResult.getBusinessNoteStoreUrl(), authResult.getBusinessAuthToken());

        User businessUser = authResult.getBusinessUser();
        return new EvernoteBusinessNotebookHelper(client, mExecutorService, businessUser.getName(), businessUser.getShardId());
    }

    /**
     * Use this method, if you want to download a note as HTML from a private or linked note store.
     * For business notes use {@link #getHtmlHelperBusiness()} instead.
     *
     * @return An async wrapper to load a note as HTML from the Evernote service.
     */
    public synchronized EvernoteHtmlHelper getHtmlHelperDefault() {
        if (mHtmlHelperDefault == null) {
            mHtmlHelperDefault = createHtmlHelper(mEvernoteSession.getAuthToken());
        }
        return mHtmlHelperDefault;
    }

    /**
     * Use this method, if you want to download a business note as HTML.
     *
     * @return An async wrapper to load a business note as HTML from the Evernote service.
     */
    public synchronized EvernoteHtmlHelper getHtmlHelperBusiness() throws TException, EDAMUserException, EDAMSystemException {
        if (mHtmlHelperBusiness == null) {
            com.evernote.client.android.AuthenticationResult authenticationResult = authenticateToBusiness();
            mHtmlHelperBusiness = createHtmlHelper(authenticationResult.getBusinessAuthToken());
        }
        return mHtmlHelperBusiness;
    }

    /**
     * @see #getHtmlHelperBusiness()
     */
    public Future<EvernoteHtmlHelper> getHtmlHelperBusinessAsync(@Nullable EvernoteCallback<EvernoteHtmlHelper> callback) {
        return mCreateHelperClient.submitTask(new Callable<EvernoteHtmlHelper>() {
            @Override
            public EvernoteHtmlHelper call() throws Exception {
                return getHtmlHelperBusiness();
            }
        }, callback);
    }

    protected EvernoteHtmlHelper createHtmlHelper(String authToken) {
        return new EvernoteHtmlHelper(mHttpClient, mEvernoteSession.getAuthenticationResult().getEvernoteHost(), authToken, mExecutorService);
    }

    protected TBinaryProtocol createBinaryProtocol(String url) {
        return new TBinaryProtocol(new TAndroidTransport(mHttpClient, mByteStore, url, mHeaders));
    }

    protected NoteStore.Client createNoteStoreClient(String url) {
        return new NoteStore.Client(createBinaryProtocol(url));
    }

    protected synchronized EvernoteNoteStoreClient createEvernoteNoteStoreClient(String url, String authToken) {
        return new EvernoteNoteStoreClient(createNoteStoreClient(url), authToken, mExecutorService);
    }

    protected final String createKey(String url, String authToken) {
        return url + authToken;
    }

    protected final com.evernote.client.android.AuthenticationResult authenticateToBusiness() throws TException, EDAMUserException, EDAMSystemException {
        com.evernote.client.android.AuthenticationResult authResult = mEvernoteSession.getAuthenticationResult();

        if (authResult.getBusinessAuthToken() == null || authResult.getBusinessAuthTokenExpiration() < System.currentTimeMillis()) {
            AuthenticationResult businessAuthResult = getUserStoreClient().authenticateToBusiness();
            authResult.setBusinessAuthData(businessAuthResult);
        }

        return authResult;
    }

    public static class Builder {

        private final EvernoteSession mEvernoteSession;
        private final Map<String, String> mHeaders;

        private OkHttpClient mHttpClient;
        private ByteStore mByteStore;
        private ExecutorService mExecutorService;

        /**
         * @param evernoteSession The current session, must not be {@code null}.
         */
        public Builder(EvernoteSession evernoteSession) {
            mEvernoteSession = EvernotePreconditions.checkNotNull(evernoteSession);
            mHeaders = new HashMap<>();
        }

        /**
         * @param httpClient The client executing the HTTP calls.
         */
        public Builder setHttpClient(OkHttpClient httpClient) {
            mHttpClient = httpClient;
            return this;
        }

        /**
         * @param byteStore Caches the written data, which is later sent to the Evernote service.
         */
        public Builder setByteStore(ByteStore byteStore) {
            mByteStore = byteStore;
            return this;
        }

        private Builder addHeader(String name, String value) {
            // maybe set this to public
            mHeaders.put(name, value);
            return this;
        }

        /**
         * @param executorService Runs the background actions.
         */
        public Builder setExecutorService(ExecutorService executorService) {
            mExecutorService = executorService;
            return this;
        }

        public EvernoteClientFactory build() {
            if (mHttpClient == null) {
                mHttpClient = createDefaultHttpClient();
            }
            if (mByteStore == null) {
                mByteStore = createDefaultByteStore(mEvernoteSession.getApplicationContext());
            }
            if (mExecutorService == null) {
                mExecutorService = Executors.newSingleThreadExecutor();
            }

            addHeader("Cache-Control", "no-transform");
            addHeader("Accept", "application/x-thrift");
            addHeader("User-Agent", EvernoteUtil.generateUserAgentString(mEvernoteSession.getApplicationContext()));

            return new EvernoteClientFactory(mEvernoteSession, mHttpClient, mByteStore, mHeaders, mExecutorService);
        }

        private OkHttpClient createDefaultHttpClient() {
            OkHttpClient httpClient = new OkHttpClient();
            httpClient.setConnectTimeout(10, TimeUnit.SECONDS);
            httpClient.setReadTimeout(10, TimeUnit.SECONDS);
            httpClient.setWriteTimeout(20, TimeUnit.SECONDS);
            httpClient.setConnectionPool(new ConnectionPool(20, 2 * 60 * 1000));
            return httpClient;
        }

        private ByteStore createDefaultByteStore(Context context) {
            int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 32);
            return new DiskBackedByteStore(new File(context.getCacheDir(), "evernoteCache"), cacheSize);
        }
    }
}
