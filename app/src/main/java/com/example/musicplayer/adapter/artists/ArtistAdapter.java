package com.example.musicplayer.adapter.artists;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Artist;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> {

    private List<Artist> mArtists = new ArrayList<>();

    public ArtistAdapter(List<Artist> artists) {
        mArtists = artists;
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
        }

        public void onBindArtist(Artist artist){
            mArtist = artist;
            mArtistName.setText(mArtist.getArtistName());
            mTrackCount.setText(mArtist.getSongsOfArtist().size()+ " Track");
            MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
            mMediaMetadataRetriever.setDataSource(mArtist.getSongsOfArtist().get(0).getPath());
            byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
            if (mPic!=null){
                BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
                bitmapFactory.outWidth = mCoverArtist.getMaxWidth();
                bitmapFactory.outHeight = mCoverArtist.getMaxHeight();
                Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            songImage.compress(Bitmap.CompressFormat.PNG,50,out);
                mCoverArtist.setImageBitmap(songImage);
            }else mCoverArtist.setBackgroundResource(R.drawable.default_image_round);
        }
    }
}
