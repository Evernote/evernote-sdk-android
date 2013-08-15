package com.evernote.client.android;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that uses reflection to asynchronously wrap Client methods
 */
class AsyncReflector {

  /**
   * List of primitives to convert from autoboxed method calls
   */
  public final static Map<Class<?>, Class<?>> sPrimitiveMap = new HashMap<Class<?>, Class<?>>();
  static {
    sPrimitiveMap.put(Boolean.class, boolean.class);
    sPrimitiveMap.put(Byte.class, byte.class);
    sPrimitiveMap.put(Short.class, short.class);
    sPrimitiveMap.put(Character.class, char.class);
    sPrimitiveMap.put(Integer.class, int.class);
    sPrimitiveMap.put(Long.class, long.class);
    sPrimitiveMap.put(Float.class, float.class);
    sPrimitiveMap.put(Double.class, double.class);
  }

  /**
   * Singled threaded Executor for async work
   */
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
                 //Convert Autoboxed primitives to actual primitives (ex: Integer.class to int.class)
                 if(sPrimitiveMap.containsKey(args[i].getClass())) {
                    classes[i] = sPrimitiveMap.get(args[i].getClass());
                 } else {
                    classes[i] = args[i].getClass();
                 }
              }

              Method method = null;
              if(receiver instanceof Class) {
                 //Can receive a class if using for static methods
                 method = ((Class)receiver).getMethod(function, classes);
              } else {
                 //used for instance methods
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

  static <T> void execute(final Object receiver, final String function, final Object... args) {
     if (args != null){
        Object lastArgument = args[args.length - 1];
        if (lastArgument instanceof OnClientCallback){
           Object[] newArgs = new Object[args.length - 1];
           for (int i=0; i<args.length-1; i++){
              newArgs[i] = args[i];
           }
           execute(receiver, (OnClientCallback<T>)lastArgument, function, newArgs);
        } else {
           execute(receiver, null, function, args);
        }
     } else {
        execute(receiver, null, function, args);
     }
  }
}
