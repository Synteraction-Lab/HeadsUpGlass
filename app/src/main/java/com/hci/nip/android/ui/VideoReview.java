package com.hci.nip.android.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.hci.nip.android.BaseActivity;
import com.hci.nip.android.IntentActionType;
import com.hci.nip.android.util.FileUtil;
import com.hci.nip.glass.R;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class VideoReview extends BaseActivity {

    private static final String TAG = VideoReview.class.getName();
    public static final String VIDEO_NAME = "video.name";

    private Button backButton;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_review);

        configureUIElements();

        Intent intent = getIntent();
        playVideo(intent.getStringExtra(VIDEO_NAME));

    }

    private void configureUIElements() {
        backButton = findViewById(R.id.btnVideoReviewBack);
        videoView = findViewById(R.id.videoReview);

        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void playVideo(String videoName) {
        Log.i(TAG, "PlayVideo: " + videoName);
        //Creating MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.fromFile(new File(FileUtil.getAbsoluteFilePath(videoName))));
        videoView.requestFocus();
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        videoView.stopPlayback();
    }

    @Override
    public void onIntentReceive(Context context, IntentActionType intentActionType, Intent intent) {
        // DO NOTHING
    }

    @Override
    public List<IntentActionType> getIntentActionTypes() {
        return Collections.emptyList();
    }

}
