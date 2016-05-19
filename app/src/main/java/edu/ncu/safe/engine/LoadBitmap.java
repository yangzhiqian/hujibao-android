package edu.ncu.safe.engine;

import android.content.Context;
import android.graphics.Bitmap;
import edu.ncu.safe.db.dao.BitmapDatabase;
import edu.ncu.safe.domain.BitmapDBInfo;
import edu.ncu.safe.external.ACache;

public class LoadBitmap {
	private Context context;
	private ACache cache;
	private BitmapDatabase db;
	public LoadBitmap(Context context){
		this.context = context;
		cache = ACache.get(context, 8*1024*1024, 50);
	}
	
	public void getBitmap(String uri,OnBitampLoadingListtener listener){
		Bitmap bitmap ;
		//从一级缓存里面获取（内存）
		bitmap = cache.getAsBitmap(uri);
		if(bitmap == null){
			//一级缓存中没有，从二级缓存中获取（sqlite）
			BitmapDBInfo info = db.queryFromBitmapDB(uri);
			if(info == null){
				//二级缓存中没有，到网络上加载
			}
		}
	}
	
	public interface OnBitampLoadingListtener{
		void bitmapLoadingCompleted(Bitmap bitmap);
	}
}
