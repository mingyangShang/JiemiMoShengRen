package com.levelup.jiemimoshengren.log;

public class SmyRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 20150504L;

	public SmyRuntimeException(String errorMsg){
		super("smy:"+errorMsg);
	}
	
	public SmyRuntimeException(String errorMsg,Throwable throwable){
		super(errorMsg,throwable);
	}
	
	public SmyRuntimeException(Throwable throwable){
		super(throwable);
	}
	
}
