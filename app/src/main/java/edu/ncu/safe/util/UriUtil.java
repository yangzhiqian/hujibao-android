package edu.ncu.safe.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Yang on 2017/5/11.<br/>
 * url和path之间的转换工具
 */

public class UriUtil {
    /**
     * 参考 http://www.jianshu.com/p/f9a63fcc0b91
     *
     * @param context 上下文
     * @param uri     url,包括file类型的uri和media类型的uri
     * @return filepath
     */
    public static String uri2Path(Context context, Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //Log.d(TAG, uri.toString());
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                return getFilePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                //Log.d(TAG, uri.toString());
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                return getFilePath(context, contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //Log.d(TAG, "content: " + uri.toString());
            return getFilePath(context, uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //file 类型的uri
            return uri.getPath();
        }
        return null;
    }

    /**
     * 文件路径转换成file类型的uri
     * @param filePath  文件路径
     * @return          file类型的uri（file://）
     */
    public static Uri filePath2FileUri(String filePath) {
        File picPath = new File(filePath);
        Uri uri = null;
        if (picPath.exists()) {
            uri = Uri.fromFile(picPath);
        }
        return uri;
    }
    /**
     * 文件路径转换成media类型的uri
     * @param context   上下文
     * @param path      文件路径
     * @return          media类型的uri（content://）
     */
    public static Uri filePath2MediaUri(Context context, String path) {
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(mediaUri,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "= ?",
                new String[] {path.substring(path.lastIndexOf("/") + 1)},
                null);

        Uri uri = null;
        if(cursor.moveToFirst()) {
            uri = ContentUris.withAppendedId(mediaUri,
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
        }
        cursor.close();
        return uri;
    }


    /**
     * 使用的是meida类型的uri
     *
     * @param context   上下文
     * @param uri       uri该处只能使用media类型的uri
     * @param selection selection
     * @return path
     */
    private static String getFilePath(Context context, Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }

            cursor.close();
        }
        return path;
    }
}
