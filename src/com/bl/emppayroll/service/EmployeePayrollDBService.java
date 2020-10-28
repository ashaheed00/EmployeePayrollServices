package com.bl.emppayroll.service;

import java.sql.*;
import java.time.LocalDate;
import java.sql.Date;
import java.util.*;

import com.bl.emppayroll.EmployeePayrollData;
import com.bl.emppayroll.exception.EmployeePayrollException;
import com.bl.emppayroll.exception.EmployeePayrollException.ExceptionType;

public class EmployeePayrollDBService {

	private PreparedStatement empPreparedStatement;
	private static EmployeePayrollDBService employeePayrollDBService;

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "my964sql!!";
		return DriverManager.getConnection(jdbcURL, userName, password);
	}

	public List<EmployeePayrollData> readData() {
		String sql = String.format("select * from employee_payroll");
		return getEmployeePayrollList(sql);
	}

	public List<EmployeePayrollData> getEmployeePayrollDatas(String name) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		if (empPreparedStatement == null)
			prepareStatementForEmployeeData();
		try {
			empPreparedStatement.setString(1, name);
			ResultSet resultSet = empPreparedStatement.executeQuery();
			employeePayrollList = getEmployeePayrollList(resultSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public List<EmployeePayrollData> getEmployeesForDateRange(LocalDate startDate, LocalDate endDate)
			throws EmployeePayrollException {
		String sql = String.format("SELECT * FROM employee_payroll WHERE start_date BETWEEN '%s' AND '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return getEmployeePayrollList(sql);
	}

	public int updateSalaryUsingSQL(String name, Double salary) throws EmployeePayrollException {
		String sql = "UPDATE employee_payroll SET basic_pay = ?  WHERE name = ? ";
		try (Connection connection = getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new EmployeePayrollException("Wrong SQL query given", ExceptionType.WRONG_SQL);
		}
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name = ?;";
			empPreparedStatement = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<EmployeePayrollData> getEmployeePayrollList(ResultSet resultSet) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				employeePayrollList.add(new EmployeePayrollData(resultSet.getInt("id"), resultSet.getString("name"),
						resultSet.getDouble("basic_pay"), resultSet.getDate("start_date")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmployeePayrollList(String sql) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				employeePayrollList.add(new EmployeePayrollData(resultSet.getInt("id"), resultSet.getString("name"),
						resultSet.getDouble("basic_pay"), resultSet.getDate("start_date")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public Map<String, Double> getEmpDataGroupedByGender(String column, String operation) {
		Map<String, Double> dataByGenderMap = new HashMap<>();
		String sql = String.format("SELECT gender, %s(%s) FROM employee_payroll GROUP BY gender;", operation, column);
		try (Connection connection = getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				dataByGenderMap.put(resultSet.getString(1), resultSet.getDouble(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataByGenderMap;
	}

	public int insertNewEmployeeToDB(String name, Double salary, String department, String startDate, String gender)
			throws EmployeePayrollException {
		String sql = String.format(
				"INSERT INTO employee_payroll (name, basic_pay, department, start_date, gender,deductions,taxable_pay,tax,net_pay) VALUES ('%s','%s','%s','%s','%s',0,0,0,0);",
				name, salary, department, startDate, gender);
		try (Connection connection = getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new EmployeePayrollException("Wrong SQL or field given", ExceptionType.WRONG_SQL);
		}
	}
}
