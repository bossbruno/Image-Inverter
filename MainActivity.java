package com.example.invertedimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

Drawable de;
Bitmap btmp;
ImageView  myimg;
Uri imageurl;
    Bitmap newpic;

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myimg = (ImageView)findViewById(R.id.invert);
        }

    //Open Gallery method
    public void OpenGallery(View v){
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            assert data != null;
            imageurl = data.getData();


            try {
                myimg.setImageURI(imageurl);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //method to call invert with a buton

    public void Invert(View v)  {

        de = myimg.getDrawable();
        btmp = ((BitmapDrawable)de).getBitmap();
       newpic = invertImage(btmp);
        myimg.setImageBitmap(newpic);
    }


    public void SavetoGallery(View v) throws FileNotFoundException {
        saveImage(newpic,"Inverted image");
    }

    private void saveImage(Bitmap bitmap, @NonNull String name) throws FileNotFoundException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "Inverted Image");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + "DoubleTap";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(image);

        }
        try {
            saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this,"Image Saved" , Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            Toast.makeText(this,"Image Download Failed" + e.toString() , Toast.LENGTH_SHORT).show();

        }

    }


    // Method to Invert a bitmap image

    public static Bitmap invertImage(Bitmap original)
    {
        Bitmap finalImage = Bitmap.createBitmap(original.getWidth(),original.getHeight(),original.getConfig());
        int A,R,G,B;
        int  pixelColorr;

        int height = original.getHeight();
        int width = original.getWidth();

        for (int y = 0 ; y <height; y++){
            for (int x = 0 ; x<width; x++){
                pixelColorr = original.getPixel(x,y);
                A = Color.alpha(pixelColorr);
                R = 255-Color.red(pixelColorr);
                G = 255-Color.green(pixelColorr);
                B = 255-Color.blue(pixelColorr);

                finalImage.setPixel(x,y,Color.argb(A,R,G,B));




            }
        }
        return finalImage;
    }
}