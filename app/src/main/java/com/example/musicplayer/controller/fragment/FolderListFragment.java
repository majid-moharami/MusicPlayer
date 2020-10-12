package com.example.musicplayer.controller.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.folder.FolderAdapter;
import com.example.musicplayer.model.Folder;
import com.example.musicplayer.repository.SongRepository;

import java.util.List;

public class FolderListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SongRepository mRepository;
    private FolderAdapter mAdapter;
    private List<Folder> mFolders;

    public static FolderListFragment newInstance() {
        FolderListFragment fragment = new FolderListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepository = SongRepository.getSongRepository(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_folder_list, container, false);
        findViews(view);
        mFolders = mRepository.getAllFolder();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext() , 1));
        updateUI();
        return view;
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.folder_recycler_view);
    }

    private void updateUI(){
        if (mAdapter == null){
            mAdapter = new FolderAdapter(mFolders , getContext());
            mRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setFolderList(mFolders);
            mAdapter.notifyDataSetChanged();
        }
    }
}