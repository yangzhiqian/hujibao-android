package edu.ncu.safe.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.ImageInfo;

/**
 * Created by Mr_Yang on 2016/7/1.
 */
public class LoadLocalImageInfo {
    private Context context;
    public LoadLocalImageInfo(Context context){
        this.context= context;
    }
    public List<ImageInfo> getLocalImageInfos(){
        List<ImageInfo> infos = new ArrayList<ImageInfo>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        String path;
        File file;
        String name;
        long time;
        long size;
        while (cursor.moveToNext()) {
            path = cursor.getString(cursor.getColumnIndex("_data"));
            file = new File(path);
            name = file.getName();
            time = file.lastModified();
            size = file.length();
            infos.add(new ImageInfo(path, name, time, size));
        }
        return infos;
    }
}
