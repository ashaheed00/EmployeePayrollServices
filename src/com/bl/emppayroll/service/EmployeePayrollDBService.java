package com.bl.emppayroll.service;

import java.sql.*;
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
		String sql = "select * from employee_payroll";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Statement statement = getConnection().createStatement();) {
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = getEmployeePayrollList(resultSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeePayrollList;
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
			String sql = "SELECT * FROM employee_payroll WHERE name = ?";
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
}
