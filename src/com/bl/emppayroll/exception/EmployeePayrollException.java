package com.bl.emppayroll.exception;

public class EmployeePayrollException extends Exception {
	public enum ExceptionType {
		NO_DATA_FOUND, WRONG_SQL, WRONG_NAME, WRONG_IO_TYPE
	}

	private ExceptionType exceptionType;
	private String message;

	public EmployeePayrollException(ExceptionType exception) {
		this.exceptionType = exception;
	}

	public EmployeePayrollException(String message, ExceptionType exception) {
		this.exceptionType = exception;
		this.message = message;
	}

	public ExceptionType getExceptionType() {
		return exceptionType;
	}

	public String getMessage() {
		return message;
	}

}