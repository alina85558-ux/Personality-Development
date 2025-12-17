package com.mayur.personalitydevelopment.Utils;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mayur.personalitydevelopment.BuildConfig;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.app.PersonalityDevelopmentApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Admin on 5/8/2017.
 */

public class Utils {

    public static int likeCounter = 0;
    public static String FILTER_NEW_TO_OLD = "new_to_old";
    public static String FILTER_OLD_TO_NEW = "old_to_new";
    public static String FILTER_MOST_LIKED = "most_liked";
    public static String FILTER_PREMIUM = "premium";
    static Dialog loaderDialog;

    // NETWORK ------------------------------------------------------

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String convertNumberToCount(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        if (String.format("%.1f%c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1)).equalsIgnoreCase("0")) {
            return "";
        } else {
            return String.format("%.1f%c",
                    count / Math.pow(1000, exp),
                    "kMGTPE".charAt(exp - 1));
        }
    }

    public static void showToast(final String message) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(PersonalityDevelopmentApp.getInstance(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, pxToDp(PersonalityDevelopmentApp.getInstance(), 70));
                toast.show();
            }
        });
    }

    public static int pxToDp(Context context, float px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static void hideDialog() {
        if (loaderDialog != null && loaderDialog.isShowing()) {
            loaderDialog.dismiss();
            loaderDialog = null;
        }
    }

    public static void showDialog(Context context) {
        hideDialog();
        loaderDialog = new Dialog(context, R.style.myDialog);
        Window window = loaderDialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawableResource(R.drawable.dialog_basic_transparent);
        loaderDialog.setContentView(R.layout.dialog_loader);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        loaderDialog.setCancelable(false);
        final ProgressBar loader = loaderDialog.findViewById(R.id.loader);

        loaderDialog.setOnShowListener(dialog -> {
            if(loader != null){
                loader.setVisibility(View.VISIBLE);
            }
        });

        loaderDialog.setOnDismissListener(dialog -> loader.setVisibility(View.GONE));

        if (!loaderDialog.isShowing()) {
            loaderDialog.show();
        }
    }

    public static int getKv(String packageId) {
        if (packageId.equalsIgnoreCase(BuildConfig.APPLICATION_ID)) {
            return 68471658;
        }
        return 0;
    }

    public static void downloadFile(String uRl, Context context) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Personality");
        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                //.setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Downloading Image.")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Personality_Development" + System.currentTimeMillis() + ".jpg");


        mgr.enqueue(request);

        boolean success = (new File(Environment.getExternalStorageDirectory() + "/Personality_Development")).mkdir();
        if (!success) {
        }

        try {
            URL url = new URL(uRl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            String data1 = String.format(Environment.getExternalStorageDirectory() + "/Personality/%d.jpg", System.currentTimeMillis());

            FileOutputStream stream = new FileOutputStream(data1);

            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outstream);
            byte[] byteArray = outstream.toByteArray();

            stream.write(byteArray);
            stream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String compressImage(String imageUri, Context mContext) {

        String filePath = getRealPathFromURI(imageUri, mContext);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Fllawi/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private static String getRealPathFromURI(String contentURI, Context mContext) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor;

        cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);

        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static RequestBody imageToBody(String text) {
        RequestBody requestBody;
        if (text != null && text.length() > 0) {
            MediaType MEDIA_TYPE = MediaType.parse("image/*");
            File file = new File(text);
            requestBody = RequestBody.create(MEDIA_TYPE, file);
        } else {
            requestBody = null;
        }
        return requestBody;
    }

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    }

    public static String getCurrentDateWithTime() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    public static String changeHourFormat(String time) {
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm", Locale.US);
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm aa", Locale.US);
        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return _12HourSDF.format(_24HourDt) + "";
    }

    public static boolean isSubscribed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Purchase", Context.MODE_PRIVATE);
        return prefs.getBoolean("Issubscribed", false);
    }

    public static void saveFcmToken(Context context, String token) {
        SharedPreferences pref = context.getSharedPreferences("FcmPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("FCM_TOKEN_NEW", token);
        editor.apply();
    }

    public static String getFcmToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences("FcmPref", 0); // 0 - for private mode
        return pref.getString("FCM_TOKEN_NEW", "");
    }

    public static void setArticleLang(Context context, int lang) {
//        1 = Eng Lang, 2 = Hindi Lang
        SharedPreferences pref = context.getSharedPreferences("articleLangPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("articleLang", lang);
        editor.apply();
    }

    public static int getArticleLang(Context context) {
        SharedPreferences pref = context.getSharedPreferences("articleLangPref", 0);
        return pref.getInt("articleLang", 1);
    }

    public static int getCurrentTimeMillis() {
        return (int) System.currentTimeMillis();
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyyHHmmss");
        return format.format(new Date());
    }

    public static boolean isIabServiceAvailable(Context context) {
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentServices(intent, 0);
        return list != null && list.size() > 0;
    }

}
