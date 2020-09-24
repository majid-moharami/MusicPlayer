package com.example.musicplayer.controller.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.albums.AlbumAdapter;
import com.example.musicplayer.adapter.songs.SongsAdapter;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.repository.SongRepository;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AlbumListFragment extends Fragment {

    private FastScrollRecyclerView mRecyclerView;
    private SongRepository mRepository;
    private List<Album> mAlbumList=new ArrayList<>();
    private AlbumAdapter mAdapter;

    public static AlbumListFragment newInstance() {
        AlbumListFragment fragment = new AlbumListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepository = SongRepository.getSongRepository(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
        findViews(view);
        mAlbumList = mRepository.getAllAlbum();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        updateUI();
        return view;
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.album_recycler);
    }

    private void updateUI() {

        if (mAlbumList.size() == 0) {
            return;
        }
        if (mAdapter == null) {
            mAdapter = new AlbumAdapter(mAlbumList,getActivity());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setAlbumList(mAlbumList);
            mAdapter.notifyDataSetChanged();
        }
    }
}