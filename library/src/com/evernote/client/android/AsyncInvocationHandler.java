package com.evernote.client.android;

import com.evernote.edam.notestore.NoteStore;
import com.evernote.thrift.protocol.TProtocol;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: ahmedre
 * Date: 5/13/13
 * Time: 10:59 PM
 */
public class AsyncInvocationHandler implements InvocationHandler {
   protected String mAuthenticationToken;
   protected final NoteStore.Client mClient;

   AsyncInvocationHandler(TProtocol prot, String authenticationToken) {
      mClient = new NoteStore.Client(prot);
      mAuthenticationToken = authenticationToken;
   }

   AsyncInvocationHandler(TProtocol iprot, TProtocol oprot, String authenticationToken) {
      mClient = new NoteStore.Client(iprot, oprot);
      mAuthenticationToken = authenticationToken;
   }

   @Override
   public Object invoke(Object o, Method method, Object[] args) throws Throwable {
      if (method.getDeclaringClass() == Object.class) {
         return method.invoke(this, args);
      }

      int len = (args == null)? 1 : args.length + 1;
      Object[] newArgs = new Object[len];

      int i = 0;
      newArgs[i++] = mAuthenticationToken;
      if (args != null){
         for (Object arg : args){
            newArgs[i++] = arg;
         }
      }

      AsyncReflector.execute(mClient, method.getName(), newArgs);
      return null;
   }
}
