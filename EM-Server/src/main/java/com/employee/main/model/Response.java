package com.employee.main.model;

import java.io.Serializable;

public class Response implements Serializable {
	private static final long serialVersionUID = 1L;
	private String data;
	private Object jData;
	private int resposneCode;
	private String responseMsg;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Object getjData() {
		return jData;
	}

	public void setjData(Object jData) {
		this.jData = jData;
	}

	public int getResposneCode() {
		return resposneCode;
	}

	public void setResponseCode(int resposneCode) {
		this.resposneCode = resposneCode;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

}
