package com.bl.emppayroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import com.mysql.cj.jdbc.Driver;

public class JDBCDemo {

	public static void main(String[] args) throws SQLException {

		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "****";
		Connection connection = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.print("Driver loaded: ");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("cannot find the driver");
		}
		listAllDrivers();

		try {
			System.out.println("Connecting to database: " + jdbcURL);
			connection = DriverManager.getConnection(jdbcURL, userName, password);
			System.out.println("Connected successfully: " + connection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PreparedStatement stmt = connection.prepareStatement("select * from employee_payroll");
		ResultSet resultSet = stmt.executeQuery();
		while (resultSet.next()) {
			System.out.println(
					resultSet.getInt("id") + " " + resultSet.getString("name") + " " + resultSet.getString("gender")
							+ " " + resultSet.getDouble("basic_pay") + " " + resultSet.getDate("start_date"));
		}
	}

	private static void listAllDrivers() {
		Enumeration<java.sql.Driver> driverList = DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driver = (Driver) driverList.nextElement();
			System.out.println(driver.getClass().getName());
		}
	}

}