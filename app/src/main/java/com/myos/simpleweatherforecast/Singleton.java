package com.myos.simpleweatherforecast;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Jacek on 2016-03-01.
 */
public class Singleton extends Application {
    private static Singleton sInstance = new Singleton();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static Singleton getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        //Volley
        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(this.mRequestQueue,
                new ImageLoader.ImageCache() {

                    private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(
                            10);

                    public void putBitmap(String url, Bitmap bitmap) {
                        mCache.put(url, bitmap);
                    }

                    public Bitmap getBitmap(String url) {
                        return mCache.get(url);
                    }
                });


    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }


}
