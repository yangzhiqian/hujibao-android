package edu.ncu.safe.db.dao;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import edu.ncu.safe.db.BitmapDatabaseHelper;
import edu.ncu.safe.domain.BitmapDBInfo;

public class BitmapDatabase {
	private BitmapDatabaseHelper helper;
	
	public BitmapDatabase(Context context ){
		helper = new BitmapDatabaseHelper(context, "bitmap");
	}
	
	public BitmapDBInfo queryFromBitmapDB(String uri){
		BitmapDBInfo info = null;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(BitmapDatabaseHelper.DBNAME, null, BitmapDatabaseHelper.COLUMNSNAME[0]+ "=?", new String[]{uri}, null, null, null);
		if(cursor.moveToFirst()){
			String url = cursor.getString(cursor.getColumnIndex(BitmapDatabaseHelper.COLUMNSNAME[0]));
			String path = cursor.getString(cursor.getColumnIndex(BitmapDatabaseHelper.COLUMNSNAME[1]));
			String str = cursor.getString(cursor.getColumnIndex(BitmapDatabaseHelper.COLUMNSNAME[2]));
			
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = (Date)format.parse(str);
				info = new BitmapDBInfo(url, path, date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		db.close();
		return info;
	}
	
	
}
