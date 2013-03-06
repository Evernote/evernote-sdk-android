package com.evernote.client.oauth.android;

/**
 *An Interface to require the execute for asynchronous reflection wrappers.
 * @author @tylersmithnet
 */
interface AsyncClientInterface {
  /**
   * Reflection to run Asynchronous methods
   */
  <T> void execute(final OnClientCallback<T, Exception> callback, final String function, final Object... args);
}
