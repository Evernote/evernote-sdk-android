package com.evernote.client.oauth;

/** 
 * A Scribe Api that for the sandbox Evernote web service API. 
 */
public class EvernoteSandboxApi extends EvernoteApi {  
  public static String evernoteHost = "sandbox.evernote.com";

  @Override
  protected String getEvernoteHost() {
    return evernoteHost;
  }
}
