package com.bl.emppayroll;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.bl.emppayroll.exception.EmployeePayrollException;
import com.bl.emppayroll.service.EmployeePayrollService;
import com.bl.emppayroll.service.EmployeePayrollService.IOService;

public class EmployeePayrollJDBCTest {
	EmployeePayrollService empPayRollService;
	List<EmployeePayrollData> empPayrollList;
	Map<String, Double> empPayrollDataByGenderMap;

	@Before
	public void initialize() {
		empPayRollService = new EmployeePayrollService();
		empPayrollList = empPayRollService.readEmployeePayrollData(IOService.DB_IO);
	}

	@Test
	public void givenEmpPayrollDB_WhenRetrieved_ShouldMatchEmpCount() {
		assertEquals(5, empPayrollList.size());
	}

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

	@Test
	public void givenDateRange_WhenRetrievedEmployee_ShouldReturnEmpCount() throws EmployeePayrollException {
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate enDate = LocalDate.now();
		empPayrollList = empPayRollService.getEmpPayrollDataForDateRange(startDate, enDate);
		assertEquals(4, empPayrollList.size());
	}

	@Test
	public void givenEmployeeDB_WhenRetrievedSum_ShouldReturnSumByGender() throws EmployeePayrollException {
		empPayrollDataByGenderMap = empPayRollService.getSumOfDataGroupedByGender(IOService.DB_IO, "basic_pay");
		assertEquals(4000000, empPayrollDataByGenderMap.get("M"), 0.0);
		assertEquals(7800000, empPayrollDataByGenderMap.get("F"), 0.0);
	}
	
	@Test
	public void givenEmployeeDB_WhenRetrievedAvg_ShouldReturnSumByGender() throws EmployeePayrollException {
		empPayrollDataByGenderMap = empPayRollService.getAvgOfDataGroupedByGender(IOService.DB_IO, "basic_pay");
		assertEquals(2000000, empPayrollDataByGenderMap.get("M"), 0.0);
		assertEquals(2600000, empPayrollDataByGenderMap.get("F"), 0.0);
	}

}
