package com.example.adhish.downloadretriveimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    CoordinatorLayout coordinatorLayout;
    FloatingActionButton btnSelectImage;
    AppCompatImageView imgView;

    DBHelper dbHelper;

//    String imgurl="https://cdn1.truelancer.com/user-picture/101448-57ebbe2a80c84.jpg";

    String imgurl="http://www.appinessworld.com/images/career/slider-1.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the views...
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        btnSelectImage = (FloatingActionButton) findViewById(R.id.btnSelectImage);
        imgView = (AppCompatImageView) findViewById(R.id.imgView);

        btnSelectImage.setOnClickListener(this);

        // Create the Database helper object
        dbHelper = new DBHelper(this);

    }

    // Show simple message using SnackBar
    void showMessage(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        if(!loadImageFromDB(imgurl)) {
            new GetImages(imgurl).execute();
        }
        else
        {
            loadImageFromDB(imgurl);
        }
    }

    // Save the
    Boolean saveImageInDB(byte[] byteArray,String url) {

        try {
            dbHelper.open();
            byte[] inputData = byteArray;
            dbHelper.insertImage(inputData,url);
            dbHelper.close();
            return true;
        } catch (Exception ioe) {
            Log.e(TAG, "<saveImageInDB> Error : " + ioe.getLocalizedMessage());
            dbHelper.close();
            return false;
        }

    }

    Boolean loadImageFromDB(String url) {
        try {
            dbHelper.open();
            byte[] bytes = dbHelper.retreiveImageFromDB(url);
            dbHelper.close();
            // Show Image from DB in ImageView
            showMessage("Image Loaded from Database...");
            imgView.setImageBitmap(Utils.getImage(bytes));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "<loadImageFromDB> Error : " + e.getLocalizedMessage());
            dbHelper.close();
            return false;
        }
    }

    private class GetImages extends AsyncTask<Object, Object, Object> {
        private String requestUrl;
        private Bitmap bitmap;
        private GetImages(String requestUrl) {
            this.requestUrl = requestUrl;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            try {
                URL url = new URL(requestUrl);
                URLConnection conn = url.openConnection();
                bitmap = BitmapFactory.decodeStream(conn.getInputStream());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                saveImageInDB(byteArray,requestUrl);

                Log.d(TAG,"Loading from web");

            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            loadImageFromDB(imgurl);
        }
    }
}
