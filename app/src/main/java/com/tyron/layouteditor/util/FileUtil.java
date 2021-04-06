package com.tyron.layouteditor.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    public static boolean isContentScheme(Uri uri)
    {
        return ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme());
    }

    public static boolean isFileScheme(Uri uri)
    {
        return ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme());
    }


    /**
     * Returns a uri to a child file within a folder.  This can be used to get an assumed uri
     * to a child within a folder.  This avoids heavy calls to DocumentFile.listFiles or
     * write-locked createFile
     *
     * This will only work with a uri that is an heriacrchical tree similar to SCHEME_FILE
     * @param hierarchicalTreeUri folder to install into
     * @param filename filename of child file
     * @return Uri to the child file
     */
    public static Uri getChildUri(Uri hierarchicalTreeUri, String filename)
    {
        // TODO: This technically doesn't work for content uris the url encode path separators
        String childUriString = hierarchicalTreeUri.toString() + "/" + filename;
        return Uri.parse(childUriString);
    }
    /**
     * Check is a file is writable. Detects write issues on external SD card.
     *
     * @param file
     *            The file
     * @return true if the file is writable.
     */
    public static boolean isWritable(final File file) {
        boolean isExisting = file.exists();

        try {
            FileOutputStream output = new FileOutputStream(file, true);
            try {
                output.close();
            }
            catch (IOException e) {
                // do nothing.
            }
        }
        catch (FileNotFoundException e) {
            return false;
        }
        boolean result = file.canWrite();

        // Ensure that file is not created during this process.
        if (!isExisting) {
            file.delete();
        }

        return result;
    }

    // Utility methods for Android 5

    /**
     * Get a list of external SD card paths. (Kitkat or higher.)
     *
     * @return A list of external SD card paths.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String[] getExtSdCardPaths(Context context) {
        List<String> paths = new ArrayList<String>();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    //FileLog.w("Unexpected external file dir: " + file.getAbsolutePath());
                }
                else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    }
                    catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        return paths.toArray(new String[0]);
    }

    /**
     * Determine the main folder of the external SD card containing the given file.
     *
     * @param file
     *            the file.
     * @return The main folder of the external SD card containing this file, if the file is on an SD card. Otherwise,
     *         null is returned.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getExtSdCardFolder(final Context context, final File file) {
        String[] extSdPaths = getExtSdCardPaths(context);
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        }
        catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Determine if a file is on external sd card. (Kitkat or higher.)
     *
     * @param file
     *            The file.
     * @return true if on external sd card.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isOnExtSdCard(final Context context, final File file) {
        return getExtSdCardFolder(context, file) != null;
    }

    public static String getCanonicalPathSilently(File file)
    {
        try
        {
            return file.getCanonicalPath();
        }
        catch (IOException e)
        {
            return file.getPath();
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        final String column = "_data";
        final String[] projection = {
                column
        };

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null))
        {
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        return null;
    }

    /**
     * Opens an InputStream to uri.  Checks if it's a local file to create a FileInputStream,
     * otherwise resorts to using the ContentResolver to request a stream.
     *
     * @param context The context.
     * @param uri The Uri to query.
     */
    public static InputStream getInputStream(final Context context, final Uri uri) throws FileNotFoundException
    {
        if (isFileScheme(uri))
        {
            return new FileInputStream(uri.getPath());
        }
        else
        {
            return context.getContentResolver().openInputStream(uri);
        }
    }

    /**
     * Opens an InputStream to uri.  Checks if it's a local file to create a FileInputStream,
     * otherwise resorts to using the ContentResolver to request a stream.
     *
     * @param context The context.
     * @param uri The Uri to query.
     */
    public static ParcelFileDescriptor getParcelFileDescriptor(final Context context, final Uri uri, String mode) throws FileNotFoundException, FileNotFoundException {
        if (isFileScheme(uri))
        {
            int m = ParcelFileDescriptor.MODE_READ_ONLY;
            if ("w".equalsIgnoreCase(mode) || "rw".equalsIgnoreCase(mode)) m = ParcelFileDescriptor.MODE_READ_WRITE;
            else if ("rwt".equalsIgnoreCase(mode)) m = ParcelFileDescriptor.MODE_READ_WRITE | ParcelFileDescriptor.MODE_TRUNCATE;

            //TODO: Is this any faster?  Otherwise could just rely on resolver
            return ParcelFileDescriptor.open(new File(uri.getPath()), m);
        }
        else
        {
            return context.getContentResolver().openFileDescriptor(uri, mode);
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri)
    {
        String[] proj = {MediaStore.Images.Media.DATA};
        try(Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null))
        {
            if (cursor == null)
                return null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }

    public static boolean isSymlink(File file) {
        try
        {
            File canon;
            if (file.getParent() == null)
            {
                canon = file;
            } else
            {
                File canonDir = file.getParentFile().getCanonicalFile();
                canon = new File(canonDir, file.getName());
            }
            return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public static File[] getStorageRoots()
    {
        File mnt = new File("/storage");
        if (!mnt.exists())
            mnt = new File("/mnt");

        File[] roots = mnt.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.exists()
                        && pathname.canWrite() && !pathname.isHidden()
                        && !isSymlink(pathname);
            }
        });
        return roots;
    }

    public static List<File> getStoragePoints(File root)
    {
        List<File> matches = new ArrayList<>();

        if (root == null)
            return matches;

        File[] contents = root.listFiles();
        if (contents == null)
            return matches;

        for (File sub : contents)
        {
            if (sub.isDirectory())
            {
                if (isSymlink(sub))
                    continue;

                if (sub.exists()
                        && sub.canWrite()
                        && !sub.isHidden())
                {
                    matches.add(sub);
                }
                else
                {
                    matches.addAll(getStoragePoints(sub));
                }
            }
        }
        return matches;
    }

    public static List<File> getStorageRoots(String[] roots)
    {
        List<File> valid = new ArrayList<>();
        for (String root : roots)
        {
            File check = new File(root);
            if (check.exists())
            {
                valid.addAll(getStoragePoints(check));
            }
        }
        return valid;
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @return The cache dir
     */

    @TargetApi(9)
    public static boolean isExternalStorageRemovable()
    {
        return Environment.isExternalStorageRemovable();
    }
    public static File getDiskCacheDir(Context context, String uniqueName)
    {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        File cache = null;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable())
        {
            cache = context.getExternalCacheDir();
        }
        if (cache == null)
            cache = context.getCacheDir();

        return new File(cache, uniqueName);
    }

    private static void createNewFile(String path) {
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = path.substring(0, lastSep);
            makeDir(dirPath);
        }

        File file = new File(path);

        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String path) {
        createNewFile(path);

        StringBuilder sb = new StringBuilder();
        FileReader fr = null;
        try {
            fr = new FileReader(new File(path));

            char[] buff = new char[1024];
            int length = 0;

            while ((length = fr.read(buff)) > 0) {
                sb.append(new String(buff, 0, length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public static void writeFile(String path, String str) {
        createNewFile(path);
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(new File(path), false);
            fileWriter.write(str);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyFile(String sourcePath, String destPath) {
        if (!isExistFile(sourcePath)) return;
        createNewFile(destPath);

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(sourcePath);
            fos = new FileOutputStream(destPath, false);

            byte[] buff = new byte[1024];
            int length = 0;

            while ((length = fis.read(buff)) > 0) {
                fos.write(buff, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void moveFile(String sourcePath, String destPath) {
        copyFile(sourcePath, destPath);
        deleteFile(sourcePath);
    }

    public static void deleteFile(String path) {
        File file = new File(path);

        if (!file.exists()) return;

        if (file.isFile()) {
            file.delete();
            return;
        }

        File[] fileArr = file.listFiles();

        if (fileArr != null) {
            for (File subFile : fileArr) {
                if (subFile.isDirectory()) {
                    deleteFile(subFile.getAbsolutePath());
                }

                if (subFile.isFile()) {
                    subFile.delete();
                }
            }
        }

        file.delete();
    }

    public static boolean isExistFile(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void makeDir(String path) {
        if (!isExistFile(path)) {
            File file = new File(path);
            file.mkdirs();
        }
    }

    public static void listDir(String path, ArrayList<String> list) {
        File dir = new File(path);
        if (!dir.exists() || dir.isFile()) return;

        File[] listFiles = dir.listFiles();
        if (listFiles == null || listFiles.length <= 0) return;

        if (list == null) return;
        list.clear();
        for (File file : listFiles) {
            list.add(file.getAbsolutePath());
        }
    }

    public static boolean isDirectory(String path) {
        if (!isExistFile(path)) return false;
        return new File(path).isDirectory();
    }

    public static boolean isFile(String path) {
        if (!isExistFile(path)) return false;
        return new File(path).isFile();
    }

    public static long getFileLength(String path) {
        if (!isExistFile(path)) return 0;
        return new File(path).length();
    }

    public static String getExternalStorageDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getPackageDataDir(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    public static String getPublicDir(String type) {
        return Environment.getExternalStoragePublicDirectory(type).getAbsolutePath();
    }

    public static String convertUriToFilePath(final Context context, final Uri uri) {
        String path = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    path = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);

                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                }

                final Uri contentUri = ContentUris
                        .withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                path = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                path = getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            path = getDataColumn(context, uri, null, null);
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }

        if (path != null) {
            try {
                return URLDecoder.decode(path, "UTF-8");
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private static void saveBitmap(Bitmap bitmap, String destPath) {
        FileOutputStream out = null;
        FileUtil.createNewFile(destPath);
        try {
            out = new FileOutputStream(new File(destPath));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getScaledBitmap(String path, int max) {
        Bitmap src = BitmapFactory.decodeFile(path);

        int width = src.getWidth();
        int height = src.getHeight();
        float rate = 0.0f;

        if (width > height) {
            rate = max / (float) width;
            height = (int) (height * rate);
            width = max;
        } else {
            rate = max / (float) height;
            width = (int) (width * rate);
            height = max;
        }

        return Bitmap.createScaledBitmap(src, width, height, true);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampleBitmapFromPath(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static void resizeBitmapFileRetainRatio(String fromPath, String destPath, int max) {
        if (!isExistFile(fromPath)) return;
        Bitmap bitmap = getScaledBitmap(fromPath, max);
        saveBitmap(bitmap, destPath);
    }

    public static void resizeBitmapFileToSquare(String fromPath, String destPath, int max) {
        if (!isExistFile(fromPath)) return;
        Bitmap src = BitmapFactory.decodeFile(fromPath);
        Bitmap bitmap = Bitmap.createScaledBitmap(src, max, max, true);
        saveBitmap(bitmap, destPath);
    }

    public static void resizeBitmapFileToCircle(String fromPath, String destPath) {
        if (!isExistFile(fromPath)) return;
        Bitmap src = BitmapFactory.decodeFile(fromPath);
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(),
                src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(src.getWidth() / 2, src.getHeight() / 2,
                src.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);

        saveBitmap(bitmap, destPath);
    }

    public static void resizeBitmapFileWithRoundedBorder(String fromPath, String destPath, int pixels) {
        if (!isExistFile(fromPath)) return;
        Bitmap src = BitmapFactory.decodeFile(fromPath);
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);

        saveBitmap(bitmap, destPath);
    }

    public static void cropBitmapFileFromCenter(String fromPath, String destPath, int w, int h) {
        if (!isExistFile(fromPath)) return;
        Bitmap src = BitmapFactory.decodeFile(fromPath);

        int width = src.getWidth();
        int height = src.getHeight();

        if (width < w && height < h)
            return;

        int x = 0;
        int y = 0;

        if (width > w)
            x = (width - w) / 2;

        if (height > h)
            y = (height - h) / 2;

        int cw = w;
        int ch = h;

        if (w > width)
            cw = width;

        if (h > height)
            ch = height;

        Bitmap bitmap = Bitmap.createBitmap(src, x, y, cw, ch);
        saveBitmap(bitmap, destPath);
    }

    public static void rotateBitmapFile(String fromPath, String destPath, float angle) {
        if (!isExistFile(fromPath)) return;
        Bitmap src = BitmapFactory.decodeFile(fromPath);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        saveBitmap(bitmap, destPath);
    }

    public static void scaleBitmapFile(String fromPath, String destPath, float x, float y) {
        if (!isExistFile(fromPath)) return;
        Bitmap src = BitmapFactory.decodeFile(fromPath);
        Matrix matrix = new Matrix();
        matrix.postScale(x, y);

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, w, h, matrix, true);
        saveBitmap(bitmap, destPath);
    }

    public static void skewBitmapFile(String fromPath, String destPath, float x, float y) {
        if (!isExistFile(fromPath)) return;
        Bitmap src = BitmapFactory.decodeFile(fromPath);
        Matrix matrix = new Matrix();
        matrix.postSkew(x, y);

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, w, h, matrix, true);
        saveBitmap(bitmap, destPath);
    }

    public static void setBitmapFileColorFilter(String fromPath, String destPath, int color) {
        if (!isExistFile(fromPath)) return;
        Bitmap src = BitmapFactory.decodeFile(fromPath);
        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0,
                src.getWidth() - 1, src.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, p);
        saveBitmap(bitmap, destPath);
    }

    public static void setBitmapFileBrightness(String fromPath, String destPath, float brightness) {
        if (!isExistFile(fromPath)) return;
        Bitmap src = BitmapFactory.decodeFile(fromPath);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1, 0, 0, 0, brightness,
                        0, 1, 0, 0, brightness,
                        0, 0, 1, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(src, 0, 0, paint);
        saveBitmap(bitmap, destPath);
    }

    public static void setBitmapFileContrast(String fromPath, String destPath, float contrast) {
        if (!isExistFile(fromPath)) return;
        Bitmap src = BitmapFactory.decodeFile(fromPath);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, 0,
                        0, contrast, 0, 0, 0,
                        0, 0, contrast, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(src, 0, 0, paint);

        saveBitmap(bitmap, destPath);
    }

    public static int getJpegRotate(String filePath) {
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int iOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            switch (iOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                default:
                    rotate = 0;
                    break;
            }
        } catch (IOException e) {
            return 0;
        }

        return rotate;
    }

    public static File createNewPictureFile(Context context) {
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = date.format(new Date()) + ".jpg";
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + fileName);
        return file;
    }
}