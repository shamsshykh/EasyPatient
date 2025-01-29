package com.app.easy_patient.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Vinod Kumar on 9/5/19.
 */
public class FileUtils {

    /**
     * This method is used to return a file.
     *
     * @return
     */
    public static File getFileToKeepImage(Context context) {

        if (isSDCARDMounted()) {
            String BASE_DIR = context.getExternalCacheDir().getPath();
            File f = new File(BASE_DIR, "tmp_avatar_"
                    + String.valueOf(System.currentTimeMillis()) + ".jpg");
            try {
                f.createNewFile();
            } catch (IOException e) {
            }
            return f;
        } else {
            return null;
        }
    }

    /**
     * This method is to return whether device SD card is maounted or not
     *
     * @return
     */
    public static boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

    /**
     * This method is used to copy the crooped image
     */
    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    public static int getContactIdForWhatsAppCall(String name, Context context) {

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data._ID},
                ContactsContract.Data.DISPLAY_NAME + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{name, "vnd.android.cursor.item/vnd.com.whatsapp.voip.call"},
                ContactsContract.Contacts.DISPLAY_NAME);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data._ID));
            System.out.println("9999999999999999          name  " + name + "      id    " + phoneContactID);
            return phoneContactID;
        } else {
            System.out.println("8888888888888888888          ");
            return 0;
        }
    }
}
