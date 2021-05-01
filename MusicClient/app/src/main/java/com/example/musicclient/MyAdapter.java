package com.example.musicclient;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiccommon.SongInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private static final String TAG = "MyAdapter";

    public static List<SongInfo> allData = new ArrayList<>();
    private final Context mContext;
    private final RecyclerView.LayoutManager mLayoutManager;
    private RVClickListener RVlistener;
    MediaPlayer mediaPlayer;

    public MyAdapter(List<SongInfo> allData, Context mContext,RecyclerView.LayoutManager layoutManager, RVClickListener RVlistener) {
        this.allData = allData;
        this.mContext = mContext;
        this.mLayoutManager = layoutManager;
        this.RVlistener = RVlistener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View listView = inflater.inflate(R.layout.song_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listView, RVlistener);

        // listener to play the song.
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RVlistener.onClick(v, viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }


    //pass data to the viewholder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: called.");
        holder.image.setImageBitmap(allData.get(position).getmImage());
        holder.titleName.setText(allData.get(position).getmTitle());
        holder.artistName.setText(allData.get(position).getmArtist());

    }

    @Override
    public int getItemCount() {
        return allData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleName;
        public TextView artistName;
        public ImageView image;
        public RelativeLayout parentLayout;
        public View itemView;

        private RVClickListener listener;


        public ViewHolder(@NonNull View itemView, RVClickListener passedListener) {
            super(itemView);
            titleName = (TextView) itemView.findViewById(R.id.layout_title);
            artistName = (TextView) itemView.findViewById(R.id.layout_artist);
            image = (ImageView) itemView.findViewById(R.id.layout_image);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}

