package com.evernote.client.oauth.android.client;


import com.evernote.client.oauth.android.EvernoteSession;
import com.evernote.edam.type.PremiumInfo;
import com.evernote.edam.type.User;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.edam.userstore.BootstrapInfo;
import com.evernote.edam.userstore.PublicUserInfo;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.protocol.TProtocol;

import java.util.concurrent.ExecutorService;

/**
 * An Async wrapper for {@link UserStore.Client}
 * Use these methods with a {@link OnClientCallback} to get make network requests
 *
 * @author @tylersmithnet
 */
public class AsyncUserStoreClient extends UserStore.Client {

  ExecutorService mThreadExecutor;

  public AsyncUserStoreClient(TProtocol prot) {
    super(prot);
    mThreadExecutor = EvernoteSession.getOpenSession().getThreadExecutor();
  }

  public AsyncUserStoreClient(TProtocol iprot, TProtocol oprot) {
    super(iprot, oprot);
    mThreadExecutor = EvernoteSession.getOpenSession().getThreadExecutor();
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#checkVersion(String, short, short)
   */
  public void checkVersion(final String clientName, final short edamVersionMajor, final short edamVersionMinor, final OnClientCallback<Boolean, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncUserStoreClient.super.checkVersion(clientName, edamVersionMajor, edamVersionMinor));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#getBootstrapInfo(String)
   */
  public void getBootstrapInfo(final String locale, final OnClientCallback<BootstrapInfo, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncUserStoreClient.super.getBootstrapInfo(locale));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#authenticate(String, String, String, String)
   */
  public void authenticate(final String username, final String password, final String consumerKey, final String consumerSecret, final OnClientCallback<AuthenticationResult, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncUserStoreClient.super.authenticate(username, password, consumerKey, consumerSecret));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#authenticateLongSession(String, String, String, String, String, String)
   */
  public void authenticateLongSession(final String username, final String password, final String consumerKey, final String consumerSecret, final String deviceIdentifier, final String deviceDescription, final OnClientCallback<AuthenticationResult, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncUserStoreClient.super.authenticateLongSession(username, password, consumerKey, consumerSecret, deviceIdentifier, deviceDescription));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#authenticateToBusiness(String)
   */
  public void authenticateToBusiness(final String authenticationToken, final OnClientCallback<AuthenticationResult, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncUserStoreClient.super.authenticateToBusiness(authenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#refreshAuthentication(String)
   */
  public void refreshAuthentication(final String authenticationToken, final OnClientCallback<AuthenticationResult, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncUserStoreClient.super.refreshAuthentication(authenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#getUser(String)
   */
  public void getUser(final String authenticationToken, final OnClientCallback<User, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncUserStoreClient.super.getUser(authenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#getPublicUserInfo(String)
   */
  public void getPublicUserInfo(final String username, final OnClientCallback<PublicUserInfo, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncUserStoreClient.super.getPublicUserInfo(username));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#getPremiumInfo(String)
   */
  public void getPremiumInfo(final String authenticationToken, final OnClientCallback<PremiumInfo, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncUserStoreClient.super.getPremiumInfo(authenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#getNoteStoreUrl(String)
   */
  public void getNoteStoreUrl(final String authenticationToken, final OnClientCallback<String, Exception> callback) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          callback.onResultsReceived(AsyncUserStoreClient.super.getNoteStoreUrl(authenticationToken));
        } catch (Exception ex) {
          callback.onErrorReceived(ex);
        }
      }
    });
  }
}

