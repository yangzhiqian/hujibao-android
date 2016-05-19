package edu.ncu.safe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FlowsRecordDatabaseHelper extends SQLiteOpenHelper{

	public static final String APPFLOWSTABLENAME = "APPFLOWSTABLE";
	public static final String TOTALFLOWSTABLENAME = "TOTALFLOWSTABLENAME";
	public static final String[] APPFLOWSTABLECOLUMNS = {"UID","APPNAME","UPDATEFLOWS","DOWNLOADFLOWS"};
	public static final String[] TOTALFLOWSTABLECOLUMNS = {"TOTALDATE","TOTALUPDATE","TOTALDOWNLOAD"};
	public FlowsRecordDatabaseHelper(Context context, String databaseName) {
		super(context, databaseName, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table "+APPFLOWSTABLENAME +
				" ("+APPFLOWSTABLECOLUMNS[0]+
				"  INT primary key, "+APPFLOWSTABLECOLUMNS[1]+
				"  varchar NOT NULL, "+APPFLOWSTABLECOLUMNS[2]+
				"  BIGINT, "+APPFLOWSTABLECOLUMNS[3]+"  BIGINT)";
		db.execSQL(sql);
		sql = "create table "+TOTALFLOWSTABLENAME +
				" ("+TOTALFLOWSTABLECOLUMNS[0]+
				"  int primary key, "+TOTALFLOWSTABLECOLUMNS[1]+
				"  BIGINT, "+TOTALFLOWSTABLECOLUMNS[2]+"  BIGINT)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
