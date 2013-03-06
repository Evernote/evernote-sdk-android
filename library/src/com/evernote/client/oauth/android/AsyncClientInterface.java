package com.evernote.client.oauth.android;

public interface AsyncClientInterface {
  <T> void execute(final OnClientCallback<T, Exception> callback, final String function, final Object... args);
}
