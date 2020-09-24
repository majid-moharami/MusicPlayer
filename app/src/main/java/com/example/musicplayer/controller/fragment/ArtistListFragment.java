package com.example.musicplayer.controller.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.albums.AlbumAdapter;
import com.example.musicplayer.adapter.artists.ArtistAdapter;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.repository.SongRepository;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ArtistListFragment extends Fragment {

    private FastScrollRecyclerView mRecyclerView;
    private SongRepository mRepository;
    private List<Artist> mArtistList=new ArrayList<>();
    private ArtistAdapter mAdapter;

    public static ArtistListFragment newInstance() {
        ArtistListFragment fragment = new ArtistListFragment();
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
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);
        findViews(view);
        mArtistList = mRepository.getAllArtist();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        updateUI();
        return view;

    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.artist_recycler);
    }
    private void updateUI() {

        if (mArtistList.size() == 0) {
            return;
        }
        if (mAdapter == null) {
            mAdapter = new ArtistAdapter(mArtistList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setArtists(mArtistList);
            mAdapter.notifyDataSetChanged();
        }
    }
}