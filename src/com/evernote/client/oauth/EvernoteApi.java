/*
The MIT License

Copyright (c) 2010 Pablo Fernandez

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

Modified by Evernote for use with the Evernote API.
*/

package com.evernote.client.oauth;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

/** 
 * A Scribe Api that for the production Evernote web service API. 
 */
public class EvernoteApi extends DefaultApi10a {
  public static String evernoteHost = "www.evernote.com";

  protected String getEvernoteHost() {
    return evernoteHost;
  }

  @Override
  public String getAccessTokenEndpoint() {
    return "https://" + getEvernoteHost() + "/oauth";
  }

  @Override
  public String getAuthorizationUrl(Token requestToken) {
    String template = "https://" + getEvernoteHost() + "/OAuth.action?oauth_token=%s";
    return String.format(template, requestToken.getToken());
  }

  @Override
  public String getRequestTokenEndpoint() {
    return "https://" + getEvernoteHost() + "/oauth";
  }
  
  @Override
  public org.scribe.extractors.AccessTokenExtractor getAccessTokenExtractor() {
    return new AccessTokenExtractor();
  }
}
