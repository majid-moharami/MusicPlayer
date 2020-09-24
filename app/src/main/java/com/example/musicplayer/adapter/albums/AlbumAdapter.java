package com.example.musicplayer.adapter.albums;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.songs.SongsAdapter;
import com.example.musicplayer.model.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> {

    private List<Album> mAlbumList = new ArrayList<>();

    public AlbumAdapter(List<Album> albumList) {
        mAlbumList = albumList;
    }

    public List<Album> getAlbumList() {
        return mAlbumList;
    }

    public void setAlbumList(List<Album> albumList) {
        mAlbumList = albumList;
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.album_list_item, parent, false);
        return new AlbumHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {
        Album album = mAlbumList.get(position);
        holder.onBindAlbum(album);
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

     static class AlbumHolder extends RecyclerView.ViewHolder{

        private ImageView mCoverAlbum;
        private TextView mAlumName;
        private TextView mArtistName;
        private Album mAlbum;
        public AlbumHolder(@NonNull View itemView) {
            super(itemView);
            mCoverAlbum = itemView.findViewById(R.id.album_cover);
            mAlumName = itemView.findViewById(R.id.album_name);
            mArtistName = itemView.findViewById(R.id.artist_name_in_album_list);
        }

        public void onBindAlbum(Album album){
            mAlbum = album;
            mAlumName.setText(mAlbum.getAlbumName());
            mArtistName.setText(mAlbum.getSongsOfAlbum().get(0).getArtistName() + " | "+mAlbum.getSongsOfAlbum().size());

            MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
            mMediaMetadataRetriever.setDataSource(mAlbum.getSongsOfAlbum().get(0).getPath());
            byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
            if (mPic!=null){
                BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
                bitmapFactory.outWidth = mCoverAlbum.getMaxWidth();
                bitmapFactory.outHeight = mCoverAlbum.getMaxHeight();
                Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            songImage.compress(Bitmap.CompressFormat.PNG,50,out);
                mCoverAlbum.setImageBitmap(songImage);
            }else mCoverAlbum.setBackgroundResource(R.drawable.default_image_round);
        }
    }
}
