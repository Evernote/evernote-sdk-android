package com.evernote.client.oauth.android;

import android.util.Log;
import com.evernote.client.conn.mobile.TEvernoteHttpClient;
import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.TTransportException;

import java.io.File;
import java.util.Map;

/**
 * A class to produce User and Note store clients.
 *
 *
 * @author @briangriffey
 * @author @tylersmithnet
 */

public class ClientFactory {
  private String LOGTAG = "ClientFactory";
  private static final String USER_AGENT_KEY = "User-Agent";

  private String mUserAgent;
  private Map<String, String> mCustomHeaders;
  private File mTempDir;
  private NoteStore.Client mBusinessNoteStoreClient;


  /**
   * Private constructor
   */
  private ClientFactory() {}

  /**
   * Protected constructor. This should always be requested through an {@link com.evernote.client.oauth.android.EvernoteSession}
   */
  protected ClientFactory(String userAgent, File tempDir) {
    mUserAgent = userAgent;
    mTempDir = tempDir;
  }

  /**
   * Create a new NoteStore client. Each call to this method will return
   * a new NoteStore.Client instance. The returned client can be used for any
   * number of API calls, but is NOT thread safe.
   *
   * @throws IllegalStateException if @link #isLoggedIn() is false.
   * @throws TTransportException if an error occurs setting up the
   * connection to the Evernote service.
   */
  public AsyncNoteStoreClient createNoteStoreClient() throws TTransportException {
    if(EvernoteSession.getOpenSession() == null || EvernoteSession.getOpenSession().getAuthenticationResult() == null) {
      throw new IllegalStateException();
    }

    return createNoteStoreClient(EvernoteSession.getOpenSession().getAuthenticationResult().getNoteStoreUrl());
  }

  public AsyncNoteStoreClient createNoteStoreClient(String url) throws TTransportException {
    TEvernoteHttpClient transport =
        new TEvernoteHttpClient(url, mUserAgent, mTempDir);
    TBinaryProtocol protocol = new TBinaryProtocol(transport);
    return new AsyncNoteStoreClient(protocol, protocol, EvernoteSession.getOpenSession().getAuthenticationResult().getAuthToken());
  }


  /**
   *
   * Create a new Business NoteStore client. Each call to this method will return
   * a new NoteStore.Client instance. The returned client can be used for any
   * number of API calls, but is NOT thread safe.
   *
   * This method will check if the user is a business user if the
   * {@link com.evernote.client.oauth.android.EvernoteSession#isBusinessUser()} has not been called,
   * this is a network request
   *
   * This method will check expiration time for the business authorization token, this is a network request
   *
   * This method is synchronous
   *
   * @throws TException
   * @throws EDAMUserException
   * @throws EDAMSystemException User is not part of a business
   */
  public AsyncNoteStoreClient createBusinessNoteStoreClient() throws TException, EDAMUserException, EDAMSystemException {
    com.evernote.client.oauth.android.AuthenticationResult authResult =
        EvernoteSession.getOpenSession().getAuthenticationResult();

    if(authResult.getBusinessId() == -1) {
      //Make one more network request in case user hasn't called isBusinessUser()
      if(!EvernoteSession.getOpenSession().isBusinessUser()) {
        Log.e(LOGTAG, "User is not part of a business");
        throw new EDAMSystemException(EDAMErrorCode.UNSUPPORTED_OPERATION);
      }
    }

    if(authResult.getBusinessAuthTokenExpiration() < System.currentTimeMillis()) {
      AuthenticationResult evernoteAuth = createUserStoreClient().authenticateToBusiness(authResult.getAuthToken());
      authResult.setBusinessAuthTokenExpiration(evernoteAuth.getExpiration());
      authResult.setBusinessNoteStoreUrl(evernoteAuth.getNoteStoreUrl());
    }

    return createNoteStoreClient(authResult.getBusinessNoteStoreUrl());
  }

  /**
   * This is an async call to retrieve a business note store.
   *
   * @param callback to receive results from creating NoteStore
   */
  public void createBusinessNoteStoreClient(final OnClientCallback<AsyncNoteStoreClient, Exception> callback) {
    EvernoteSession.getOpenSession().getThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        try {
          callback.onResultsReceivedBG(createBusinessNoteStoreClient());
        } catch(Exception ex) {
          callback.onErrorReceivedBG(ex);
        }
      }
    });
  }


  /**
   * Creates a UserStore client interface that can be used to send requests to a
   * particular UserStore server. For example, the following would provide a
   * handle to make requests from the "MyClient" application to talk to the
   * Evernote server at "www.evernote.com" :
   * <p/>
   * <pre>
   * UserStore.Iface userStore = factory.createUserStoreClient(&quot;www.evernote.com&quot;,
   *     &quot;MyClient (Java)&quot;);
   * </pre>
   * <p/>
   * This call does not actually initiate any communications with the UserStore,
   * it only creates the handle that will be used.
   *
   * @param url the hostname (or numeric IP address) for the server that we should
   *            communicate with. This will attempt to use HTTPS to talk to that
   *            server unless the hostname contains a port number component, in
   *            which case we'll use plaintext HTTP.
   * @param url the hostname (or numeric IP address) for the server that we should
   *            communicate with. This will attempt to use HTTPS to talk to that
   *            server unless the hostname contains a port number component, in
   *            which case we'll use plaintext HTTP.
   * @return
   * @throws TTransportException
   */
  public AsyncUserStoreClient createUserStoreClient(String url) throws TTransportException {
    return createUserStoreClient(url, 0);
  }

  /**
   * Create a new UserStore client. Each call to this method will return
   * a new UserStore.Client instance. The returned client can be used for any
   * number of API calls, but is NOT thread safe.
   *
   * @throws IllegalStateException if @link #isLoggedIn() is false.
   * @throws TTransportException if an error occurs setting up the
   * connection to the Evernote service.
   *
   */
  public AsyncUserStoreClient createUserStoreClient()  throws IllegalStateException, TTransportException {
    if(EvernoteSession.getOpenSession() == null || EvernoteSession.getOpenSession().getAuthenticationResult() == null) {
      throw new IllegalStateException();
    }

    return createUserStoreClient(EvernoteSession.getOpenSession().getAuthenticationResult().getEvernoteHost());
  }

  public AsyncUserStoreClient createUserStoreClient(String serviceUrl, int port) throws TTransportException {
    String url = getFullUrl(serviceUrl, port);

    TEvernoteHttpClient transport =
        new TEvernoteHttpClient(url, mUserAgent, mTempDir);

    if (mCustomHeaders != null) {
      for (Map.Entry<String, String> header : mCustomHeaders.entrySet()) {
        transport.setCustomHeader(header.getKey(), header.getValue());
      }
    }
    if (mUserAgent != null) {
      transport.setCustomHeader(USER_AGENT_KEY, mUserAgent);
    }
    TBinaryProtocol protocol = new TBinaryProtocol(transport);
    return new AsyncUserStoreClient(protocol, protocol);
  }

  private String getFullUrl(String serviceUrl, int port) {
    String url = "";

    if (port != 0)
      serviceUrl += ":" + port;
    if (!serviceUrl.startsWith("http")) {
      url = serviceUrl.contains(":") ? "http://" : "https://";
    }

    url += serviceUrl + "/edam/user";

    return url;
  }


  public String getUserAgent() {
    return mUserAgent;
  }

  public void setUserAgent(String mUserAgent) {
    this.mUserAgent = mUserAgent;
  }

  /**
   * if non-null, this is a mapping of HTTP headers to values which
   * will be included in the request.
   */
  public Map<String, String> getCustomHeaders() {
    return mCustomHeaders;
  }

  public void setCustomHeaders(Map<String, String> mCustomHeaders) {
    this.mCustomHeaders = mCustomHeaders;
  }

  /**
   * a temporary directory in which large outgoing Thrift messages will
   * be cached to disk before they are sent
   */
  public File getTempDir() {
    return mTempDir;
  }

  public void setTempDir(File mTempDir) {
    this.mTempDir = mTempDir;
  }

}
