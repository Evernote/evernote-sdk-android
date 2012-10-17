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
package com.evernote.edam.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.User;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.edam.userstore.UserStore;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;

/**
 * Contains a set of static helper functions that may be useful when
 * accessing Evernote via the EDAM API.
 */
public class EDAMUtil {

  /**
   * All EDAM strings will be encoded as UTF-8.
   */
  public static final Charset UTF8 = Charset.forName("UTF-8");
  public static final Charset DEFAULT_CHARSET = UTF8;

  private static final ConcurrentHashMap<String, Charset>
    encodingToCharsetCache = new ConcurrentHashMap<String, Charset>();

  /**
   * One-way hashing function used for providing a checksum of EDAM data
   */
  public static final String EDAM_HASH_ALGORITHM = "MD5";

  /**
   * A runtime exception that will be thrown when we hit an error that should
   * "never" occur ... e.g. if the JVM doesn't know about UTF-8 or MD5.
   */
  private static final class EDAMUtilException extends RuntimeException {

    private static final long serialVersionUID = -8099786694856724498L;

    public EDAMUtilException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  /**
   * Creates a properly formated user agent string for the EDAM protocol in the
   * format:
   *
   * <pre>
   * application / version ; platform / version ; [ device-make device-model / version ;]
   * </pre>
   *
   * @param applicationName
   *          name of the application string
   * @param svnRevisionNumber
   *          the revision number of the application
   * @return A formated user agent string for use in
   *         {@link EDAMUtil#getNoteStoreClient(String, String, User)} and
   *         {@link EDAMUtil#getUserAgentString(String, String)}
   */
  public static final String getUserAgentString(String applicationName,
      String svnRevisionNumber) {
    return applicationName + "/" + svnRevisionNumber + ";"
        + System.getProperty("os.name") + "/"
        + System.getProperty("os.version") + "; Java "
        + System.getProperty("java.vendor") + "/"
        + System.getProperty("java.version") + ";";
  }

  /**
   * Creates a UserStore client interface that can be used to send requests to a
   * particular UserStore server. For example, the following would provide a
   * handle to make requests from the "MyClient" application to talk to the
   * Evernote server at "www.evernote.com" :
   *
   * <pre>
   * UserStore.Iface userStore = EDAMUtil.getUserStoreClient(&quot;www.evernote.com&quot;,
   *     &quot;MyClient (Java)&quot;);
   * </pre>
   *
   * This call does not actually initiate any communications with the UserStore,
   * it only creates the handle that will be used.
   *
   * @param host
   *          the hostname (or numeric IP address) for the server that we should
   *          communicate with. This will attempt to use HTTPS to talk to that
   *          server unless the hostname contains a port number component, in
   *          which case we'll use plaintext HTTP.
   * @param userAgent
   *          if non-null, this is the User-Agent string that will be provided
   *          on all HTTP requests to the service. This allows for better
   *          logging and debugging if problems arise.
   * @return The client interface that can be used to talk to the UserStore
   * @throws TTransportException
   *           if the provided information can't be used to construct a
   *           UserStore handle. (E.g. if the hostname is malformed.)
   */
  public static UserStore.Client getUserStoreClient(String host,
      String userAgent) throws TTransportException {
    return getUserStoreClient(host, userAgent, null);
  }

  /**
   * Creates a UserStore client interface that can be used to send requests to a
   * particular UserStore server. For example, the following would provide a
   * handle to make requests from the "MyClient" application to talk to the
   * Evernote server at "www.evernote.com" :
   *
   * <pre>
   * UserStore.Iface userStore = EDAMUtil.getUserStoreClient(&quot;www.evernote.com&quot;,
   *     &quot;MyClient (Java)&quot;);
   * </pre>
   *
   * This call does not actually initiate any communications with the UserStore,
   * it only creates the handle that will be used.
   *
   * @param host
   *          the hostname (or numeric IP address) for the server that we should
   *          communicate with. This will attempt to use HTTPS to talk to that
   *          server unless the hostname contains a port number component, in
   *          which case we'll use plaintext HTTP.
   * @param userAgent
   *          if non-null, this is the User-Agent string that will be provided
   *          on all HTTP requests to the service. This allows for better
   *          logging and debugging if problems arise.
   * @param customHeaders
   *          if non-null, this is a mapping of HTTP headers to values which
   *          will be included in the request.
   * @return The client interface that can be used to talk to the UserStore
   * @throws TTransportException
   *           if the provided information can't be used to construct a
   *           UserStore handle. (E.g. if the hostname is malformed.)
   */
  public static UserStore.Client getUserStoreClient(String host,
      String userAgent, Map<String, String> customHeaders)
  throws TTransportException {
    String url = host.contains(":") ? "http://" : "https://";
    url += host + "/edam/user";
    THttpClient transport = new THttpClient(url);
    if (customHeaders != null) {
      for (Entry<String, String> header : customHeaders.entrySet()) {
        transport.setCustomHeader(header.getKey(), header.getValue());
      }
    }
    if (userAgent != null) {
      transport.setCustomHeader("User-Agent", userAgent);
    }
    TBinaryProtocol protocol = new TBinaryProtocol(transport) {
      @Override
      public void readMessageEnd() {
        ((THttpClient)getTransport()).close();
      }
    };
    return new UserStore.Client(protocol, protocol);
  }

  /**
   * Creates a NoteStore client interface that can be used to send requests to a
   * particular NoteStore shard. For example, the following would provide a
   * handle to make requests from the "MyClient" application to talk to the
   * Evernote server at "www.evernote.com" :
   *
   * <pre>
   *   NoteStore.Iface noteStore =
   *     EDAMUtil.getNoteStoreClient(&quot;https://www.evernote.com/shard/s1/notestore&quot;, &quot;MyClient (Java)&quot;);
   * </pre>
   *
   * This call does not actually initiate any communications with the NoteStore,
   * it only creates the handle that will be used.
   *
   * @param url
   *          the URL for the NoteStore HTTP service, which would be retrieved
   *          from the AuthenticationResponse.noteStoreUrl field after
   *          authentication
   * @param userAgent
   *          if non-null, this is the User-Agent string that will be provided
   *          on all HTTP requests to the service. This allows for better
   *          logging and debugging if problems arise.
   * @return The client interface that can be used to talk to the NoteStore
   * @throws TTransportException
   *           if the provided information can't be used to construct a
   *           NoteStore handle. (E.g. if the hostname is malformed.)
   */
  public static NoteStore.Client getNoteStoreClient(String url,
      String userAgent) throws TTransportException {
    return getNoteStoreClient(url, userAgent, null);
  }

  /**
   * Creates a NoteStore client interface that can be used to send requests to a
   * particular NoteStore shard. For example, the following would provide a
   * handle to make requests from the "MyClient" application to talk to the
   * Evernote server determined by the provided AuthenticationResult:
   *
   * <pre>
   *   AuthenticationResult authResult = ...;
   *   NoteStore.Iface noteStore =
   *     EDAMUtil.getNoteStoreClient(authResult, &quot;MyClient (Java)&quot;);
   * </pre>
   *
   * This call does not actually initiate any communications with the NoteStore,
   * it only creates the handle that will be used.
   *
   * @param authResult
   *          The AuthenticationResult returned by the server after a call
   *          to UserStore.authentication, which contains connection information
   *          for the appropriate shard
   * @param userAgent
   *          if non-null, this is the User-Agent string that will be provided
   *          on all HTTP requests to the service. This allows for better
   *          logging and debugging if problems arise.
   * @return The client interface that can be used to talk to the NoteStore
   * @throws TTransportException
   *           if the provided information can't be used to construct a
   *           NoteStore handle. (E.g. if the hostname is malformed.)
   */
  public static NoteStore.Client getNoteStoreClient(
      AuthenticationResult authResult,
      String userAgent) throws TTransportException {
    return getNoteStoreClient(authResult.getNoteStoreUrl(), userAgent, null);
  }  
  
  /**
   * Creates a NoteStore client interface that can be used to send requests to a
   * particular NoteStore shard. For example, the following would provide a
   * handle to make requests from the "MyClient" application to talk to the
   * Evernote server at "www.evernote.com" :
   *
   * <pre>
   *   NoteStore.Iface noteStore =
   *     EDAMUtil.getNoteStoreClient(&quot;https://www.evernote.com/shard/s1/notestore&quot;, &quot;MyClient (Java)&quot;);
   * </pre>
   *
   * This call does not actually initiate any communications with the NoteStore,
   * it only creates the handle that will be used.
   *
   * @param url
   *          the URL for the NoteStore HTTP service, which would be retrieved
   *          from the AuthenticationResponse.noteStoreUrl field after
   *          authentication
   * @param userAgent
   *          if non-null, this is the User-Agent string that will be provided
   *          on all HTTP requests to the service. This allows for better
   *          logging and debugging if problems arise.
   * @param customHeaders
   *          if non-null, this is a mapping of HTTP headers to values which
   *          will be included in the request.
   * @return The client interface that can be used to talk to the NoteStore
   * @throws TTransportException
   *           if the provided information can't be used to construct a
   *           NoteStore handle. (E.g. if the hostname is malformed.)
   */
  public static NoteStore.Client getNoteStoreClient(String url,
      String userAgent, Map<String, String> customHeaders)
  throws TTransportException {
    THttpClient transport = new THttpClient(url);
    if (customHeaders != null) {
      for (Entry<String, String> header : customHeaders.entrySet()) {
        transport.setCustomHeader(header.getKey(), header.getValue());
      }
    }
    if (userAgent != null) {
      transport.setCustomHeader("User-Agent", userAgent);
    }
    TBinaryProtocol protocol = new TBinaryProtocol(transport) {
      @Override
      public void readMessageEnd() {
        ((THttpClient)getTransport()).close();
      }
    };
    return new NoteStore.Client(protocol, protocol);
  }

  /**
   * Takes the provided byte array and converts it into a hexidecimal string
   * with two characters per byte.
   */
  public static String bytesToHex(byte[] bytes) {
    return bytesToHex(bytes, false);
  }

  /**
   * Takes the provided byte array and converts it into a hexidecimal string
   * with two characters per byte.
   *
   * @param withSpaces
   *          if true, this will put a space character between each hex-rendered
   *          byte, for readability.
   */
  public static String bytesToHex(byte[] bytes, boolean withSpaces) {
    StringBuilder sb = new StringBuilder();
    for (byte hashByte : bytes) {
      int intVal = 0xff & hashByte;
      if (intVal < 0x10) {
        sb.append('0');
      }
      sb.append(Integer.toHexString(intVal));
      if (withSpaces) {
        sb.append(' ');
      }
    }
    return sb.toString();
  }

  /**
   * Takes a string in hexidecimal format and converts it to a binary byte
   * array. This does no checking of the format of the input, so this should
   * only be used after confirming the format or origin of the string. The input
   * string should only contain the hex data, two characters per byte.
   */
  public static byte[] hexToBytes(String hexString) {
    byte[] result = new byte[hexString.length() / 2];
    for (int i = 0; i < result.length; ++i) {
      int offset = i * 2;
      result[i] = (byte) Integer.parseInt(hexString.substring(offset,
          offset + 2), 16);
    }
    return result;
  }

  /**
   * Encodes a string as a byte array using the default encoding.
   */
  public static byte[] stringToBytes(String string) {
    return stringToBytes(string, DEFAULT_CHARSET);
  }

  /**
   * Encodes a string as a byte array with a specified character set encoding.
   */
  public static byte[] stringToBytes(String string, String encoding) {
    return stringToBytes(string, getCharset(encoding));
  }

  /**
   * Encodes a string as a byte array with a specified character set encoding.
   */
  private static byte[] stringToBytes(String string, Charset charset) {
    if (string == null) {
      return null;
    }
    ByteBuffer encoded = charset.encode(string);
    byte[] result = new byte[encoded.remaining()];
    encoded.get(result, 0, result.length);
    return result;
  }

  /**
   * Encodes a CharBuffer as a byte array with the default encoding
   */
  public static byte[] charBufferToBytes(CharBuffer buffer) {
    return charBufferToBytes(buffer, DEFAULT_CHARSET);
  }
  
  /**
   * Encodes a CharBuffer as a byte array with the specified encoding
   */
  private static byte[] charBufferToBytes(CharBuffer buffer, Charset charset) {
    if (buffer == null) {
      return null;
    }
    ByteBuffer encoded = charset.encode(buffer);
    byte[] result = new byte[encoded.remaining()];
    encoded.get(result, 0, result.length);
    return result;
  }
  
  /**
   * Decodes a byte array as a string using the default encoding (UTF-8)
   */
  public static String bytesToString(byte[] bytes) {
    return bytesToString(bytes, DEFAULT_CHARSET);
  }

  /**
   * Decodes a byte array as a string using the specified character set
   * encoding.
   */
  public static String bytesToString(byte[] bytes, String encoding) {
    return bytesToString(bytes, getCharset(encoding));
  }

  /**
   * Decodes a byte array as a string using the specified character set
   * encoding.
   */
  private static String bytesToString(byte[] bytes, Charset charSet) {
    if (bytes == null) {
      return null;
    }
    return charSet.decode(ByteBuffer.wrap(bytes)).toString();
  }

  /**
   * Creates a Thrift Data object using the provided data blob.
   *
   * @param body
   *          the binary contents of the Data object to be created
   */
  public static Data bytesToData(byte[] body) {
    return bytesToData(body, true);
  }

  /**
   * Creates a Thrift Data object using the provided data blob.
   *
   * @param body
   *          the binary contents of the Data object to be created
   * @param includeBody
   *          if true, then the Data should contain the body bytes, otherwise it
   *          will just contain the metadata (hash, size) about the data.
   */
  public static Data bytesToData(byte[] body, boolean includeBody) {
    Data data = new Data();
    data.setSize(body.length);
    data.setBodyHash(hash(body));
    if (includeBody) {
      data.setBody(body);
    }
    return data;
  }

  /**
   * Returns an MD5 checksum of the provided array of bytes.
   */
  public static byte[] hash(byte[] body) {
    try {
      return MessageDigest.getInstance(EDAM_HASH_ALGORITHM).digest(body);
    } catch (NoSuchAlgorithmException e) {
      throw new EDAMUtilException(EDAM_HASH_ALGORITHM + " not supported", e);
    }
  }

  /**
   * Returns an MD5 checksum of the contents of the provided InputStream.
   */
  public static byte[] hash(InputStream in) throws IOException {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance(EDAM_HASH_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw new EDAMUtilException(EDAM_HASH_ALGORITHM + " not supported", e);
    }
    byte[] buf = new byte[1024];
    int n;
    while ((n = in.read(buf)) != -1) {
      digest.update(buf, 0, n);
    }
    return digest.digest();
  }
  
  /**
   * Returns an MD5 checksum of the provided string, which is encoded into UTF-8
   * format first for unambiguous hashing.
   */
  public static byte[] hash(String content) {
    return hash(stringToBytes(content));
  }

  /**
   * Converts content of edam.Data.body[] into a String. Assumes bytes contain
   * data encoded with UTF8 charset.
   */
  public static String dataToString(Data d) {
    return dataToString(d, DEFAULT_CHARSET);
  }

  /**
   * @param d
   *          A Data object with some sort of content.
   * @param charSet
   *          Character of Data.body. More info on charsets at
   *          http://java.sun.com/j2se/1.5.0/docs/api/java/nio/charset/Charset.html
   * @return Contents of Data.body[] as a string.
   * @throws UnsupportedEncodingException
   */
  public static String dataToString(Data d, String encoding) {
    return dataToString(d, getCharset(encoding));
  }

  /**
   * @param d
   *          A Data object with some sort of content.
   * @param charSet
   *          Character of Data.body. More info on charsets at
   *          http://java.sun.com/j2se/1.5.0/docs/api/java/nio/charset/Charset.html
   * @return Contents of Data.body[] as a string.
   * @throws UnsupportedEncodingException
   */
  private static String dataToString(Data d, Charset charSet) {
    byte[] dataBytes = d.getBody();
    if (null == d.getBody()) {
      return null;
    }
    return bytesToString(dataBytes, charSet);
  }

  /**
   * Creates a Thrift Data object using the provided String data value.
   *
   * @param body
   *          the body of the string to convert to binary form for the Data
   * @param includeBody
   *          if true, the Data object will include the binary encoded body
   *          itself, otherwise it will just contain metadata.
   */
  public static Data stringToData(String body, boolean includeBody) {
    return bytesToData(stringToBytes(body), includeBody);
  }

  /**
   * Creates a Thrift Data object using the provided String data value.
   *
   * @param body
   *          the body of the string to convert to binary form for the Data
   * @param charSet
   *          the character set to use for converting the data from String to
   *          binary
   * @param includeBody
   *          if true, the Data object will include the binary encoded body
   *          itself, otherwise it will just contain metadata.
   * @throws UnsupportedEncodingException
   */
  public static Data stringToData(String body, Charset charSet,
      boolean includeBody) throws UnsupportedEncodingException {
    return bytesToData(stringToBytes(body, charSet), includeBody);
  }

  /**
   * Creates a Thrift Data object using the provided String data value.
   *
   * @param body
   *          the body of the string to convert to binary form for the Data
   * @param charSet
   *          the character set to use for converting the data from String to
   *          binary
   * @param includeBody
   *          if true, the Data object will include the binary encoded body
   *          itself, otherwise it will just contain metadata.
   * @throws UnsupportedEncodingException
   */
  public static Data stringToData(String body, String charSet,
      boolean includeBody) throws UnsupportedEncodingException {
    return bytesToData(stringToBytes(body, charSet), includeBody);
  }

  /**
   * Private constructor ... this class should not be instantiated, just use the
   * static helper functions.
   */
  private EDAMUtil() {
  }

  /**
   * Throws an EDAMUserException with the provided parameter
   */
  public static EDAMUserException newUserException(EDAMErrorCode errorCode,
      String param) {
    EDAMUserException ex = new EDAMUserException(errorCode);
    if (param != null) {
      ex.setParameter(param);
    }
    return ex;
  }

  /**
   * Throws an EDAMSystemException with the provided message
   */
  public static EDAMSystemException newSystemException(EDAMErrorCode errorCode,
      String message) {
    EDAMSystemException ex = new EDAMSystemException(errorCode);
    if (message != null) {
      ex.setMessage(message);
    }
    return ex;
  }

  /**
   * Throws an EDAMNotFoundException with the provided identifier and key
   */
  public static EDAMNotFoundException newNotFoundException(String identifier,
      String key) {
    EDAMNotFoundException ex = new EDAMNotFoundException();
    ex.setIdentifier(identifier);
    ex.setKey(key);
    return ex;
  }

  private static final char[] HEX_DIGITS = new char[] {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'A', 'B', 'C', 'D', 'E', 'F'
  };

  /**
   * Implements URL encoding without relying on the default URLEncoder class,
   * since that does bad things with threads and semaphores.
   */
  public static String urlEncode(String s) {
    StringBuilder sb = new StringBuilder();
    byte[] bytes = stringToBytes(s);
    for (int i = 0; i < bytes.length; ++i) {
      byte b = bytes[i];
      if ((b >= 'A' && b <= 'Z') ||
          (b >= 'a' && b <= 'z') ||
          (b >= '0' && b <= '9') ||
          (b == '.') ||
          (b == '-') ||
          (b == '_')) {
        sb.append((char)b);
      } else {
        sb.append('%');
        sb.append(HEX_DIGITS[(b >> 4) & 0xf]);
        sb.append(HEX_DIGITS[b & 0xf]);
      }
    }
    return sb.toString();
  }

  /**
   * Returns the Java Charset that should be used for the provided encoding
   */
  public static Charset getCharset(String enc) {
    Charset charset = encodingToCharsetCache.get(enc);
    if (charset == null) {
        charset = Charset.forName(enc);
        encodingToCharsetCache.put(enc, charset);
    }
    return charset;
  }

}
