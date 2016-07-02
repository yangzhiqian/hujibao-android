package edu.ncu.safe.test;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.test.AndroidTestCase;

import java.io.File;
import java.sql.Date;

/**
 * Created by Mr_Yang on 2016/6/3.
 */
public class PhotoTest extends AndroidTestCase{
    public void testp(){
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){
            String data = cursor.getString(cursor.getColumnIndex("_data"));
            File file = new File(data);
            String name = file.getName();
            long date_modified = file.lastModified();
            long size =  file.getTotalSpace();
            System.out.println(data+"::::"+name+":::::::"+size+"::::"+new Date(date_modified).toString());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=100;
            BitmapFactory.decodeFile(data,options);
        }

    }
}
