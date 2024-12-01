package com.example.registar.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

public class BitmapHelper {

    public static void processImageInBackground(Context context, ImageView imageView, Uri photoUri, boolean startNewThread) {
        if (startNewThread){
            ExecutorService executor = ExecutorHelper.getExecutor();
            executor.execute(() -> {
                try {
                    Bitmap scaledBitmap = getBitmapFromUri(context, photoUri, imageView.getWidth(), imageView.getHeight());
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        imageView.setImageBitmap(scaledBitmap);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                CameraHelper.photoURI = null;
            });
        }
        else {
            try {
                Bitmap scaledBitmap = getBitmapFromUri(context, photoUri, imageView.getWidth(), imageView.getHeight());
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    imageView.setImageBitmap(scaledBitmap);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            CameraHelper.photoURI = null;
        }
    }

    private static Bitmap getBitmapFromUri(Context context, Uri photoUri, int width, int height) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(photoUri);

        // Decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        if (inputStream != null)
            inputStream.close();

        // Calculate sample size
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode with inJustDecodeBounds=false to load the bitmap
        options.inJustDecodeBounds = false;
        inputStream = context.getContentResolver().openInputStream(photoUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        if (inputStream != null)
            inputStream.close();

        // Correct orientation
        //Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return correctBitmapOrientation(context, photoUri, bitmap);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (options.outHeight <= 0 || options.outWidth <= 0) {
            throw new IllegalArgumentException("Invalid image dimensions: " + options.outWidth + "x" + options.outHeight);
        }
        if (reqWidth <= 0 || reqHeight <= 0) {
            throw new IllegalArgumentException("Invalid requested dimensions: " + reqWidth + "x" + reqHeight);
        }

        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap correctBitmapOrientation(Context context, Uri photoUri, Bitmap bitmap) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(photoUri);
        if (inputStream != null) {
            ExifInterface exif = new ExifInterface(inputStream);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    // No rotation needed
                    break;
            }

            inputStream.close();
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        else
            return null;
    }

}
