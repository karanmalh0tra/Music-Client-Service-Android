package com.example.musiccommon;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class SongInfo implements Parcelable {
    private String mTitle, mArtist, mSongUrl;
    private Bitmap mImage;
    public SongInfo(String mTitles, String mArtists, String mSongUrls, Bitmap mImages){
        this.mTitle = mTitles;
        this.mArtist = mArtists;
        this.mSongUrl = mSongUrls;
        this.mImage = mImages;
    }

    public SongInfo(Parcel in) {
        mTitle = in.readString();
        mArtist = in.readString();
        mSongUrl = in.readString();
        mImage = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mArtist);
        dest.writeString(mSongUrl);
        dest.writeParcelable(mImage, flags);
    }

    //Getters
    public String getmTitle() {
        return mTitle;
    }

    public String getmArtist() {
        return mArtist;
    }

    public String getmSongUrl() {
        return mSongUrl;
    }

    public Bitmap getmImage() {
        return mImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SongInfo> CREATOR = new Creator<SongInfo>() {
        @Override
        public SongInfo createFromParcel(Parcel in) {
            return new SongInfo(in);
        }

        @Override
        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };
}
