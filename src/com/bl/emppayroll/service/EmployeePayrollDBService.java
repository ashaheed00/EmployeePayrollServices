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
	private int connectionCounter;

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	private synchronized Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "my964sql!!";
		Connection connection;
		connectionCounter++;
		System.out
				.println("Proccessing Thraed: " + Thread.currentThread().getName() + " with ID: " + connectionCounter);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Proccessing Thraed: " + Thread.currentThread().getName() + " with ID: " + connectionCounter
				+ " is successful!");
		return connection;
	}

	public List<EmployeePayrollData> readData() {
		String sql = String.format("select * from employee_payroll where active=true");
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
			String sql = "SELECT * FROM employee_payroll WHERE name = ? and active=true;";
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

	public int insertNewEmployeeToDB(String name, Double salary, String startDate, String gender)
			throws EmployeePayrollException {
		String sql = String.format(
				"INSERT INTO employee_payroll (name, basic_pay, start_date, gender) VALUES ('%s','%s','%s','%s');",
				name, salary, startDate, gender);

		try (Connection connection = getConnection()) {
			empPreparedStatement = connection.prepareStatement(sql);
			return empPreparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeePayrollException("Wrong SQL or field given", ExceptionType.WRONG_SQL);
		} finally {
			try {
				empPreparedStatement.close();
			} catch (SQLException e) {
			}
		}
	}

	public EmployeePayrollData addNewEmployeeToDB(String name, Double salary, String startDate, String gender,
			int companyId, ArrayList<String> department) throws EmployeePayrollException {
		EmployeePayrollData employeePayrollData = null;
		int empId = -1;
		Connection connection = null;
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO employee_payroll (name, basic_pay, start_date, gender,company_id) VALUES ('%s','%s','%s','%s','%s');",
					name, salary, startDate, gender, companyId);
			int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					empId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		try (Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO departments (emp_id,department) VALUES ('%s','%s');", empId,
					department);
			int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					empId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = taxablePay - tax;
			String sql = String.format(
					"INSERT INTO payroll_details (emp_id,basic_pay,deductions,taxable_pay,tax,net_pay) VALUES ('%s','%s','%s','%s','%s','%s');",
					empId, salary, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayrollData(empId, name, salary, Date.valueOf(startDate), gender,
						companyId, department);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return employeePayrollData;
	}

	public void removeEmployeeFromDB(int empId) throws EmployeePayrollException {
		String sql = String.format("UPDATE employee_payroll SET active = false WHERE id = '%s'", empId);
		try (Connection connection = getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new EmployeePayrollException("Wrong SQL or field given", ExceptionType.WRONG_SQL);
		}
	}
}
