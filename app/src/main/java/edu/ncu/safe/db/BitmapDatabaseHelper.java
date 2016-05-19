package edu.ncu.safe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BitmapDatabaseHelper extends SQLiteOpenHelper{
	
	public static final String DBNAME = "BITMAP";
	public static final String[] COLUMNSNAME = {"URI","PATH","UPDATETIME"};

	public BitmapDatabaseHelper(Context context, String name) {
		super(context, name, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建bitmap 数据库
		String sql = "create table "+DBNAME+"("+COLUMNSNAME[0]+" varchar primary key," +
				COLUMNSNAME[1]+" varchar ," +
				COLUMNSNAME[2]+" datetime)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
