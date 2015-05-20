package com.evernote.android.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.evernote.android.demo.R;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.helper.Cat;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * @author rwondratschek
 */
@SuppressWarnings("FieldCanBeLocal")
public class ViewHtmlActivity extends AppCompatActivity {

    private static final Cat CAT = new Cat("ViewHtmlActivity");

    private static final String KEY_NOTE = "KEY_NOTE";
    private static final String KEY_HTML = "KEY_HTML";

    public static Intent createIntent(Context context, NoteRef note, String html) {
        Intent intent = new Intent(context, ViewHtmlActivity.class);
        intent.putExtra(KEY_NOTE, note);
        intent.putExtra(KEY_HTML, html);
        return intent;
    }

    private NoteRef mNoteRef;
    private String mHtml;

    private EvernoteHtmlHelper mEvernoteHtmlHelper;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_html);

        mNoteRef = getIntent().getParcelableExtra(KEY_NOTE);
        mHtml = getIntent().getStringExtra(KEY_HTML);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tb_text));

        setSupportActionBar(toolbar);

        if (!isTaskRoot()) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setTitle(mNoteRef.getTitle());

        final WebView webView = (WebView) findViewById(R.id.webView);

        if (savedInstanceState == null) {
            String data = "<html><head></head><body>" + mHtml + "</body></html>";

            webView.setWebViewClient(new WebViewClient() {

                @SuppressWarnings("deprecation")
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    try {
                        Response response = getEvernoteHtmlHelper().fetchEvernoteUrl(url);
                        WebResourceResponse webResourceResponse = toWebResource(response);
                        if (webResourceResponse != null) {
                            return webResourceResponse;
                        }

                    } catch (Exception e) {
                        CAT.e(e);
                    }

                    return super.shouldInterceptRequest(view, url);
                }
            });

            webView.loadDataWithBaseURL("", data, "text/html", "UTF-8", null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected WebResourceResponse toWebResource(Response response) throws IOException {
        if (response == null || !response.isSuccessful()) {
            return null;
        }

        String mimeType = response.header("Content-Type");
        String charset = response.header("charset");
        return new WebResourceResponse(mimeType, charset, response.body().byteStream());
    }

    protected EvernoteHtmlHelper getEvernoteHtmlHelper() throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        if (mEvernoteHtmlHelper == null) {
            EvernoteClientFactory clientFactory = EvernoteSession.getInstance().getEvernoteClientFactory();

            if (mNoteRef.isLinked()) {
                mEvernoteHtmlHelper = clientFactory.getLinkedHtmlHelper(mNoteRef.loadLinkedNotebook());
            } else {
                mEvernoteHtmlHelper = clientFactory.getHtmlHelperDefault();
            }
        }

        return mEvernoteHtmlHelper;
    }
}
