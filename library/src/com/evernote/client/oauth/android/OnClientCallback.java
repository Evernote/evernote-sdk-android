package com.evernote.client.oauth.android;

import android.os.Handler;
import android.os.Looper;

/**
 * This class must be implemented to use the {@link AsyncNoteStoreClient} and the {@link AsyncUserStoreClient}
 */
public abstract class OnClientCallback<T, Y extends Throwable> {
  private Handler mUIHandler = new Handler(Looper.getMainLooper());
  /**
   * @param data sent to callback when the async operation has completed positively
   */
  public abstract void onResultsReceived(T data);

  void onResultsReceivedBG(final T data) {
    mUIHandler.post(new Runnable() {
      @Override
      public void run() {
        onResultsReceived(data);
      }
    });
  }

  /**
   * @param exception is the error sent to the callback when the async operation has completed negatively
   */
  public abstract void onErrorReceived(Y exception);

  void  onErrorReceivedBG(final Y exception) {
    mUIHandler.post(new Runnable() {
      @Override
      public void run() {
        onErrorReceived(exception);
      }
    });
  }
}
