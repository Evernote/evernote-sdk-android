package com.evernote.client.oauth.android;

import android.util.Log;
import com.evernote.edam.userstore.BootstrapInfo;
import com.evernote.edam.userstore.BootstrapProfile;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The BootstrapManager provides access to check the current
 * {@link com.evernote.edam.userstore.Constants#EDAM_VERSION_MAJOR} and
 * the {@link com.evernote.edam.userstore.Constants#EDAM_VERSION_MINOR} against the Evernote Web serivice for API
 * Compatibility
 *
 * It provides access to the {@link List} of {@link BootstrapProfile} representing the possible server connections
 * for the user.  This list must be requested from the server on any type of authentication attempt
 */
public class BootstrapManager {

  private final String LOGTAG = "EvernoteSession";

  /**
   * List of locales that match china
   */
  private static List<Locale> sChinaLocales = Arrays.asList(new Locale[]{
      Locale.TRADITIONAL_CHINESE,
      Locale.CHINESE,
      Locale.CHINA,
      Locale.SIMPLIFIED_CHINESE});

  /**
   * Server matched name for BootstrapProfile that matches china
   */
  public static final String CHINA_PROFILE = "Evernote-China";

  /**
   * Display names for Yinxiang and Evernote
   */
  public static final String DISPLAY_YXBIJI = "印象笔记";
  public static final String DISPLAY_EVERNOTE = "Evernote";
  public static final String DISPLAY_EVERNOTE_INTL = "Evernote International";

  private ArrayList<String> mBootstrapServerUrls = new ArrayList<String>();
  private UserStore.Client mUserStoreClient;
  private Locale mLocale;
  private ClientFactory mClientProducer;

  protected String mBootstrapServerUsed;

  public BootstrapManager(EvernoteSession.EvernoteService service, ClientFactory producer) {
    this(service, producer, Locale.getDefault());
  }

  /**
   *
   * @param service {@link com.evernote.client.oauth.android.EvernoteSession.EvernoteService#PRODUCTION} when using
   * production and {@link com.evernote.client.oauth.android.EvernoteSession.EvernoteService#SANDBOX} when using sandbox
   * @param producer Client producer used to create clients
   * @param locale Used to detect if the china servers need to be checked
   */
  public BootstrapManager(EvernoteSession.EvernoteService service, ClientFactory producer, Locale locale) {
    mLocale = locale;
    mClientProducer = producer;

    mBootstrapServerUrls.clear();
    switch(service) {
      case PRODUCTION:
        if (sChinaLocales.contains(mLocale)) {
          mBootstrapServerUrls.add(EvernoteSession.HOST_CHINA);
        }
        mBootstrapServerUrls.add(EvernoteSession.HOST_PRODUCTION);
        break;

      case SANDBOX:
        mBootstrapServerUrls.add(EvernoteSession.HOST_SANDBOX);
        break;
    }
  }

  /**
   * Initialized the User Store to check for supported version of the API
   *
   * @throws ClientUnsupportedException on unsupported version
   * @throws Exception on generic errors
   */
  private void initializeUserStoreAndCheckVersion() throws Exception {

    int i = 0;
    String version = com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR + "."
        + com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR;

    for (String url : mBootstrapServerUrls) {
      i++;
      try {
        mUserStoreClient =  mClientProducer.createUserStore(url);//EvernoteUtil.getUserStoreClient(url, mUserAgent, mDataDir);

        if (!mUserStoreClient.checkVersion(mClientProducer.getUserAgent(),
            com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
            com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR)) {
          mUserStoreClient = null;
          throw new ClientUnsupportedException(version);
        }

        mBootstrapServerUsed = url;
        return;
      } catch (ClientUnsupportedException cue) {

        Log.e(LOGTAG, "Invalid Version", cue);
        throw cue;
      } catch (Exception e) {
        mUserStoreClient = null;
        if (i < mBootstrapServerUrls.size()) {
          Log.e(LOGTAG, "Error contacting bootstrap server=" + url, e);
          continue;
        } else {
          throw e;
        }
      }
    }
  }

  /**
   * Makes a web request to get the latest bootstrap information
   * This is a requirement during the oauth process
   *
   * @return {@link BootstrapInfoWrapper}
   * @throws Exception
   */
  public BootstrapInfoWrapper getBootstrapInfo() throws Exception {
    Log.d(LOGTAG, "getBootstrapInfo()");
    BootstrapInfo bsInfo = null;
    try {
      if (mUserStoreClient == null) {
        initializeUserStoreAndCheckVersion();
      }

      bsInfo = mUserStoreClient.getBootstrapInfo(mLocale.toString());
      printBootstrapInfo(bsInfo);

    } catch (ClientUnsupportedException cue) {
      throw cue;
    } catch (TException e) {
      Log.e(LOGTAG, "error getting bootstrap info", e);
    }

    BootstrapInfoWrapper wrapper = new BootstrapInfoWrapper(mBootstrapServerUsed, bsInfo);
    return wrapper;
  }

  /**
   * Log the {@link BootstrapProfile} list
   * @param bsInfo
   */
  public void printBootstrapInfo(BootstrapInfo bsInfo) {
    if (bsInfo == null) return;

    Log.d(LOGTAG, "printBootstrapInfo");
    List<BootstrapProfile> profiles = bsInfo.getProfiles();
    if (profiles != null) {
      for (BootstrapProfile profile : profiles) {
        Log.d(LOGTAG, profile.toString());
      }
    } else {
      Log.d(LOGTAG, "Profiles are null");
    }
  }

  /**
   * Wrapper class to hold the Evernote API server URL and the {@link BootstrapProfile} object
   */
  public static class BootstrapInfoWrapper {
    private String mServerUrl;
    private BootstrapInfo mBootstrapInfo;

    public BootstrapInfoWrapper(String serverUrl, BootstrapInfo info) {
      mServerUrl = serverUrl;
      mBootstrapInfo = info;
    }

    public String getServerUrl() {
      return mServerUrl;
    }

    public BootstrapInfo getBootstrapInfo() {
      return mBootstrapInfo;
    }
  }

  public static class ClientUnsupportedException extends Exception {
    public ClientUnsupportedException(String version) {
      super("Client version " + version + " not supported.");
    }
  };
}
