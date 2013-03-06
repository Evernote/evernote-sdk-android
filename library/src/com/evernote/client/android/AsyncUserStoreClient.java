package com.evernote.client.android;


import android.os.Handler;
import android.os.Looper;
import com.evernote.edam.type.PremiumInfo;
import com.evernote.edam.type.User;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.edam.userstore.BootstrapInfo;
import com.evernote.edam.userstore.PublicUserInfo;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.protocol.TProtocol;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * An Async wrapper for {@link UserStore.Client}
 * Use these methods with a {@link OnClientCallback} to get make network requests
 *
 * @author @tylersmithnet
 */
public class AsyncUserStoreClient extends UserStore.Client implements AsyncClientInterface {

  private final ExecutorService mThreadExecutor;
  private final Handler mUIHandler;

  AsyncUserStoreClient(TProtocol prot) {
    super(prot);
    mThreadExecutor = EvernoteSession.getOpenSession().getThreadExecutor();
    mUIHandler = new Handler(Looper.getMainLooper());
  }

  AsyncUserStoreClient(TProtocol iprot, TProtocol oprot) {
    super(iprot, oprot);
    mThreadExecutor = EvernoteSession.getOpenSession().getThreadExecutor();
    mUIHandler = new Handler(Looper.getMainLooper());
  }

  /**
   * Reflection to run Asynchronous methods
   */
  public <T> void execute(final OnClientCallback<T, Exception> callback, final String function, final Object... args) {
    mThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          Class[] classes = new Class[args.length];
          for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
          }

          Method method = AsyncUserStoreClient.this.getClass().getMethod(function, classes);
          final T answer = (T) method.invoke(AsyncUserStoreClient.this, args);

          mUIHandler.post(new Runnable() {
            @Override
            public void run() {
              callback.onResultsReceived(answer);
            }
          });

        } catch (final Exception ex) {
          mUIHandler.post(new Runnable() {
            @Override
            public void run() {
              callback.onErrorReceived(ex);
            }
          });
        }
      }
    });
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#checkVersion(String, short, short)
   */
  public void checkVersion(final String clientName, final short edamVersionMajor, final short edamVersionMinor, final OnClientCallback<Boolean, Exception> callback) {
    execute(callback, "checkVersion", clientName, edamVersionMajor, edamVersionMinor);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#getBootstrapInfo(String)
   */
  public void getBootstrapInfo(final String locale, final OnClientCallback<BootstrapInfo, Exception> callback) {
    execute(callback, "getBootstrapInfo", locale);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#authenticate(String, String, String, String)
   */
  public void authenticate(final String username, final String password, final String consumerKey, final String consumerSecret, final OnClientCallback<AuthenticationResult, Exception> callback) {
    execute(callback, "authenticate", username, password, consumerKey, consumerSecret);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#authenticateLongSession(String, String, String, String, String, String)
   */
  public void authenticateLongSession(final String username, final String password, final String consumerKey, final String consumerSecret, final String deviceIdentifier, final String deviceDescription, final OnClientCallback<AuthenticationResult, Exception> callback) {
    execute(callback, "authenticateLongSession", username, password, consumerKey, consumerSecret, deviceIdentifier, deviceDescription);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#authenticateToBusiness(String)
   */
  public void authenticateToBusiness(final String authenticationToken, final OnClientCallback<AuthenticationResult, Exception> callback) {
    execute(callback, "authenticateToBusiness", authenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#refreshAuthentication(String)
   */
  public void refreshAuthentication(final String authenticationToken, final OnClientCallback<AuthenticationResult, Exception> callback) {
    execute(callback, "refreshAuthentication", authenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#getUser(String)
   */
  public void getUser(final String authenticationToken, final OnClientCallback<User, Exception> callback) {
    execute(callback, "getUser", authenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#getPublicUserInfo(String)
   */
  public void getPublicUserInfo(final String username, final OnClientCallback<PublicUserInfo, Exception> callback) {
    execute(callback, "getPublicUserInfo", username);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#getPremiumInfo(String)
   */
  public void getPremiumInfo(final String authenticationToken, final OnClientCallback<PremiumInfo, Exception> callback) {
    execute(callback, "getPremiumInfo", authenticationToken);
  }

  /**
   * Asynchronous wrapper
   *
   * @param {@link OnClientCallback} providing an interface to the calling code
   * @see UserStore.Client#getNoteStoreUrl(String)
   */
  public void getNoteStoreUrl(final String authenticationToken, final OnClientCallback<String, Exception> callback) {
    execute(callback, "getNoteStoreUrl", authenticationToken);
  }
}

