package com.evernote.android.demo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.evernote.android.demo.R;
import com.evernote.android.demo.task.BaseTask;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.notestore.SyncState;
import com.evernote.edam.type.User;

import net.vrallev.android.task.TaskResult;

/**
 * @author rwondratschek
 */
public class UserInfoFragment extends Fragment {

    private static final String KEY_USER = "KEY_USER";

    public static UserInfoFragment create(User user) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_USER, user);

        UserInfoFragment fragment = new UserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private User mUser;

    private SeekBar mSeekBarUpload;
    private TextView mTextViewCurrentUpload;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (User) getArguments().getSerializable(KEY_USER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        mSeekBarUpload = (SeekBar) view.findViewById(R.id.seekBar);
        mTextViewCurrentUpload = (TextView) view.findViewById(R.id.textView_upload_current);

        // disable seek, dirty hack
        mSeekBarUpload.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int mCurrentProgress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekBar.setProgress(mCurrentProgress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mCurrentProgress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        TextView textViewEmail = (TextView) view.findViewById(R.id.textView_email);
        if (TextUtils.isEmpty(mUser.getEmail())) {
            textViewEmail.setText(getString(R.string.empty, "email"));
        } else {
            textViewEmail.setText(mUser.getEmail());
        }

        mSeekBarUpload.setMax((int) (mUser.getAccounting().getUploadLimit() / 1_000));

        TextView textViewLimit = (TextView) view.findViewById(R.id.textView_upload_max);
        setUploadValue(textViewLimit, mUser.getAccounting().getUploadLimit());

        if (savedInstanceState == null) {
            new GetSyncStateTask().start(this);
        }

        return view;
    }

    @TaskResult
    public void onSyncState(SyncState syncState) {
        mSeekBarUpload.setProgress((int) (syncState.getUploaded() / 1_000));
        setUploadValue(mTextViewCurrentUpload, syncState.getUploaded());
    }

    private void setUploadValue(TextView textView, long limit) {
        if (limit > 1_000_000) {
            float value = limit / 1_000_000f;
            textView.setText(getString(R.string.mega_bytes, value));
        } else {
            float value = limit / 1_000f;
            textView.setText(getString(R.string.kilo_bytes, value));
        }
    }

    private static class GetSyncStateTask extends BaseTask<SyncState> {

        public GetSyncStateTask() {
            super(SyncState.class);
        }

        @Override
        protected SyncState checkedExecute() throws Exception {
            return EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient().getSyncState();
        }
    }
}
