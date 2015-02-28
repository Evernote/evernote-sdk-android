package com.evernote.client.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.evernote.client.android.helper.Cat;
import com.evernote.client.android.helper.EvernotePreconditions;
import com.evernote.client.oauth.EvernoteAuthToken;
import com.evernote.client.oauth.YinxiangApi;
import com.evernote.edam.userstore.BootstrapInfo;
import com.evernote.edam.userstore.BootstrapProfile;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.List;

/**
 * A helper class to handle OAuth requests.
 *
 * @author rwondratschek
 */
@SuppressWarnings("UnusedDeclaration")
public class EvernoteOAuthHelper {

    protected static final String CALLBACK_SCHEME = "en-oauth";
    protected static final Cat CAT = new Cat("OAuthHelper");

    protected final EvernoteSession mSession;
    protected final String mConsumerKey;
    protected final String mConsumerSecret;
    protected final boolean mSupportAppLinkedNotebooks;

    protected BootstrapProfile mBootstrapProfile;
    protected OAuthService mOAuthService;

    protected Token mRequestToken;

    public EvernoteOAuthHelper(EvernoteSession session, String consumerKey, String consumerSecret, boolean supportAppLinkedNotebooks) {
        mSession = EvernotePreconditions.checkNotNull(session);
        mConsumerKey = EvernotePreconditions.checkNotEmpty(consumerKey);
        mConsumerSecret = EvernotePreconditions.checkNotEmpty(consumerSecret);
        mSupportAppLinkedNotebooks = supportAppLinkedNotebooks;
    }

    public void initialize() throws Exception {
        BootstrapProfile bootstrapProfile = fetchBootstrapProfile(mSession);
        initialize(EvernotePreconditions.checkNotNull(bootstrapProfile, "Bootstrap did not return a valid host"));
    }

    public void initialize(BootstrapProfile bootstrapProfile) {
        mBootstrapProfile = EvernotePreconditions.checkNotNull(bootstrapProfile);
        mOAuthService = createOAuthService(mBootstrapProfile, mConsumerKey, mConsumerSecret);
    }

    public Token createRequestToken() {
        mRequestToken = mOAuthService.getRequestToken();
        return mRequestToken;
    }

    public String createAuthorizationUrl(Token requestToken) {
        String url = mOAuthService.getAuthorizationUrl(requestToken);
        if (mSupportAppLinkedNotebooks) {
            url += "&supportLinkedSandbox=true";
        }

        return url;
    }

    public Intent startAuthorization(Activity activity) {
        if (mBootstrapProfile != null) {
            initialize(mBootstrapProfile);
        } else {
            try {
                initialize();
            } catch (Exception e) {
                CAT.e(e);
                return null;
            }
        }

        createRequestToken();
        String authorizationUrl = createAuthorizationUrl(mRequestToken);
        return EvernoteUtil.createAuthorizationIntent(activity, authorizationUrl, mSession.isForceAuthenticationInThirdPartyApp());
    }

    public boolean finishAuthorization(Activity activity, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return false;
        }

        String url = data.getStringExtra(EvernoteUtil.EXTRA_OAUTH_CALLBACK_URL);
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        Uri uri = Uri.parse(url);

        String verifierString = uri.getQueryParameter("oauth_verifier");
        String appLnbString = uri.getQueryParameter("sandbox_lnb");
        boolean isAppLinkedNotebook = !TextUtils.isEmpty(appLnbString) && "true".equalsIgnoreCase(appLnbString);

        if (TextUtils.isEmpty(verifierString)) {
            CAT.i("User did not authorize access");
            return false;
        }

        Verifier verifier = new Verifier(verifierString);
        try {
            EvernoteAuthToken evernoteAuthToken = new EvernoteAuthToken(mOAuthService.getAccessToken(mRequestToken, verifier), isAppLinkedNotebook);
            mSession.persistAuthenticationToken(activity, evernoteAuthToken, mBootstrapProfile.getSettings().getServiceHost());
            return true;

        } catch (Exception e) {
            CAT.e("Failed to obtain OAuth access token", e);
        }

        return false;
    }

    protected static BootstrapProfile fetchBootstrapProfile(EvernoteSession session) throws Exception {
        //Network request
        BootstrapManager.BootstrapInfoWrapper infoWrapper = session.getBootstrapSession().getBootstrapInfo();
        if (infoWrapper == null) {
            return null;
        }

        BootstrapInfo info = infoWrapper.getBootstrapInfo();
        if (info == null) {
            return null;
        }

        List<BootstrapProfile> profiles = info.getProfiles();
        if (profiles != null && !profiles.isEmpty()) {
            return profiles.get(0);
        } else {
            return null;
        }
    }

    protected static OAuthService createOAuthService(BootstrapProfile bootstrapProfile, String consumerKey, String consumerSecret) {
        String host = bootstrapProfile.getSettings().getServiceHost();
        if (host == null) {
            return null;
        }

        Uri uri = new Uri.Builder()
            .authority(host)
            .scheme("https")
            .build();

        Class<? extends Api> apiClass;
        switch (uri.toString()) {
            case EvernoteSession.HOST_SANDBOX:
                apiClass = EvernoteApi.Sandbox.class;
                break;

            case EvernoteSession.HOST_PRODUCTION:
                apiClass = EvernoteApi.class;
                break;

            case EvernoteSession.HOST_CHINA:
                apiClass = YinxiangApi.class;
                break;

            default:
                throw new IllegalArgumentException("Unsupported Evernote host: " + host);
        }

        return new ServiceBuilder()
            .provider(apiClass)
            .apiKey(consumerKey)
            .apiSecret(consumerSecret)
            .callback(CALLBACK_SCHEME + "://callback")
            .build();
    }
}
