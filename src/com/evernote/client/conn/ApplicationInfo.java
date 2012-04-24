/*
 * Copyright 2012 Evernote Corporation
 * All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *  
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *     
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.evernote.client.conn;

import com.evernote.edam.util.EDAMUtil;

/**
 * A container class for the application-specific information needed to use the
 * Everote API.
 */
public class ApplicationInfo {

  private String consumerKey;
  private String consumerSecret;
  private String evernoteHost;
  private String userAgent;
  
  /**
   * Create a new ApplicationInfo.
   * 
   * @param consumerKey Your application's API consumer key.
   * @param consumerSecret Your application's API consumer secret.
   * @param evernoteHost The Evernote host that you wish to connect to
   * (sandbox.evernote.com or www.evernote.com).
   * @param appName The name of your application, used to identify the app
   * in API calls.
   * @param appVerion The version of your application, used to identify the app
   * in API calls. 
   */
  public ApplicationInfo(String consumerKey, String consumerSecret, 
      String evernoteHost, String appName, String appVersion) {
    this.consumerKey = consumerKey;
    this.consumerSecret = consumerSecret;
    this.evernoteHost = evernoteHost;
    this.userAgent = EDAMUtil.getUserAgentString(appName, appVersion);
  }
  
  public String getConsumerKey() {
    return consumerKey;
  }

  public String getConsumerSecret() {
    return consumerSecret;
  }

  public String getEvernoteHost() {
    return evernoteHost;
  }
  
  public String getUserAgent() {
    return userAgent;
  }
}
