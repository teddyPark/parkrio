package com.sneezer.parkrio;

public class LogoutException extends Exception {
	public LogoutException() {
		super();
	}
	
	public LogoutException(String err) {
		super(err);
	}
	
	public String getError() {
		return "�α׾ƿ��Ǿ����ϴ�. �� �α����� �ּ���";
	}
}
