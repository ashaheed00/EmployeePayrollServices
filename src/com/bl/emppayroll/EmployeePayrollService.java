package com.bl.emppayroll;

import java.util.*;

public class EmployeePayrollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.print("Enter Employee ID: ");
		int id = consoleInputReader.nextInt();
		System.out.print("Enter Employee Name: ");
		String name = consoleInputReader.next();
		System.out.print("Enter Employee Salary: ");
		double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	void writeEmployeePayrollData(IOService inputReader) {
		System.out.println("\nWriting Employee Payroll Data to Console\n" + employeePayrollList);
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			employeePayrollList = new EmployeePayrollFileIOService().readData();
		if (ioService.equals(IOService.DB_IO))
			employeePayrollList = employeePayrollDBService.readData();
		return employeePayrollList;
	}

	public void updateEmployeeSalary(String name, Double salary) {
		int result = employeePayrollDBService.updateSalaryUsingSQL(name, salary);
		EmployeePayrollData employeePayrollData = getEmployeePayrollData(name);
		if (result != 0 && employeePayrollData != null)
			employeePayrollData.setSalary(salary);
	}

	public boolean isEmpPayrollSyncedWithDB(String name) {
		try {
			return employeePayrollDBService.getEmployeePayrollDatas(name).get(0).equals(getEmployeePayrollData(name));
		} catch (IndexOutOfBoundsException e) {
		}
		return false;
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return employeePayrollList.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
	}
}
