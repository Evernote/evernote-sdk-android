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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/** 
 * A Scribe AccessTokenExtractor that allows empty token secrets and returns
 * EvernoteAccessToken objects.
 */ 
public class AccessTokenExtractor implements org.scribe.extractors.AccessTokenExtractor {
  
  private static final Pattern TOKEN_REGEX = Pattern.compile("oauth_token=([^&]+)");
  private static final Pattern SECRET_REGEX = Pattern.compile("oauth_token_secret=([^&]*)");
  private static final Pattern NOTESTORE_REGEX = Pattern.compile("edam_noteStoreUrl=([^&]+)");
  private static final Pattern WEBAPI_REGEX = Pattern.compile("edam_webApiUrlPrefix=([^&]+)");
  private static final Pattern USERID_REGEX = Pattern.compile("edam_userId=([^&]+)");
  
  /**
   * {@inheritDoc} 
   */
  public Token extract(String response)
  {
    Preconditions.checkEmptyString(response, "Response body is incorrect. " +
        "Can't extract a token from an empty string");
    return new EvernoteAuthToken(extract(response, TOKEN_REGEX), 
        extract(response, SECRET_REGEX), 
        extract(response, NOTESTORE_REGEX), 
        extract(response, WEBAPI_REGEX), 
        Integer.parseInt(extract(response, USERID_REGEX)), 
        response);
  }

  private String extract(String response, Pattern p)
  {
    Matcher matcher = p.matcher(response);
    if (matcher.find() && matcher.groupCount() >= 1)
    {
      return OAuthEncoder.decode(matcher.group(1));
    }
    else
    {
      throw new OAuthException("Response body is incorrect. " +
          "Can't extract token and secret from this: '" + response + "'", null);
    }
  }
}