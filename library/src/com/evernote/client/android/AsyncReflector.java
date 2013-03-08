package com.evernote.client.android;

import android.os.Handler;
import android.os.Looper;

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
              if(callback != null) callback.onSuccess(answer);
            }
          });

        } catch (final Exception ex) {
          handler.post(new Runnable() {
            @Override
            public void run() {
              if(callback != null) callback.onException(ex);
            }
          });
        }
      }
    });
  }
}
