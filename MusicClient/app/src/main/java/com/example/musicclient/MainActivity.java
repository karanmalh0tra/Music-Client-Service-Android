package com.example.musicclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musiccommon.IMyAidlInterface;
import com.example.musiccommon.SongInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    Button bindButton, unbindButton,fetchAllButton;
    ImageView imageView;
    TextView textView3, textView4;
    Spinner spin1, spin2;
    int flag = 0;

    public static String[] mTitles;
    public static String[] mArtists;
    public static String[] mSongUrls;
    public static Bitmap[] mImages;
    int index = 0;
    public static List<SongInfo> allData = new ArrayList<>();
    public static SongInfo oneSong;
    public static String songURL;

    private static final String TAG = "MainActivity";
    protected static final int PERMISSION_REQUEST = 0;
    boolean isServiceStarted = false;
    boolean mIsBound = false;
    int hugeSize = 0;
    protected IMyAidlInterface mIMyAidlInterface = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Fetching all the resource IDs
        bindButton = (Button) findViewById(R.id.bind);
        unbindButton = (Button) findViewById(R.id.unbind);
        fetchAllButton = (Button) findViewById(R.id.FetchAll);
        spin1 = (Spinner) findViewById(R.id.spinner);
        spin2 = (Spinner) findViewById(R.id.spinner2);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);

        //inital Locking since the service is unbounded
        imageView.setImageBitmap(null);
        textView3.setText("");
        textView4.setText("");
        spin1.setEnabled(false);
        spin1.setClickable(false);
        spin2.setEnabled(false);
        spin2.setClickable(false);
        fetchAllButton.setEnabled(false);
        unbindButton.setEnabled(false);

        //Binds if not bounded
        bindButton.setOnClickListener(v -> {
            checkBindingAndBind();
        });

        //unbinds if bounded
        unbindButton.setOnClickListener(v -> {
            onUnBindingService();
        });


        fetchAllButton.setOnClickListener(v -> {
                if (mIsBound) {
                    //just logs to check if data was successfully received or not
                    for (int i = 0; i < allData.size(); i++){
                        Log.i(TAG, "onCreate: Artist "+ allData.get(i).getmArtist());
                        Log.i(TAG, "onCreate: Title "+ allData.get(i).getmTitle());
                        Log.i(TAG, "onCreate: Url "+ allData.get(i).getmSongUrl());
                    }
                    Log.i(TAG, "onCreate: mTitles "+mTitles);

                    Intent intent = new Intent(getApplicationContext(), NextActivity.class);
                    startActivity(intent);


                } else {
                    Log.i(TAG, "Karan says that the service was not bound!");
                }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //Bind Service if not Bound
    protected void checkBindingAndBind() {
        if (!mIsBound) {

            boolean b = false;
            Intent i = new Intent(IMyAidlInterface.class.getName());

            ResolveInfo info = getPackageManager().resolveService(i, 0);
            Log.i(TAG, "checkBindingAndBind: info is"+info);
            i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            b = bindService(i, mConnection, Context.BIND_AUTO_CREATE);
            Log.i(TAG, "checkBindingAndBind: bindService returned "+b);
            if (b) {
                Log.i(TAG, "Karan says bindService() succeeded!");
            } else {
                Log.i(TAG, "Karan says bindService() failed!");
            }
        } else {
            Log.i(TAG, "checkBindingAndBind: bind already exists");
        }
    }

    //UnBind Service and Lock Buttons
    public void onUnBindingService(){
        if (mIsBound) {
            unbindService(mConnection);
        }
        mIMyAidlInterface = null;
        bindButton.setEnabled(true);
        imageView.setImageBitmap(null);
        textView3.setText("");
        textView4.setText("");
        spin1.setEnabled(false);
        spin1.setClickable(false);
        spin2.setEnabled(false);
        spin2.setClickable(false);
        fetchAllButton.setEnabled(false);
        unbindButton.setEnabled(false);
        try{
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch(Exception e){
            e.printStackTrace();
        }

        mIsBound = false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        //Executed after service is Bound
        public void onServiceConnected(ComponentName className, IBinder service) {

            mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            mIsBound = true;
            try {
                allData = mIMyAidlInterface.fetchAll();
                Log.i(TAG, "onCreate: " + allData.size());
                mTitles=new String[allData.size()+1];
                int i = 0;
                for(i = 0; i<allData.size(); i++){
                    mTitles[i] = allData.get(i).getmTitle();
                }
                mTitles[i] = "Select a Song";
                hugeSize = i;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            bindButton.setEnabled(false);
            unbindButton.setEnabled(true);
            fetchAllButton.setEnabled(true);
            onPopulateSpinner1();
            onPopulateSpinner2();


            Toast.makeText(MainActivity.this, "Service Connected", Toast.LENGTH_SHORT).show();
        }


        //Executed after service is unbounded.
        public void onServiceDisconnected(ComponentName className) {

            mIMyAidlInterface = null;
            bindButton.setEnabled(true);
            imageView.setImageBitmap(null);
            textView3.setText("");
            textView4.setText("");
            spin1.setEnabled(false);
            spin1.setClickable(false);
            spin2.setEnabled(false);
            spin2.setClickable(false);
            fetchAllButton.setEnabled(false);
            unbindButton.setEnabled(false);
            try{
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch(Exception e){
                e.printStackTrace();
            }

            mIsBound = false;
            Log.i(TAG, "onServiceDisconnected: ");
            Toast.makeText(MainActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();

        }
    };



    //Listener and Logic for Spinner Number 2 that displays all the information about the song
    public void onPopulateSpinner2(){
        Log.i(TAG, "onCreate: mTitles" + mTitles);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mTitles);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin2.setEnabled(true);
        spin2.setClickable(true);
        spin2.setAdapter(aa);
        spin2.setSelection(hugeSize, false);
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position<allData.size()){
                    //Using the fetch one song API
                    try {
                        oneSong = mIMyAidlInterface.fetchOneSong(position);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "onItemSelected: "+oneSong.getmTitle());
                    Log.i(TAG, "onItemSelected: "+oneSong.getmArtist());
                    imageView.setImageBitmap(oneSong.getmImage());
                    textView3.setText(oneSong.getmTitle());
                    textView4.setText(oneSong.getmArtist());
                } else {
                    imageView.setImageBitmap(null);
                    textView3.setText("");
                    textView4.setText("");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    //Logic for the Spinners that fetches the URL and plays the song
    public void onPopulateSpinner1(){
        Log.i(TAG, "onCreate: mTitles" + mTitles);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mTitles);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin1.setEnabled(true);
        spin1.setClickable(true);
        spin1.setAdapter(aa);
        spin1.setSelection(hugeSize, false);
        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position < allData.size()){
                    try {
                        //Call songUrl API
                        songURL = mIMyAidlInterface.songUrl(position);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "onItemSelected: called");


                    Toast.makeText(getApplicationContext(),"Song Downloading. Please Wait" , Toast.LENGTH_LONG).show();
                    Uri uri = Uri.parse(songURL);
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
                                mediaPlayer.setDataSource(String.valueOf(uri));
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    if(mediaPlayer != null){
                        try{
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    //Stop Media Player if playing. Else, Just catch the exception.
    @Override
    protected void onStop(){
        super.onStop();
        if(mediaPlayer != null){
            try{
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }


    //unbind service on Destroy
    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (mIsBound) {
            unbindService(mConnection);
        }
    }
}