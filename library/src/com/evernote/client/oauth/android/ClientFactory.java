package com.evernote.client.oauth.android;

import com.evernote.client.conn.mobile.TEvernoteHttpClient;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.TTransportException;

import java.io.File;
import java.util.Map;

/**
 * A class to produce User and Note store clients.
 *
 * class created by @briangriffey
 */

public class ClientFactory {

    private static final String USER_AGENT_KEY = "User-Agent";

    private String mUserAgent;
    private Map<String, String> mCustomHeaders;
    private File mTempDir;

    public NoteStore.Client createNoteStore(String url) throws TTransportException {
        TEvernoteHttpClient transport =
                new TEvernoteHttpClient(url, mUserAgent, mTempDir);
        TBinaryProtocol protocol = new TBinaryProtocol(transport);
        return new NoteStore.Client(protocol, protocol);
    }

    /**
     * Creates a UserStore client interface that can be used to send requests to a
     * particular UserStore server. For example, the following would provide a
     * handle to make requests from the "MyClient" application to talk to the
     * Evernote server at "www.evernote.com" :
     * <p/>
     * <pre>
     * UserStore.Iface userStore = factory.createUserStore(&quot;www.evernote.com&quot;,
     *     &quot;MyClient (Java)&quot;);
     * </pre>
     * <p/>
     * This call does not actually initiate any communications with the UserStore,
     * it only creates the handle that will be used.
     *
     * @param url the hostname (or numeric IP address) for the server that we should
     *             communicate with. This will attempt to use HTTPS to talk to that
     *             server unless the hostname contains a port number component, in
     *             which case we'll use plaintext HTTP.
     * @param url  the hostname (or numeric IP address) for the server that we should
     *             communicate with. This will attempt to use HTTPS to talk to that
     *             server unless the hostname contains a port number component, in
     *             which case we'll use plaintext HTTP.
     * @return
     * @throws TTransportException
     */
    public UserStore.Client createUserStore(String url) throws TTransportException {
        return createUserStore(url, 0);
    }


    public UserStore.Client createUserStore(String serviceUrl, int port) throws TTransportException {
        String url = getFullUrl(serviceUrl, port);

        TEvernoteHttpClient transport =
                new TEvernoteHttpClient(url, mUserAgent, mTempDir);

        if (mCustomHeaders != null) {
            for (Map.Entry<String, String> header : mCustomHeaders.entrySet()) {
                transport.setCustomHeader(header.getKey(), header.getValue());
            }
        }
        if (mUserAgent != null) {
            transport.setCustomHeader(USER_AGENT_KEY, mUserAgent);
        }
        TBinaryProtocol protocol = new TBinaryProtocol(transport);
        return new UserStore.Client(protocol, protocol);
    }

    private String getFullUrl(String serviceUrl, int port) {
        String url = "";

        if (port != 0)
            serviceUrl += ":" + port;
        if (!serviceUrl.startsWith("http")) {
            url = serviceUrl.contains(":") ? "http://" : "https://";
        }

        url += serviceUrl + "/edam/user";

        return url;
    }


    public String getUserAgent() {
        return mUserAgent;
    }

    public void setUserAgent(String mUserAgent) {
        this.mUserAgent = mUserAgent;
    }

    /**
     * if non-null, this is a mapping of HTTP headers to values which
     * will be included in the request.
     */
    public Map<String, String> getCustomHeaders() {
        return mCustomHeaders;
    }

    public void setCustomHeaders(Map<String, String> mCustomHeaders) {
        this.mCustomHeaders = mCustomHeaders;
    }

    /**
     * a temporary directory in which large outgoing Thrift messages will
     * be cached to disk before they are sent
     */
    public File getTempDir() {
        return mTempDir;
    }

    public void setTempDir(File mTempDir) {
        this.mTempDir = mTempDir;
    }
}
