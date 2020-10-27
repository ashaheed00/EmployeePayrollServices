package com.bl.emppayroll;

import java.sql.*;
import java.util.*;

public class EmployeePayrollDBService {

	String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
	String userName = "root";
	String password = "my964sql!!";
	Connection connection;

	public List<EmployeePayrollData> readData() {
		String sql = "select * from employee_payroll";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			connection = DriverManager.getConnection(jdbcURL, userName, password);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				employeePayrollList.add(new EmployeePayrollData(resultSet.getInt("id"), resultSet.getString("name"),
						resultSet.getDouble("basic_pay"), resultSet.getDate("start_date")));
			}
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
}
