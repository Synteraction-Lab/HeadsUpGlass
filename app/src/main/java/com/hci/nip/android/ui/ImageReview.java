package com.hci.nip.android.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.hci.nip.android.BaseActivity;
import com.hci.nip.android.IntentActionType;
import com.hci.nip.android.util.FileUtil;
import com.hci.nip.glass.R;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ImageReview extends BaseActivity {

    private static final String TAG = ImageReview.class.getName();

    public static final String IMAGE_NAME = "image.name";
    public static final String IMAGE_POST_VIEW_MILLIS = "image.post.view.millis";

    private static final int DEFAULT_POST_VIEW_MILLIS = 1000;

    private Button backButton;
    private ImageView imageView;
    private CountDownTimer countDownTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_review);

        configureUIElements();

        Intent intent = getIntent();
        displayImage(intent.getStringExtra(IMAGE_NAME), intent.getLongExtra(IMAGE_POST_VIEW_MILLIS, DEFAULT_POST_VIEW_MILLIS));
    }

    private void configureUIElements() {
        backButton = findViewById(R.id.btnImageReviewBack);
        imageView = findViewById(R.id.imageReview);

        backButton.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            finish();
        });
    }


    private void displayImage(String imageName, long millis) {
        Log.i(TAG, "DisplayImage: " + imageName);
        imageView.setImageURI(Uri.fromFile(new File(FileUtil.getAbsoluteFilePath(imageName))));

        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // DO NOTHING
            }

            public void onFinish() {
                backButton.performClick();
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
