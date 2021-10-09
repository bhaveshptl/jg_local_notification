package com.jg.localnotifcation.jg_local_notification;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.webkit.URLUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageAsync  extends AsyncTask<String, String, Bitmap> {
    private final static String TAG = "DownloadImageAsync";
    String imageUrl;

    public interface DownloadImageAsyncResponse {
        void onImageDownloaded(Bitmap output);
    }

    public DownloadImageAsyncResponse delegate = null;

    public DownloadImageAsync(String imageUrl,DownloadImageAsyncResponse delegate ) {
        this.imageUrl = imageUrl;
        this.delegate = delegate;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        try {
            boolean isValid= URLUtil.isValidUrl(imageUrl);
            if(isValid){
                URL url = new URL(imageUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                 bitmap = BitmapFactory.decodeStream(input);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        delegate.onImageDownloaded(bitmap);

    }
}
