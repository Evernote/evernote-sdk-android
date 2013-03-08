package com.evernote.client.android;

import android.os.Handler;
import android.os.Looper;
import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMUserException;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that uses reflection to asynchronously wrap Client methods
 */
class AsyncReflector {
  private static ExecutorService sThreadExecutor = Executors.newSingleThreadExecutor();
  /**
   * Reflection to run Asynchronous methods
   */
  static <T> void execute(final Object receiver, final OnClientCallback<T> callback, final String function, final Object... args) {
    final Handler handler = new Handler(Looper.getMainLooper());
    sThreadExecutor.execute(new Runnable() {
      public void run() {
        try {
          Class[] classes = new Class[args.length];
          for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
          }

          Method method = null;
          if(receiver instanceof Class) {
            method = ((Class)receiver).getMethod(function, classes);
          } else {
            method = receiver.getClass().getMethod(function, classes);
          }

          final T answer = (T) method.invoke(receiver, args);

          handler.post(new Runnable() {
            @Override
            public void run() {
              if(callback != null) callback.onResultsReceived(answer);
            }
          });

        } catch (final Exception ex) {
          if(ex instanceof EDAMUserException) {
            EDAMUserException userError = (EDAMUserException)ex;
            if("authenticationToken".equals(userError.getParameter()) &&
                (userError.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED ||
                    userError.getErrorCode() == EDAMErrorCode.BAD_DATA_FORMAT)) {
              handler.post(new Runnable() {
                @Override
                public void run() {
                  if(callback != null && callback.getContext() != null){
                    EvernoteSession.getOpenSession().logOut(callback.getContext());
                    EvernoteSession.getOpenSession().authenticate(callback.getContext());
                  }
                }
              });

            }
          }

          handler.post(new Runnable() {
            @Override
            public void run() {
              if(callback != null) callback.onExceptionReceived(ex);
            }
          });
        }
      }
    });
  }
}
