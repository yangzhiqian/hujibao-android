package edu.ncu.safe.engine;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.SmsInfo;

public class LoadRecoverSms {
	private Context context;
	public LoadRecoverSms(Context context){
		this.context = context;
	}
	/**
	 * 从信息表中获取所有的信息
	 * @return  返回包含信息对象的集合
	 */
	public List<SmsInfo> getSms(){
		List<SmsInfo> infos = new ArrayList<SmsInfo>();
		Uri uri = Uri.parse("content://sms");
		Cursor cursor =context.getContentResolver().query(uri,
				new String[]{"address","date","type","body"}, null, null, null);
		while(cursor.moveToNext()){
			String address = cursor.getString(0);
			long date = cursor.getLong(1);
			int type = cursor.getInt(2);
			String body = cursor.getString(3);
			SmsInfo info = new SmsInfo(address, date, type, body);
			infos.add(info);
		}
		return infos;
	}
	
	/**
	 * 将一个要添加短信的集合添加到短信数据库中
	 * @param toAddInfos  要添加的短信集合
	 * @return 添加的个数
	 */
	public int recoverSms(List<SmsInfo> toAddInfos){
		int numbers = 0;
		List<SmsInfo> existInfos = getSms();
		Uri uri = Uri.parse("content://sms");
		ContentResolver resolver = context.getContentResolver();
		for(SmsInfo info : toAddInfos){
			if(!isInCollection(info,existInfos)){
				//要添加的短信不在短信数据库中
				ContentValues values = new ContentValues();
				values.put("address", info.getAddress());
				values.put("date", info.getDate());
				values.put("type", info.getType());
				values.put("body", info.getBody());
				resolver.insert(uri, values);
				numbers++;
			}
		}
		return numbers;
	}
	/**
	 * 向信息表中恢复一条数据
	 * @param info  要恢复的数据
	 * @return true标示恢复成功
	 */
	public boolean recoveryOneSms(SmsInfo info){
		List<SmsInfo> toAddInfos = new ArrayList<SmsInfo>();
		toAddInfos.add(info);
		if(recoverSms(toAddInfos)==0){
			return false;
		}else{
			return true;
		}
	}
	private boolean isInCollection(SmsInfo info,List<SmsInfo> infos){
		for(SmsInfo item:infos){
			if(item.getAddress()==null){
				continue;//没有号码的短信
			}
			if(item.getAddress().equals(info.getAddress())&&
					item.getDate()==info.getDate()){
				return true;
			}
		}
		return false;
	}
}
