package com.z.statisticsPlatform.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

	public final static int MAX_BETWEEN_DAYS = 90;	// 最多允许查询90天内数据
	
	/**
	 * 检查时间间隔天数是否超过MAX_BETWEEN_DAYS
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static boolean checkTime(String beginTime, String endTime) {
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(beginTime));
			long time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(endTime));
			long time2 = cal.getTimeInMillis();
			long betweenDays=(time2-time1)/(1000*3600*24);

			int bDays = Integer.parseInt(String.valueOf(betweenDays));
			return (bDays <= MAX_BETWEEN_DAYS) ? true : false;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}

