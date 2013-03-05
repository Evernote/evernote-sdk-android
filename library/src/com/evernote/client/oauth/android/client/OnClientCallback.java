package com.evernote.client.oauth.android.client;

/**
 * This class must be implemented to use the {@link AsyncNoteStoreClient} and the {@link AsyncUserStoreClient}
 */
public interface OnClientCallback<T, Y extends Throwable> {
  /**
   * @param data sent to callback when the async operation has completed positively
   */
  public void onResultsReceived(T data);

  /**
   * @param exception is the error sent to the callback when the async operation has completed negatively
   */
  public void onErrorReceived(Y exception);
}
