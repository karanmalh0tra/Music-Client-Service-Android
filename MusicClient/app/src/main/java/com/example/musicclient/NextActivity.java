package com.example.musicclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musiccommon.SongInfo;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NextActivity extends AppCompatActivity{

    private static final String TAG = "NextActivity";

    //initially linear layout. will change based on user selection
    private String currentLayout = "LinearLayout";
    RVClickListener RVlistener;
    MediaPlayer mediaPlayer;
    public static List<SongInfo> allData = MainActivity.allData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        Log.i(TAG, "onCreate: started");


        RVlistener = new RVClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.i(TAG, "onClick: clicked on "+allData.get(position));
                Uri uri = Uri.parse(allData.get(position).getmSongUrl()); // missing 'http://' will cause crashed
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                context.startActivity(intent);
                try{
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } catch(Exception e){
                    e.printStackTrace();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mediaPlayer.setDataSource(getApplicationContext(), uri);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };
        initMyAdapter(currentLayout);
    }

    //initialize the adapter, compare whether the layout to be set is grid or list.
    private void initMyAdapter(String currentLayout){
        Log.i(TAG, "initMyAdapter: called");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Log.i(TAG, "initMyAdapter: inside initMyAdapter LinearLayout");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(MainActivity.allData, this,recyclerView.getLayoutManager(), RVlistener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}