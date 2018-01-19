package app.dodi.com.prodigykms.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 01/01/2018.
 */
public class Utils {

    public static String formatDate(Date source, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(source);
    }

    public static Date parseDate(String source, String pattern) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat(pattern, Locale.getDefault());
        return parser.parse(source);
    }

    public static Bitmap createBitmap(String text, int height, int width, int bgColor, int textColor, float textSize) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(bgColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        Rect rect = new Rect();
        canvas.getClipBounds(rect);

        int cH = rect.height();
        int cW = rect.width();

        paint = new Paint();
        paint.setColor(textColor);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), rect);

        //paint.setTextAlign(Paint.Align.LEFT);

        //paint.setTextAlign(Paint.Align.CENTER);
//        float x = (canvas.getWidth() / 2.f) - (textSize / 2.f);
//        float y = (canvas.getHeight() / 2.f) + (textSize / 2.f);

        float x = (cW / 2.f) - (rect.width() / 2.f) - rect.left;
        float y = (cH / 2.f) + (rect.height() / 2.f) - rect.bottom;
        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    public static String encodeBitmspToBase64(Bitmap source) {
        source = compressBitmap(source);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        source.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64ToBitmap(String base64) {
        byte[] imageAsBytes = Base64.decode(base64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public static boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }


        return isInBackground;
    }


    public static Bitmap getThumbnail(Context context, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 60) ? (originalSize / 60) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));

        if (k == 0)
            return 1;
        else
            return k;
    }

    public static Bitmap compressBitmap(Bitmap bmp) {

        float ratio = Math.min(
                (float) 300 / bmp.getWidth(),
                (float) 300 / bmp.getHeight());

        if (ratio >= 1.0) return bmp;

        int width = Math.round((float) ratio * bmp.getWidth());
        int height = Math.round((float) ratio * bmp.getHeight());

        return Bitmap.createScaledBitmap(bmp, width, height, true);
    }
}
