package edu.ncu.safe.util;

import java.util.Calendar;

public class FormatIntDate {
	
	/**
	 * 该方法用来获取当前时间的特定整形格式   ： YYYYMMDD
	 * @return 返回特定格式的整形
	 */
	public static int getCurrentFormatIntDate(){
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String strDate = year+"";
		if(month<10){
			strDate= strDate+ "0"+ month;
		}else{
			strDate= strDate+ month;
		}
		if(day<10){
			strDate= strDate+ "0"+ day;
		}
		else{
			strDate= strDate + day;
		}
		return Integer.parseInt(strDate);
	}
	
	
	/**
	 * 该静态方法用来充某个特定格式整形时间里获取day
	 * @param formatIntDate  要获取的特定整形时间
	 * @return  返回整形day
	 */
	public static int getDayFromFormatIntDate(int formatIntDate){
		return formatIntDate%100;
	}
	/**
	 * 该静态方法用来充某个特定格式整形时间里获取month
	 * @param formatIntDate  要获取的特定整形时间
	 * @return  返回整形month
	 */
	public static int getMonthFromFormatIntDate(int formatIntDate){
		return (formatIntDate/=100)%100;
	}
	/**
	 * 该静态方法用来充某个特定格式整形时间里获取year
	 * @param formatIntDate  要获取的特定整形时间
	 * @return  返回整形year
	 */
	public static int getYearFromFormatIntDate(int formatIntDate){
		return formatIntDate/10000;
	}
}
