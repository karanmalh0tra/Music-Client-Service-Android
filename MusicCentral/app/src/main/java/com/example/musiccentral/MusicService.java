package com.example.musiccentral;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.example.musiccommon.IMyAidlInterface;
import com.example.musiccommon.SongInfo;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private static final String TAG = "MusicService";
    public String[] mTitles;
    public String[] mArtists;
    public String[] mSongUrls;
    public Bitmap[] mImages;
    int index = 0;
    List<SongInfo> allData = new ArrayList<>();
    private static final int NOTIFICATION_ID = 1;
    private Notification notification ;
    private static String CHANNEL_ID = "Music player style" ;

    @Override
    public void onCreate() {
        super.onCreate();

        //fetch data from arrays.xml
        mImages = new Bitmap[]{BitmapFactory.decodeResource(this.getResources(), R.drawable.jay_someday_the_way_i_do),
        BitmapFactory.decodeResource(this.getResources(), R.drawable.liqwyd_coral),
        BitmapFactory.decodeResource(this.getResources(), R.drawable.megaenx_recovery),
        BitmapFactory.decodeResource(this.getResources(), R.drawable.mixaund_inspiring_happy_morning),
        BitmapFactory.decodeResource(this.getResources(), R.drawable.peyruis_like_before),
        BitmapFactory.decodeResource(this.getResources(), R.drawable.purrple_cat_snooze_button)};
        mTitles = this.getResources().getStringArray(R.array.Titles);
        mArtists = this.getResources().getStringArray(R.array.Artists);
        mSongUrls = this.getResources().getStringArray(R.array.songURLs);

        for(int i = 0; i< mTitles.length; i++ ){
            allData.add(new SongInfo(mTitles[i],mArtists[i], mSongUrls[i], mImages[i]));
            Log.i(TAG, "onCreate: index is "+i+" and the object is "+allData.get(i));
        }
        // UB: Starting in Oreo notifications have a notification channel
        //     The channel defines basic properties of
        this.createNotificationChannel();

        // Create a notification area notification so the user

        final Intent notificationIntent = new Intent(getApplicationContext(),
                MusicService.class);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0) ;

        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true).setContentTitle("Music Service Client")
                .setContentText("Click does nothing")
                .setContentIntent(pendingIntent)
                .build();

        // Put this Service in a foreground state, so it won't
        // readily be killed by the system
        startForeground(NOTIFICATION_ID, notification);

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "Music player notification";
        String description = "The channel for music player notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription(description);
        }
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG,"Start Command was called");
        return START_NOT_STICKY;
    }


    //Implement 3 APs via AIDL
    private final IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {

        @Override
        public List<SongInfo> fetchAll() throws RemoteException {
            return allData;
        }

        @Override
        public SongInfo fetchOneSong(int index) throws RemoteException {
            return allData.get(index);
        }

        @Override
        public String songUrl(int index) throws RemoteException {
            return mSongUrls[index];
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"Service has been Un-binded");
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: Service has been binded");
        return mBinder;
    }
}