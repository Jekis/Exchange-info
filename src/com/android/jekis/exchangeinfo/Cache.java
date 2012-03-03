package com.android.jekis.exchangeinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

public class Cache {

    /**
     * Get data from cache.
     * If cache does not exists default value will be returned.
     * 
     * @param context
     *        Application context.
     * @param cacheId
     *        Filename from which data will be retrieved.
     * @param defaultValue
     *        Default value to be returned.
     */
    public static String get(Context context, String cacheId, String defaultValue) {
        FileInputStream fis;
        String data = defaultValue;

        try {
            fis = context.getApplicationContext().openFileInput(cacheId);
            try {
                // Prepare the file for reading.
                InputStreamReader inputreader = new InputStreamReader(fis);
                BufferedReader buffreader = new BufferedReader(inputreader);
                StringBuilder dataB = new StringBuilder();
                String line;
                // read every line of the file into the line-variable, on line
                // at the time
                while ((line = buffreader.readLine()) != null) {
                    dataB.append(line);
                }

                fis.close();
                data = dataB.toString();
            } catch (IOException e) {
                Log.d("CacheManager:get", e.getMessage());
            }
        } catch (FileNotFoundException e) {
            Log.d("CacheManager:get", e.getMessage());
        }
        return data;
    }

    /**
     * Call get() method with empty defaultValue.
     */
    public static String get(Context context, String cacheId) {
        return get(context, cacheId, "");
    }

    /**
     * Store data in cache.
     * 
     * @param context
     *        Application context.
     * @param cacheId
     *        Filename in which data will be stored.
     * @param value
     *        Data to be strored.
     */
    public static void set(Context context, String cacheId, String value) {
        FileOutputStream fos;
        try {
            fos = context.getApplicationContext().openFileOutput(cacheId, Context.MODE_PRIVATE);
            try {
                fos.write(value.getBytes());
                fos.close();
            } catch (IOException e) {
                Log.e("CacheManager:set", e.getMessage());
            }
        } catch (FileNotFoundException e) {
            Log.e("CacheManager:set", e.getMessage());
        }
    }

    /**
     * Check if cache exists.
     * 
     * @param context
     * @param cacheId
     * @return
     */
    public static boolean exists(Context context, String cacheId) {
        File file = context.getApplicationContext().getFileStreamPath(cacheId);
        return file.exists();
    }

    /**
     * Delete cache.
     * 
     * @param context
     *        Application context.
     * @param cacheId
     *        Filename in which data will be stored.
     * @return boolean Returns true, if file was deleted.
     */
    public static boolean delete(Context context, String cacheId) {
        File cacheFile = context.getApplicationContext().getFileStreamPath(cacheId);
        return cacheFile.delete();
    }
}
