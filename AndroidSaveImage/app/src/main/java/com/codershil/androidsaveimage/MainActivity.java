package com.codershil.androidsaveimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    // demo for saving image in android q and above


    private Button btnSaveImage;
    private ImageView imgBG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSaveImage = findViewById(R.id.btnSaveImage);
        imgBG = findViewById(R.id.imgBG);

        btnSaveImage.setOnClickListener(view -> {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imgBG.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            saveImageToExternalStorage(bitmap);
        });
    }

    void saveImageToExternalStorage(Bitmap bitmap) {
        // output stream will help us to save image to external storage
        OutputStream outputStream;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ;
        // content provider have access to central repository . we use content resolver to get the access of content provider,
        // content resolver work as the client for content provider
        ContentResolver contentResolver = getContentResolver();
        // content values is like a row inside content resolver . it content contain necessary things needed to communicate with content provider
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "MyImage" + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + "MyFolder");

        //content URI is a uri which identifies the data in the provider
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            outputStream = contentResolver.openOutputStream(imageUri);
            // this method will put the image with given data with the given compress format and quality
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Toast.makeText(this, "image saved", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}