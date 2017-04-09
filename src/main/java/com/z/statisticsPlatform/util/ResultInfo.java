package com.z.statisticsPlatform.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ResultInfo{
	private static SerializerFeature[] features = 
		{ SerializerFeature.WriteMapNullValue // 输出空置字段
		, SerializerFeature.WriteNullListAsEmpty // list字段如果为null，输出为[]，而不是null
		, SerializerFeature.WriteNullNumberAsZero // 数值字段如果为null，输出为0，而不是null
		, SerializerFeature.WriteNullBooleanAsFalse // Boolean字段如果为null，输出为false，而不是null
		, SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null		
		};
	
	private String code;
	private String message;
	private Object body;
 

	
	public ResultInfo(String code) {
		this.code = code;
		this.message = ResponseUtil.getMsgByCode(code);
		this.body = "";
	}

	public ResultInfo(String code, String message, Object body) {
		this.code = code;
		this.message = message;
		this.body = body;
	}

	public ResultInfo(String code, Object body) {
		this.code = code;
		this.message = ResponseUtil.getMsgByCode(code);
		this.body = body;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ResultInfo [code=" + code + ", message=" + message + "]";
	}
	
	public String toJSONString() {		
		String str = JSONObject.toJSONString(this, features);
		return str;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

}

