package com.bl.emppayroll;

import static org.junit.Assert.*;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.bl.emppayroll.exception.EmployeePayrollException;
import com.bl.emppayroll.service.EmployeePayrollService;
import com.bl.emppayroll.service.EmployeePayrollService.IOService;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

public class EmployeePayrollJDBCTest {
	EmployeePayrollService empPayRollService;
	List<EmployeePayrollData> empPayrollList;
	Map<String, Double> empPayrollDataByGenderMap;

	@Before
	public void initialize() {
		empPayRollService = new EmployeePayrollService();
		empPayrollList = empPayRollService.readEmployeePayrollData(IOService.DB_IO);
	}

	@Ignore
	@Test
	public void givenEmpPayrollDB_WhenRetrieved_ShouldMatchEmpCount() {
		assertEquals(5, empPayrollList.size());
	}

	@Ignore
	@Test
	public void givenNewSalary_WhenUpdated_ShouldSyncWithDB() {
		try {
			empPayRollService.updateEmployeeSalary("Terisa", 3000000.0);
		} catch (EmployeePayrollException e) {
			System.err.println(e.getMessage());
		}
		boolean isSynced = false;
		try {
			isSynced = empPayRollService.isEmpPayrollSyncedWithDB("Terisa");
		} catch (EmployeePayrollException e) {
			System.out.println(e.getMessage());
		}
		assertTrue(isSynced);
	}

	@Ignore
	@Test
	public void givenDateRange_WhenRetrievedEmployee_ShouldReturnEmpCount() throws EmployeePayrollException {
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate enDate = LocalDate.now();
		empPayrollList = empPayRollService.getEmpPayrollDataForDateRange(startDate, enDate);
		assertEquals(4, empPayrollList.size());
	}

	@Ignore
	@Test
	public void givenEmployeeDB_WhenRetrievedSum_ShouldReturnSumByGender() throws EmployeePayrollException {
		empPayrollDataByGenderMap = empPayRollService.getSumOfDataGroupedByGender(IOService.DB_IO, "basic_pay");
		assertEquals(4000000, empPayrollDataByGenderMap.get("M"), 0.0);
		assertEquals(7800000, empPayrollDataByGenderMap.get("F"), 0.0);
	}

	@Ignore
	@Test
	public void givenEmployeeDB_WhenRetrievedAvg_ShouldReturnAvgByGender() throws EmployeePayrollException {
		empPayrollDataByGenderMap = empPayRollService.getAvgOfDataGroupedByGender(IOService.DB_IO, "basic_pay");
		assertEquals(2000000, empPayrollDataByGenderMap.get("M"), 0.0);
		assertEquals(2600000, empPayrollDataByGenderMap.get("F"), 0.0);
	}

	@Ignore
	@Test
	public void givenEmployeeDB_WhenRetrievedMaxMin_ShouldReturnMaxByGender() throws EmployeePayrollException {
		empPayrollDataByGenderMap = empPayRollService.getMaxOfDataGroupedByGender(IOService.DB_IO, "basic_pay");
		assertEquals(2500000, empPayrollDataByGenderMap.get("M"), 0.0);
		assertEquals(3000000, empPayrollDataByGenderMap.get("F"), 0.0);
	}

	@Ignore
	@Test
	public void givenEmployeeDB_WhenRetrievedCount_ShouldReturnCountByGender() throws EmployeePayrollException {
		empPayrollDataByGenderMap = empPayRollService.getCountOfDataGroupedByGender(IOService.DB_IO, "id");
		assertEquals(2, empPayrollDataByGenderMap.get("M"), 0.0);
		assertEquals(3, empPayrollDataByGenderMap.get("F"), 0.0);
	}

	@Ignore
	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws EmployeePayrollException {
		empPayRollService.readEmployeePayrollData(IOService.DB_IO);
		empPayRollService.addEmployeePayrollData("Mark", 2000000.00, "2016-02-01", "M");
		boolean isSynced = empPayRollService.isEmpPayrollSyncedWithDB("Mark");
		assertTrue(isSynced);
	}

	@Ignore
	@Test
	public void givenNewEmployee_WhenAddedInTwoTables_ShouldSyncWithDB() throws EmployeePayrollException {
		empPayRollService.readEmployeePayrollData(IOService.DB_IO);
		// empPayRollService.addEmployeeAndPayrollData("Mark", 2000000.00, "2016-02-01",
		// "M");
		boolean isSynced = empPayRollService.isEmpPayrollSyncedWithDB("Mark");
		assertTrue(isSynced);
	}

	@Ignore
	@Test
	public void givenNewEmployee_WhenAddedUsingER_ShouldSyncWithDB() throws EmployeePayrollException {
		empPayRollService.readEmployeePayrollData(IOService.DB_IO);
		List<String> depts = new ArrayList<>();
		depts.add("Sales");
		depts.add("Marketing");
		empPayRollService.addEmployeeAndPayrollData("Mark", 2000000.00, "2016-02-01", "M", 501, depts);
		boolean isSynced = empPayRollService.isEmpPayrollSyncedWithDB("Mark");
		assertTrue(isSynced);
	}

	@Ignore
	@Test
	public void givenEmployeeId_WhenDeletedUsing_ShouldSyncWithDB() throws EmployeePayrollException {
		empPayRollService.removeEmployee(5);
		assertEquals(5, empPayrollList.size());
	}

	@Test
	public void given4Employees_WhenAdded_ShouldMatchEmpEntries() throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>() {
			{
				add(new EmployeePayrollData(0, "Kalyan", 1000000, Date.valueOf(LocalDate.now()), "M", 501));
				add(new EmployeePayrollData(0, "Rashmi", 1000000, Date.valueOf(LocalDate.now()), "F", 501));
				add(new EmployeePayrollData(0, "Sharad", 2000000, Date.valueOf(LocalDate.now()), "M", 501));
				add(new EmployeePayrollData(0, "Nancy", 1500000, Date.valueOf(LocalDate.now()), "F", 501));
			}
		};

		empPayRollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		empPayRollService.addEmployeePayrollData(employeePayrollDataList);
		Instant end = Instant.now();
		System.err.println("Duration without Thread: " + Duration.between(start, end).toMillis() + " ms");
		start = Instant.now();
		empPayRollService.addEmployeePayrollDataWithThreads(employeePayrollDataList);
		end = Instant.now();
		System.err.println("Duration with Thread: " + Duration.between(start, end).toMillis() + " ms");
		assertEquals(13, empPayRollService.readEmployeePayrollData(IOService.DB_IO).size());
	}
}
