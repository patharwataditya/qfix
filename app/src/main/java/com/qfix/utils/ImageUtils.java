package com.qfix.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.qfix.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static final int MAX_IMAGE_SIZE = 800; // Maximum width or height
    private static final int JPEG_QUALITY = 80; // JPEG quality percentage

    /**
     * Compresses a bitmap to a byte array with a maximum size
     */
    public static byte[] compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int quality = JPEG_QUALITY;
        
        // Resize bitmap if it's too large
        Bitmap resizedBitmap = resizeBitmap(bitmap);
        
        // Compress bitmap to JPEG
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        
        // Further reduce quality if the byte array is still too large
        while (outputStream.toByteArray().length > 800 * 1024 && quality > 10) { // 800KB max
            outputStream.reset();
            quality -= 10;
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        }
        
        return outputStream.toByteArray();
    }

    /**
     * Resizes a bitmap to fit within the maximum dimensions
     */
    private static Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        if (width <= MAX_IMAGE_SIZE && height <= MAX_IMAGE_SIZE) {
            return bitmap; // No resizing needed
        }
        
        float scale = Math.min((float) MAX_IMAGE_SIZE / width, (float) MAX_IMAGE_SIZE / height);
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    /**
     * Creates a temporary image file
     */
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    /**
     * Gets a Uri for a file using FileProvider
     */
    public static Uri getFileUri(Context context, File file) {
        return FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                file
        );
    }

    /**
     * Decodes a byte array into a Bitmap
     */
    public static Bitmap decodeByteArray(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    /**
     * Saves a bitmap to a file
     */
    public static File saveBitmapToFile(Context context, Bitmap bitmap, String fileName) {
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, fos);
            fos.close();
            return file;
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap to file", e);
            return null;
        }
    }
}