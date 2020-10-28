package com.bl.emppayroll.service;

import java.time.LocalDate;
import java.util.*;
import com.bl.emppayroll.EmployeePayrollData;
import com.bl.emppayroll.exception.EmployeePayrollException;
import com.bl.emppayroll.exception.EmployeePayrollException.ExceptionType;

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

	public List<EmployeePayrollData> getEmpPayrollDataForDateRange(LocalDate startDate, LocalDate endDate)
			throws EmployeePayrollException {
		return employeePayrollDBService.getEmployeesForDateRange(startDate, endDate);
	}

	public Map<String, Double> getSumOfDataGroupedByGender(IOService ioService, String column)
			throws EmployeePayrollException {
		if (ioService == IOService.DB_IO)
			return employeePayrollDBService.getEmpDataGroupedByGender(column, "SUM");
		else
			throw new EmployeePayrollException("Wrong IO type", ExceptionType.WRONG_IO_TYPE);
	}

	public Map<String, Double> getAvgOfDataGroupedByGender(IOService ioService, String column)
			throws EmployeePayrollException {
		if (ioService == IOService.DB_IO)
			return employeePayrollDBService.getEmpDataGroupedByGender(column, "AVG");
		else
			throw new EmployeePayrollException("Wrong IO type", ExceptionType.WRONG_IO_TYPE);
	}

	public Map<String, Double> getMaxOfDataGroupedByGender(IOService ioService, String column)
			throws EmployeePayrollException {
		if (ioService == IOService.DB_IO)
			return employeePayrollDBService.getEmpDataGroupedByGender(column, "MAX");
		else
			throw new EmployeePayrollException("Wrong IO type", ExceptionType.WRONG_IO_TYPE);
	}

	public Map<String, Double> getCountOfDataGroupedByGender(IOService ioService, String column)
			throws EmployeePayrollException {
		if (ioService == IOService.DB_IO)
			return employeePayrollDBService.getEmpDataGroupedByGender(column, "COUNT");
		else
			throw new EmployeePayrollException("Wrong IO type", ExceptionType.WRONG_IO_TYPE);
	}

	public void updateEmployeeSalary(String name, Double salary) throws EmployeePayrollException {
		int result = employeePayrollDBService.updateSalaryUsingSQL(name, salary);
		EmployeePayrollData employeePayrollData = getEmployeePayrollData(name);
		if (result != 0 && employeePayrollData != null)
			employeePayrollData.setSalary(salary);
		if (result == 0)
			throw new EmployeePayrollException("Wrong name given", ExceptionType.WRONG_NAME);
		if (employeePayrollData == null)
			throw new EmployeePayrollException("No data found", ExceptionType.NO_DATA_FOUND);
	}

	public void addEmployeePayrollData(String name, Double salary, String department, String startDate, String gender)
			throws EmployeePayrollException {
		int result = employeePayrollDBService.insertNewEmployeeToDB(name, salary, department, startDate, gender);
		readEmployeePayrollData(IOService.DB_IO);
		EmployeePayrollData employeePayrollData = getEmployeePayrollData(name);
		if (result != 0 && employeePayrollData != null) {
			employeePayrollData.setName(name);
			employeePayrollData.setStartDate(LocalDate.parse(startDate));
			employeePayrollData.setSalary(salary);
		}
		if (result == 0)
			throw new EmployeePayrollException("Wrong name given", ExceptionType.WRONG_NAME);
		if (employeePayrollData == null)
			throw new EmployeePayrollException("No data found", ExceptionType.NO_DATA_FOUND);
	}

	public boolean isEmpPayrollSyncedWithDB(String name) throws EmployeePayrollException {
		try {
			return employeePayrollDBService.getEmployeePayrollDatas(name).get(0).equals(getEmployeePayrollData(name));
		} catch (IndexOutOfBoundsException e) {
			throw new EmployeePayrollException("No data found with that name", ExceptionType.NO_DATA_FOUND);
		}
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return employeePayrollList.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
	}
}
