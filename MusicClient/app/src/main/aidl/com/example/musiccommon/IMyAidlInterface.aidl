package com.example.musiccommon;
import com.example.musiccommon.IMyAidlSongInfo;

interface IMyAidlInterface {
    List<SongInfo> fetchAll();
    SongInfo fetchOneSong(int index);
    String songUrl(int index);
}