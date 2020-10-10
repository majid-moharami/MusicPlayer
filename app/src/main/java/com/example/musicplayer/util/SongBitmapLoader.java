package com.example.musicplayer.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ConcurrentHashMap;

public class SongBitmapLoader<T> extends HandlerThread {

    public static final int MESSAGE_LOAD_BITMAP = 0;
    public static final String TAG = "SongBitmapLoader";
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ConcurrentHashMap<T , String> mHashMap = new ConcurrentHashMap<>();
    private ListenerSetBitMap mSetBitMap ;
    
    public SongBitmapLoader(Handler handler) {
        super(TAG);
        mResponseHandler = handler;
    }

    public void setSetBitMap(ListenerSetBitMap mSetBitMap) {
        this.mSetBitMap = mSetBitMap;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new Handler(){

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_LOAD_BITMAP){
                    final T holder = (T) msg.obj;
                    final String path = mHashMap.get(holder);

                    MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
                    mMediaMetadataRetriever.setDataSource(path);
                    final byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
                    if (mPic != null){
                        final BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
                        bitmapFactory.outWidth =50;
                        bitmapFactory.outHeight = 50;
                        final Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
                        mResponseHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mSetBitMap.listenerSet(holder , songImage);
                            }
                        });
                    }


                }
            }
        };
    }

    public void messageCreator(T holder , String path){
        mHashMap.put(holder , path);
        mRequestHandler.obtainMessage(MESSAGE_LOAD_BITMAP , holder).sendToTarget();
    }

    public interface ListenerSetBitMap<T>{
        void listenerSet(T holder , Bitmap bitmap);
    }
}
