package com.z.statisticsPlatform.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class Utils {

	public final static int MAX_BETWEEN_DAYS = 90;	// 最多允许查询90天内数据
	
	public final static int INTERVAL_CRAWLER = 3 * 3600 * 1000;	// 爬虫爬取一次间隔时间
	
	/**
	 * 检查时间间隔天数是否超过MAX_BETWEEN_DAYS
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static ResultInfo checkTime(String beginTime, String endTime) {

		ResultInfo result = new ResultInfo(ResponseUtil.success_code);
		
		try { 
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(beginTime));
			long time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(endTime));
			long time2 = cal.getTimeInMillis();
			long betweenDays=(time2-time1)/(1000*3600*24);
			
			// 如果起始时间大于结束时间
			if(betweenDays < 0) {
				return new ResultInfo(ResponseUtil.param_error_code, "起始时间大于结束时间", null);
			}

			int bDays = Integer.parseInt(String.valueOf(betweenDays));
			// 最多允许查询  Utils.MAX_BETWEEN_DAYS 天内数据
			if(bDays > MAX_BETWEEN_DAYS) {
				return new ResultInfo(ResponseUtil.param_error_code, "最多允许查询" + Utils.MAX_BETWEEN_DAYS + "天内数据", null);
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultInfo(ResponseUtil.faile_code);
		}
		return result;
	}
	
	/**
	 * 计算当日爬虫爬取次数
	 * @return
	 */
	public static int calCrawlingTimes() {
		try { 
			// 当前时间毫秒数
			long currentTime = System.currentTimeMillis();
			
			// 今天零点零分零秒的毫秒数
			long todayZero = currentTime / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
			return (int) ((currentTime - todayZero) / INTERVAL_CRAWLER);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}

