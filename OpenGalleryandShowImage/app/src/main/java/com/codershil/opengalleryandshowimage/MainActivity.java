package com.codershil.opengalleryandshowimage;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    // demo for picking up photo from gallery and save it in another folder

    ActivityResultLauncher<String> galleryLauncher;
    private Button button, btnSaveImage;
    private ImageView image;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        btnSaveImage = findViewById(R.id.button2);
        image = findViewById(R.id.imageView);

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        image.setImageURI(uri);
                        imageUri = uri;
                    }
                });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });

        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting an bitmap from an uri
                // we can also get it directly from imageview
                try {
                    if (imageUri != null) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        saveImageToExternalStorage(bitmap);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "please open image from gallery first", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    void saveImageToExternalStorage(Bitmap bitmap) {
        // output stream will help us to save image to external storage
        OutputStream outputStream;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // content provider have access to central repository . we use content resolver to get the access of content provider,
            // content resolver work as the client for content provider
            ContentResolver contentResolver = getContentResolver();
            // content values is like a row inside content resolver . it content contain necessary things needed to communicate with content provider
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "MyImage1" + ".jpg");
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

}