package com.example.musicplayer.adapter.albums;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.util.SongBitmapLoader;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> {

    private List<Album> mAlbumList = new ArrayList<>();
    private Context mContext;
    private SongRepository mSongRepository;
    private StartMusicListActivityInAlbum mStartMusicListActivityInAlbum;

    private Handler mHandler = new android.os.Handler();
    private SongBitmapLoader<AlbumHolder> mSongBitmapLoader;

    public AlbumAdapter(List<Album> albumList, Context context) {
        mAlbumList = albumList;
        mContext = context;
        mSongRepository = SongRepository.getSongRepository(mContext);
        mStartMusicListActivityInAlbum = (StartMusicListActivityInAlbum) context;

        mSongBitmapLoader = new SongBitmapLoader<>(mHandler);
        mSongBitmapLoader.start();
        mSongBitmapLoader.getLooper();

        mSongBitmapLoader.setSetBitMap(new SongBitmapLoader.ListenerSetBitMap<AlbumHolder>() {
            @Override
            public void listenerSet(AlbumHolder holder, Bitmap bitmap) {
                holder.bindBitMap(bitmap);
            }
        });
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

    class AlbumHolder extends RecyclerView.ViewHolder {

        private ImageView mCoverAlbum;
        private TextView mAlumName;
        private TextView mArtistName;
        private Album mAlbum;

        public AlbumHolder(@NonNull View itemView) {
            super(itemView);
            mCoverAlbum = itemView.findViewById(R.id.album_cover);
            mAlumName = itemView.findViewById(R.id.album_name);
            mArtistName = itemView.findViewById(R.id.artist_name_in_album_list);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent intent = MusicListActivity.newIntent(mContext,mAlbum,null);
//                    mContext.startActivity(intent);
                    mStartMusicListActivityInAlbum.startForAlbumCallBack(mAlbum);
                    mSongRepository.setIsMain(false);

                }
            });
        }

        public void onBindAlbum(Album album) {
            mAlbum = album;
            mAlumName.setText(mAlbum.getAlbumName());
            Song song = mSongRepository.getSongFromPath(mAlbum.getSongsOfAlbum().get(0));

            mArtistName.setText(song.getArtistName() + " | " + mAlbum.getSongsOfAlbum().size());
            mCoverAlbum.setImageResource(R.mipmap.ic_empty_cover_foreground);

            mSongBitmapLoader.messageCreator(this, song.getPath());
//            MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
//            mMediaMetadataRetriever.setDataSource(song.getPath());
//            byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
//            if (mPic!=null){
//                BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
//                bitmapFactory.outWidth = mCoverAlbum.getMaxWidth();
//                bitmapFactory.outHeight = mCoverAlbum.getMaxHeight();
//                Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
//                mCoverAlbum.setImageBitmap(songImage);
//            }else mCoverAlbum.setBackgroundResource(R.drawable.default_image_round);
        }

        private void bindBitMap(Bitmap bitmap) {
            mCoverAlbum.setImageBitmap(bitmap);
        }
    }


    public interface StartMusicListActivityInAlbum {
        void startForAlbumCallBack(Album album);
    }
}
