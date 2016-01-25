package com.example.menigo_m.epiandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by lopes_n on 1/25/16.
 */
public class ImageIntra extends AsyncTask<String, Void, Bitmap> {
    ImageView image;

    public ImageIntra(ImageView img) {
        image = img;
    }

    protected Bitmap doInBackground(String... args) {
        String img = args[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(img).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        image.setImageBitmap(result);
    }
}