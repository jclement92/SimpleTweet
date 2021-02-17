package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;

    EditText etCompose;
    Button btnTweet;
    TextInputLayout tilCompose;

    TwitterClient client;

    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        fm = getSupportFragmentManager();

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tilCompose = findViewById(R.id.tilCompose);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnTweet.setEnabled(etCompose.length() != 0 && etCompose.length() <= 140);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        client = TwitterApp.getRestClient(this);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    // Could also use snackbar
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    // Could also use snackbar
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_SHORT).show();
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });

        BufferedReader input;
        try {
            input = new BufferedReader(new InputStreamReader(openFileInput("filename.txt")));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = input.readLine()) != null) {
                builder.append(line).append("\n");
            }
            String text = builder.toString();
            etCompose.setText(text);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                // Make sure to check whether returned data will be null.
                String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);

                etCompose.setText(urlOfPage);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (btnTweet.isEnabled()) {
            showAlertDialog();
        } else {
            finish();
        }
    }

    private void showAlertDialog() {
        SaveTweetFragment alertDialog = SaveTweetFragment.newInstance("Save draft?", etCompose.getText().toString());
        alertDialog.show(fm, "fragment_alert");
    }
}