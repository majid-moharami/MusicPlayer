package com.example.musicplayer.controller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.fragment.SongsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.makeramen.roundedimageview.RoundedImageView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mViewPager2;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private View mIncludeLayout;
    private RoundedImageView mImageViewSongCover;
    private ImageView mImageButtonNext;
    private ImageView mImageButtonPerv;
    private ImageView mImageButtonPause;
    private TextView mTextViewArtist;
    private TextView mTextViewSongName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setSupportActionBar(mToolbar);
        createViewPager();
        configTabWithViewPager();
    }

    @SuppressLint("WrongViewCast")
    private void findViews() {
        mViewPager2 = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_bar);
        mToolbar  =findViewById(R.id.music_toolbar);
        mIncludeLayout  =findViewById(R.id.inclide_paly_bar);
        mImageButtonNext =findViewById(R.id.next_button);
        mImageButtonPerv =findViewById(R.id.per_button);
        mImageButtonPause =findViewById(R.id.puse_button);
        mImageViewSongCover =findViewById(R.id.image);
        mTextViewArtist = findViewById(R.id.artist_name);
        mTextViewSongName =findViewById(R.id.song_name);
    }


    private void configTabWithViewPager() {
        new TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0)
                    tab.setText("Songs");
                else if (position == 1)
                    tab.setText("Albums");
                else if (position == 2)
                    tab.setText("Artists");
            }
        }).attach();
    }
    private void createViewPager() {
        FragmentStateAdapter TaskAdapter = new ViewPagerAdapter(this);
        mViewPager2.setAdapter(TaskAdapter);
       // mViewPager2.setPageTransformer(new DepthPageTransformer());
    }

    private class ViewPagerAdapter extends FragmentStateAdapter{

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return SongsFragment.newInstance();
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}