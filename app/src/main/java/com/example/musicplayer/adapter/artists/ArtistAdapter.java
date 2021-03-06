package com.example.musicplayer.adapter.artists;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.albums.AlbumAdapter;
import com.example.musicplayer.controller.activity.MusicListActivity;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.util.PlayMusicRole;
import com.example.musicplayer.util.PriorityOfSongsList;
import com.example.musicplayer.util.SongBitmapLoader;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> {

    private List<Artist> mArtists = new ArrayList<>();
    private SongRepository mSongRepository;
    private StartMusicListActivity mStartMusicListActivity;
    private Context mContext;
    private Handler mHandler = new android.os.Handler();
    private SongBitmapLoader<ArtistHolder> mSongBitmapLoader;

    public ArtistAdapter(List<Artist> artists , Context context) {
        mContext =context;
        mArtists = artists;
        mSongRepository = SongRepository.getSongRepository(mContext);
        mStartMusicListActivity = (StartMusicListActivity) context;

        mSongBitmapLoader = new SongBitmapLoader<>(mHandler);
        mSongBitmapLoader.start();
        mSongBitmapLoader.getLooper();

        mSongBitmapLoader.setSetBitMap(new SongBitmapLoader.ListenerSetBitMap<ArtistHolder>() {
            @Override
            public void listenerSet(ArtistHolder holder, Bitmap bitmap) {
                holder.bindBitMap(bitmap);
            }
        });
    }

    public List<Artist> getArtists() {
        return mArtists;
    }

    public void setArtists(List<Artist> artists) {
        mArtists = artists;
    }

    @NonNull
    @Override
    public ArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.artist_list_item,parent,false);
        return new ArtistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistHolder holder, int position) {
        Artist artist = mArtists.get(position);
        holder.onBindArtist(artist);
    }

    @Override
    public int getItemCount() {
        return getArtists().size();
    }

    class ArtistHolder extends RecyclerView.ViewHolder {
        private RoundedImageView mCoverArtist;
        private TextView mArtistName;
        private TextView mTrackCount;
        private Artist mArtist;
        public ArtistHolder(@NonNull View itemView) {
            super(itemView);
            mCoverArtist = itemView.findViewById(R.id.cover_artist);
            mArtistName = itemView.findViewById(R.id.artist_name_artist_item);
            mTrackCount = itemView.findViewById(R.id.track_count);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStartMusicListActivity.startForArtistCallBack(mArtist);
                    mSongRepository.setIsMain(false);
                    mSongRepository.setMusicRole(PlayMusicRole.ARTIST);
                }
            });
        }

        public void onBindArtist(Artist artist){
            mArtist = artist;
            mArtistName.setText(mArtist.getArtistName());
            mTrackCount.setText(mArtist.getSongsOfArtist().size()+ " Track");
            MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
            Song song = mSongRepository.getSongFromPath(mArtist.getSongsOfArtist().get(0));

            mCoverArtist.setImageResource(R.mipmap.ic_empty_cover_foreground);

            mSongBitmapLoader.messageCreator(this , song.getPath());
//            mMediaMetadataRetriever.setDataSource(song.getPath());
//            byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
//            if (mPic!=null){
//                BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
//                bitmapFactory.outWidth = mCoverArtist.getMaxWidth();
//                bitmapFactory.outHeight = mCoverArtist.getMaxHeight();
//                Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
////            ByteArrayOutputStream out = new ByteArrayOutputStream();
////            songImage.compress(Bitmap.CompressFormat.PNG,50,out);
//                mCoverArtist.setImageBitmap(songImage);
//            }else mCoverArtist.setBackgroundResource(R.drawable.default_image_round);
        }
        private void bindBitMap(Bitmap bitmap){
            mCoverArtist.setImageBitmap(bitmap);
        }
    }

    public interface StartMusicListActivity{
        void startForArtistCallBack(Artist artist);
    }
}
