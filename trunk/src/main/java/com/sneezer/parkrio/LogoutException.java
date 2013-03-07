package com.sneezer.parkrio;

public class LogoutException extends Exception {
	public LogoutException() {
		super();
	}
	
	public LogoutException(String err) {
		super(err);
	}
	
	public String getError() {
		return "로그아웃되었습니다. 재 로그인해 주세요";
	}
}
