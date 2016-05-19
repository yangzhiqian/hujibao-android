package edu.ncu.safe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.db.FlowsRecordDatabaseHelper;
import edu.ncu.safe.domain.FlowsStatisticsAppItemInfo;
import edu.ncu.safe.domain.FlowsStatisticsDayItemInfo;
import edu.ncu.safe.util.FormatIntDate;

public class FlowsDatabase {
	private FlowsRecordDatabaseHelper database;

	public FlowsDatabase(Context context) {
		database = new FlowsRecordDatabaseHelper(context, "flows_db");
	}

	public void addIntoAppFlowsDB(FlowsStatisticsAppItemInfo info) {
		try {
			SQLiteDatabase db = database.getWritableDatabase();
			String sql = "insert into "
					+ FlowsRecordDatabaseHelper.APPFLOWSTABLENAME
					+ " values(?,?,?,?)";
			db.execSQL(sql, new Object[] { info.getUid(), info.getAppName(),
					info.getUpdate(), info.getDownload() });
			db.close();
		} catch (Exception e) {
		}
	}

	public List<FlowsStatisticsAppItemInfo> queryFromAppFlowsDB() {
		List<FlowsStatisticsAppItemInfo> infos = new ArrayList<FlowsStatisticsAppItemInfo>();

		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = db.query(FlowsRecordDatabaseHelper.APPFLOWSTABLENAME,
				FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS, null, null,
				null, null, FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS[2]
						+ "+"
						+ FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS[3]
						+ " desc");
		while (cursor.moveToNext()) {
			int uid = cursor
					.getInt(cursor
							.getColumnIndex(FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS[0]));
			String appName = cursor
					.getString(cursor
							.getColumnIndex(FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS[1]));
			long updateFlows = cursor
					.getLong(cursor
							.getColumnIndex(FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS[2]));
			long downloadFlows = cursor
					.getLong(cursor
							.getColumnIndex(FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS[3]));

			FlowsStatisticsAppItemInfo info = new FlowsStatisticsAppItemInfo(
					uid, appName, updateFlows, downloadFlows);
			infos.add(info);
		}
		db.close();
		return infos;
	}

	public void updateIntoAppFlowsDB(FlowsStatisticsAppItemInfo info) {
		try {
			SQLiteDatabase db = database.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS[2],
					info.getUpdate());
			values.put(FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS[3],
					info.getDownload());
			db.update(
					FlowsRecordDatabaseHelper.APPFLOWSTABLENAME, 
					values, 
					FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS[0]+"=?",
					new String[] { info.getUid() + "" });
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteFromAppFlowsDB(int uid) {
		try {
			SQLiteDatabase db = database.getWritableDatabase();
			db.delete(FlowsRecordDatabaseHelper.APPFLOWSTABLENAME, 
					FlowsRecordDatabaseHelper.APPFLOWSTABLECOLUMNS[0]+"=?",
					new String[] { uid + "" });
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public void addIntoTotalFlowsDB(FlowsStatisticsDayItemInfo info){
		try {
			SQLiteDatabase db = database.getWritableDatabase();
			String sql = "insert into "+FlowsRecordDatabaseHelper.TOTALFLOWSTABLENAME
					+ " values (?,?,?)";
			db.execSQL(sql, new String[]{info.getDate()+"",info.getUpdate()+"",info.getDownload()+""});
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void deleteIntoTotalFlowsDB(int date){
		try {
			SQLiteDatabase db = database.getWritableDatabase();
			
			db.delete(FlowsRecordDatabaseHelper.TOTALFLOWSTABLENAME,
					FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[0]+"=?",
					new String[]{date+""});
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateIntoTotalFlowsDB(FlowsStatisticsDayItemInfo info){
		try {
			SQLiteDatabase db = database.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[1], info.getUpdate());
			values.put(FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[2], info.getDownload());
			db.update(FlowsRecordDatabaseHelper.TOTALFLOWSTABLENAME,
					values,
					FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[0]+"=?",
					new String[]{info.getDate()+""});
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取数据库中最新的一条数据，一般为当天的数据
	 * @return FlowsStatisticsDayItemInfo
	 */
	public FlowsStatisticsDayItemInfo queryCurrentDayFromTotalFlowsDB(){
		int date = FormatIntDate.getCurrentFormatIntDate();
		return queryFromTotalFlowsDB(date);
	}
	
	
	/**
	 * 以date为查询条件返回FlowsStatisticsDayItemInfo数据
	 * @param date  查询条件
	 * @return  null代表没有查询 否则返回查询到的数据
	 */
	public FlowsStatisticsDayItemInfo queryFromTotalFlowsDB(int date){
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = db.query(FlowsRecordDatabaseHelper.TOTALFLOWSTABLENAME,
				null,
				FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[0]+"=?",//"date=?"
				new String[]{date+""},null , null, null);
		
		if(cursor.getCount()<1){
			db.close();
			return null;
		}
		while(cursor.moveToNext()){
			int da = cursor.getInt(cursor.getColumnIndex(FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[0]));
			long update = cursor.getLong(cursor.getColumnIndex(FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[1]));
			long download = cursor.getLong(cursor.getColumnIndex(FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[2]));
			
			FlowsStatisticsDayItemInfo info = new FlowsStatisticsDayItemInfo(da, update, download);
			db.close();
			return info;
		}
		db.close();
		return null;
	}
	
	/**
	 * 获取数据库中所有FlowsStatisticsDayItemInfo数据
	 * @return
	 */
	public List<FlowsStatisticsDayItemInfo> queryAllFromTotalFlowsDB(){
		List<FlowsStatisticsDayItemInfo> infos = new ArrayList<FlowsStatisticsDayItemInfo>();
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = db.query(FlowsRecordDatabaseHelper.TOTALFLOWSTABLENAME,
				null, null, null, null, null, 
				FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[0]+" desc");
		
		while(cursor.moveToNext()){
			int date = cursor.getInt(cursor.getColumnIndex(FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[0]));
			long update = cursor.getLong(cursor.getColumnIndex(FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[1]));
			long download = cursor.getLong(cursor.getColumnIndex(FlowsRecordDatabaseHelper.TOTALFLOWSTABLECOLUMNS[2]));
			
			FlowsStatisticsDayItemInfo info = new FlowsStatisticsDayItemInfo(date, update, download);
			infos.add(info);
		}
		return infos;
	}
	
	/**
	 * 获取当月的数据流量信息，以M为单位
	 * @return
	 */
	public float[] queryCurrentMonthByDayFlows(){
		float[] data = new float[31];
		int date = FormatIntDate.getCurrentFormatIntDate();
		while(date%100 != 0){
			FlowsStatisticsDayItemInfo info = queryFromTotalFlowsDB(date);
			if(info == null){
				data[date%100-1] = 0;
			}else{
				long update = info.getUpdate();
				long download = info.getDownload();
				data[date%100-1] = ((float)(update+download))/(1024*1024);
			}
			System.out.println(data[date%100-1]);
			date--;
		}
		return data;
	}
	/**
	 * 获取当月使用的GPRS总流量
	 * @return 以byte为单位的流量数据
	 */
	public long queryCurrentMonthTotalFlows(){
		int date = FormatIntDate.getCurrentFormatIntDate();
		long sumFlows = 0;
		
		while(date%100!=0){
			FlowsStatisticsDayItemInfo info = queryFromTotalFlowsDB(date);
			if(info!=null){
				//date当天的数据有记录
				sumFlows = sumFlows + info.getUpdate() + info.getDownload(); 
			}
			date-=1;
		}
		return sumFlows;
	}
	
	public long queryCurrentDayTotalFlows(){
		int date = FormatIntDate.getCurrentFormatIntDate();
		FlowsStatisticsDayItemInfo info = queryFromTotalFlowsDB(date);
		if(info!=null){
			//date当天的数据有记录
			return info.getUpdate() + info.getDownload(); 
		}
		return 0;
	}
}
