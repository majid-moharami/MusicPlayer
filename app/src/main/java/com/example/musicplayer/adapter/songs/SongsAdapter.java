package com.example.musicplayer.adapter.songs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongHolder> {

    private List<Song> mSongList = new ArrayList<>();
    private PlayMusicCallback mPlayMusicCallback;
    private Context mContext;

    public SongsAdapter(List<Song> songList, PlayMusicCallback playMusicCallback, Context context) {
        mSongList = songList;
        mPlayMusicCallback = playMusicCallback;
        mContext = context;
    }

    public List<Song> getSongList() {
        return mSongList;
    }

    public void setSongList(List<Song> songList) {
        mSongList = songList;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.song_item_list, parent, false);
        return new SongHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        Song song = mSongList.get(position);
        holder.BindSong(song);
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public interface PlayMusicCallback {
        void onClick(UUID uuid) throws IOException;
    }

    public class SongHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mSongCoverImage;
        private TextView mSongTitle;
        private TextView mSongArtist;
        private Song mSong;

        public SongHolder(@NonNull View itemView) {
            super(itemView);

            mSongCoverImage = itemView.findViewById(R.id.cover_track);
            mSongTitle = itemView.findViewById(R.id.song_name_list_item);
            mSongArtist = itemView.findViewById(R.id.artist_name_list_item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mPlayMusicCallback.onClick(mSong.getUUID());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void BindSong(Song song) {
            mSong = song;
            mSongTitle.setText(mSong.getSongName());
            mSongArtist.setText(mSong.getArtistName());
            MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
            mMediaMetadataRetriever.setDataSource(song.getPath());
            byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
            if (mPic!=null){
                BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
                bitmapFactory.outWidth = mSongCoverImage.getMaxWidth();
                bitmapFactory.outHeight = mSongCoverImage.getMaxHeight();
                Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            songImage.compress(Bitmap.CompressFormat.PNG,50,out);
                mSongCoverImage.setImageBitmap(songImage);
            }else mSongCoverImage.setBackgroundResource(R.drawable.default_image_round);

        }
    }
}
