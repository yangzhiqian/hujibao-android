package edu.ncu.safe.constant;

import android.os.Environment;

import java.io.File;

import edu.ncu.safe.external.ACache;

/**
 * Created by Mr_Yang on 2016/7/10.
 */
public class Constant {
    public static final String DIVIDER="-";//用于拼接缓存图片的文件名
    public static final int ACACHE_LIFETIME = ACache.TIME_DAY*7;//图片缓存的时间

    /**
     * @return 返回下载图片的绝对路径
     */
    public static String getImageFolerPath(){
       String parent =  Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(parent+"/androidsafe/Img");
        if(!file.exists()){
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static String getImageCacheFileName(String fileName,int type){
        return fileName+DIVIDER+ type;
    }
}
