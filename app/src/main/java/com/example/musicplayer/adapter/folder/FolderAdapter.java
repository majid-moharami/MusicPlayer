package com.example.musicplayer.adapter.folder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.albums.AlbumAdapter;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Folder;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.util.PlayMusicRole;
import com.example.musicplayer.util.PriorityOfSongsList;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderHolder> {
    private List<Folder> mFolderList = new ArrayList<>();
    private Context mContext;
    private SongRepository mSongRepository;
    private StartMusicListActivityInFolder startMusicListActivityInFolder;
    public FolderAdapter(List<Folder> mFolderList , Context context) {
        this.mFolderList = mFolderList;
        mContext = context;
        mSongRepository = SongRepository.getSongRepository(mContext);
        startMusicListActivityInFolder = (StartMusicListActivityInFolder) context;
    }

    public void setFolderList(List<Folder> mFolderList) {
        this.mFolderList = mFolderList;
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.folder_list_item , parent , false);
        return new FolderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, int position) {
        Folder folder = mFolderList.get(position);
        holder.onBindFonder(folder);
    }

    @Override
    public int getItemCount() {
        return mFolderList.size();
    }


    class FolderHolder extends RecyclerView.ViewHolder{
        private Folder mFolder;
        private ImageView mImageView;
        private TextView mFolderName,mTrackCount;
        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView_folder);
            mFolderName = itemView.findViewById(R.id.textView_folder_name);
            mTrackCount = itemView.findViewById(R.id.textView_track_count_folder);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMusicListActivityInFolder.startForFolderCallBack(mFolder);
                    mSongRepository.setIsMain(false);
                    mSongRepository.setMusicRole(PlayMusicRole.FOLDER);
                }
            });
        }

        private void onBindFonder(Folder folder){
            mFolder = folder;
            mImageView.setImageResource(R.mipmap.ic_music_folder2_foreground);
            mFolderName.setText(mFolder.getFolderName());
            mTrackCount.setText(mFolder.getSongsFolderPath().size() + " tracks");
        }
    }

    public interface StartMusicListActivityInFolder {
        void startForFolderCallBack(Folder folder);
    }
}
