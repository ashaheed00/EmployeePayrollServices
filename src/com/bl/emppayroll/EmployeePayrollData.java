package com.bl.emppayroll;

import java.sql.Date;

public class EmployeePayrollData {

	private int id;
	private String name;
	private double salary;
	private Date startDate;

	public EmployeePayrollData() {
	}

	public EmployeePayrollData(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayrollData(int id, String name, double salary, Date date) {
		this(id, name, salary);
		this.startDate = date;
	}

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", name=" + name + ", salary=" + salary + ", startDate=" + startDate
				+ "]";
	}

}
