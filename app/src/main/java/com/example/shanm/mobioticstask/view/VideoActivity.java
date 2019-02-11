package com.example.shanm.mobioticstask.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.shanm.mobioticstask.R;
import com.example.shanm.mobioticstask.modal.Modal;
import com.example.shanm.mobioticstask.mydatabase.MyDatabase;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "PlayerActivity";

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    String url, getVideoTitle, getVideoDesc, getVideoId;

    TextView videoTitleTextView;
    TextView videoDescTextView;

    private MyDatabase myDatabase;
    private SQLiteDatabase sqLiteDatabase;

    ProgressDialog dialog;
    ArrayList<Modal> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        playerView = findViewById(R.id.video_view);
        videoTitleTextView = findViewById(R.id.videoTitleTextView);
        videoDescTextView = findViewById(R.id.videoDescTextView);

        arrayList = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.setMessage("please wait...");
        dialog.setCancelable(false);
        loadVideos();

    }

    private void loadVideos() {

        dialog.show();
        final String url = "https://interview-e18de.firebaseio.com/media.json?print=pretty";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    final LinearLayout m_ll = (LinearLayout) findViewById(R.id.linearStyle);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String des = jsonObject.getString("description");
                        final String id = jsonObject.getString("id");
                        String thumb = jsonObject.getString("thumb");
                        String title = jsonObject.getString("title");
                        final String url1 = jsonObject.getString("url");

                        final LayoutInflater inflater = LayoutInflater.from(VideoActivity.this);
                        final View content = inflater.inflate(R.layout.video_style, null, false);
                        m_ll.addView(content);
                        ImageView thumnail = content.findViewById(R.id.imageView);
                        TextView videoTitle = content.findViewById(R.id.titleTv);
                        TextView descri = content.findViewById(R.id.descriptionTv);
                        Glide.with(VideoActivity.this).load(thumb).into(thumnail);
                        videoTitle.setText("" + title);
                        descri.setText("" + des);

                        String iddid = getIntent().getStringExtra("videoId");
                        if (id.equals(iddid)) {
                            m_ll.removeView(content);
                        }

                        thumnail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (id.equals(id)) {
                                    m_ll.removeView(content);
                                    releasePlayer();
                                } else if (player != null) {
                                    playbackPosition = player.getCurrentPosition();
                                    currentWindow = player.getCurrentWindowIndex();
                                    playWhenReady = player.getPlayWhenReady();
                                    player.release();
                                    player = null;
                                    //String id = getIntent().getStringExtra("videoId");
                                    myDatabase = new MyDatabase(VideoActivity.this);
                                    sqLiteDatabase = myDatabase.getWritableDatabase();
                                    Cursor c = sqLiteDatabase.rawQuery("select * from " + MyDatabase.TABLENAME, null);
                                    boolean res = c.moveToFirst();
                                    if (res) {
                                        do {
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put(MyDatabase.SEEkTime, "" + playbackPosition);
                                            sqLiteDatabase.update(MyDatabase.TABLENAME, contentValues, "id=" + id, null);
                                            Log.d("asasasa", "main:" + playbackPosition);

                                        } while (c.moveToPrevious());

                                    }
                                }
                                player = ExoPlayerFactory.newSimpleInstance(
                                        new DefaultRenderersFactory(VideoActivity.this),
                                        new DefaultTrackSelector(), new DefaultLoadControl());

                                playerView.setPlayer(player);

                                player.setPlayWhenReady(playWhenReady);
                                player.seekTo(currentWindow, playbackPosition);

                                Uri uri = Uri.parse(url1);
                                MediaSource mediaSource = buildMediaSource(uri);

                                ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(mediaSource);
                                player.prepare(concatenatingMediaSource, false, false);

                                videoTitleTextView.setText(getVideoTitle);
                                videoDescTextView.setText(getVideoDesc);
                            }
                        });

                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    dialog.dismiss();
                    Log.d("ssssssssss", "" + e.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(VideoActivity.this);
        requestQueue.add(stringRequest);

    }

    private void initializePlayer() {
        String id = getIntent().getStringExtra("videoId");
        url = getIntent().getStringExtra("videoUrl");
        getVideoTitle = getIntent().getStringExtra("videoTitle");
        getVideoDesc = getIntent().getStringExtra("videoDesc");

        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);

        myDatabase = new MyDatabase(this);
        sqLiteDatabase = myDatabase.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from MYTABLE where id=" + id, null);
        boolean resss = cursor.moveToFirst();
        if (resss) {
            String dada = cursor.getString(cursor.getColumnIndex("id"));
            String dadaaaa = cursor.getString(cursor.getColumnIndex("time"));

            if (dada.equals("" + id)) {
                player.setPlayWhenReady(playWhenReady);
                player.seekTo(currentWindow, Long.parseLong(dadaaaa));
                Uri uri = Uri.parse(url);
                MediaSource mediaSource = buildMediaSource(uri);
                player.prepare(mediaSource, false, false);
            } else {
                player.setPlayWhenReady(playWhenReady);
                player.seekTo(currentWindow, playbackPosition);

                Uri uri = Uri.parse(url);
                MediaSource mediaSource = buildMediaSource(uri);
                player.prepare(mediaSource, false, false);
            }
        }
        videoTitleTextView.setText(getVideoTitle);
        videoDescTextView.setText(getVideoDesc);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
            String id = getIntent().getStringExtra("videoId");
            myDatabase = new MyDatabase(this);
            sqLiteDatabase = myDatabase.getWritableDatabase();
            Cursor c = sqLiteDatabase.rawQuery("select * from " + MyDatabase.TABLENAME, null);
            boolean res = c.moveToFirst();
            if (res) {
                do {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MyDatabase.SEEkTime, "" + playbackPosition);
                    sqLiteDatabase.update(MyDatabase.TABLENAME, contentValues, "id=" + id, null);
                    Log.d("asasasa", "main:" + playbackPosition);

                } while (c.moveToPrevious());

            }
        }
    }
}
